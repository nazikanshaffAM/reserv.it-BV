package Controller;

import Model.Configuration;
import Model.Customer;
import Model.TicketPool;
import Model.Vendor;
import View.GUIApplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String CONFIGURATION_FILE = "Data.json";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Welcome, Prompt
        System.out.println("*****************************************");
        System.out.println("Welcome to the Ticket Management System!");
        System.out.println("------------------------------------------");
        System.out.println("This system helps manage event ticket sales and monitoring.");
        System.out.println("*****************************************\n");

        // Ask user if they want to start in CLI or GUI mode
        System.out.println("Please select whether you'd like to use the system in CLI or GUI mode.");
        System.out.print("Type 'CLI' or 'GUI': ");
        String mode = scanner.nextLine().trim().toLowerCase();

        // Ensure valid input
        while (!mode.equals("cli") && !mode.equals("gui")) {
            System.out.println("Invalid input. Please enter 'CLI' or 'GUI'.");
            System.out.print("Would you like to start the system in CLI or GUI mode? Enter 'CLI' or 'GUI': ");
            mode = scanner.nextLine().trim().toLowerCase();
        }

        if (mode.equals("cli")) {
            // Proceed with the configuration loading for CLI mode
            Configuration configuration = getConfig(scanner);

            TicketPool ticketPool = new TicketPool(
                    configuration.maxTicketCapacity,
                    configuration.totalTickets,
                    configuration.eventName,
                    configuration.ticketPrice
            );

            boolean isRunning = true;


            while (isRunning) {
                System.out.println("\nAvailable Commands:");
                System.out.println("1. start - Start the ticket operations");
                System.out.println("2. monitor - View real-time ticket status");
                System.out.println("3. stop - Stop the program");
                System.out.print("Enter your command (Type 'start','monitor' or 'stop'): ");

                String command = scanner.nextLine().trim().toLowerCase();

                switch (command) {
                    case "start":
                        executeOperations(configuration, ticketPool);
                        break;
                    case "monitor":
                        displayRealTimeStatus(ticketPool);
                        break;
                    case "stop":
                        System.out.println("Stopping the program...!");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid command. Please enter 'start', 'monitor', or 'stop'.");
                }
            }
        } else {
            // GUI Mode
            System.out.println("Starting GUI mode...");
            GUIApplication.launch(GUIApplication.class);
            
        }

        scanner.close();
    }

    public static void executeOperations(Configuration configuration, TicketPool ticketPool) {
        // Extracted method call for vendor threads
        List<Thread> vendorThreads = createVendorThreads(configuration, ticketPool);

        List<Thread> customerThreads = new ArrayList<>();
        for (int i = 0; i < configuration.numberOfCustomers; i++) {
            Customer customer = new Customer(ticketPool, configuration.customerRetrievalRate, i + 1);
            Thread customerThread = new Thread(customer, "Customer-" + (i + 1));
            customerThreads.add(customerThread);
            customerThread.start();
        }

        // Wait for all vendor threads to complete
        for (Thread vendorThread : vendorThreads) {
            try {
                vendorThread.join();
            } catch (InterruptedException ignored) {
            }
        }

        System.out.println("All tickets are released.");

        // Wait for all customer threads to complete
        for (Thread customerThread : customerThreads) {
            try {
                customerThread.join();
            } catch (InterruptedException ignored) {
            }
        }

        System.out.println("All tickets are sold.");
    }

    private static void displayRealTimeStatus(TicketPool ticketPool) {
        System.out.println("\nReal-Time Ticket Status:");
        System.out.println("Remaining Tickets: " + ticketPool.getRemainingTickets());
        System.out.println("Tickets in Pool: " + ticketPool.getCurrentPoolSize());
    }

    public static List<Thread> createVendorThreads(Configuration configuration, TicketPool ticketPool) {
        List<Thread> vendorThreads = new ArrayList<>();
        int baseTicketsPerVendor = configuration.totalTickets / configuration.numberOfVendors;
        int extraTickets = configuration.totalTickets % configuration.numberOfVendors;

        for (int i = 0; i < configuration.numberOfVendors; i++) {
            int ticketsToProduce = baseTicketsPerVendor + (i < extraTickets ? 1 : 0);
            Vendor vendor = new Vendor(ticketPool, ticketsToProduce, configuration.ticketReleaseRate, i + 1);
            Thread vendorThread = new Thread(vendor, "Vendor-" + (i + 1));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        return vendorThreads;
    }

    private static Configuration getConfig(Scanner scanner) {
        while (true) {
            if (new File(CONFIGURATION_FILE).exists()) {
                System.out.print("Do you want to load the previous configuration from JSON? (yes/no): ");
                String choice = scanner.nextLine().trim().toLowerCase();
                if ("yes".equals(choice)) {
                    Configuration configuration = loadConfig();
                    if (configuration != null) {
                        return configuration;
                    }
                    System.out.println("Failed to load configuration. Please enter new details.");
                } else if ("no".equals(choice)) {
                    Configuration newConfiguration = createNewConfig(scanner);
                    saveConfig(newConfiguration);
                    return newConfiguration;
                } else {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            } else {
                System.out.println("Configuration file not found. Creating a new configuration...");
                Configuration newConfiguration = createNewConfig(scanner);
                saveConfig(newConfiguration);
                return newConfiguration;
            }
        }
    }

    private static Configuration createNewConfig(Scanner scanner) {
        System.out.print("Enter the event name: ");
        String eventName = scanner.nextLine();

        double ticketPrice = getPositiveInt(scanner,"Enter ticket Price: ");
        int totalTickets = getPositiveInt(scanner, "Enter the total number of tickets: ");
        int ticketReleaseRate = getPositiveInt(scanner, "Enter the ticket release rate (/s): ");
        int customerRetrievalRate = getPositiveInt(scanner, "Enter the customer retrieval rate (/s): ");
        int maxTicketCapacity = getPositiveInt(scanner, "Enter the maximum ticket pool capacity: ");
        int numberOfVendors = getPositiveInt(scanner, "Enter the number of vendors: ");
        int numberOfCustomers = getPositiveInt(scanner, "Enter the number of customers: ");

        return new Configuration(eventName, ticketPrice, totalTickets, ticketReleaseRate,
                customerRetrievalRate, maxTicketCapacity, numberOfVendors, numberOfCustomers);
    }



    private static int getPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                if (value > 0) {
                    return value;
                } else {
                    System.out.println("Value must be a positive integer.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }
    }

    public static void saveConfig(Configuration configuration) {
        try (Writer writer = new FileWriter(CONFIGURATION_FILE)) {
            Gson gson = new Gson();
            gson.toJson(configuration, writer);
            System.out.println("Configuration saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save configuration: " + e.getMessage());
        }
    }

    public static Configuration loadConfig() {
        try (Reader reader = new FileReader(CONFIGURATION_FILE)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Configuration.class);
        } catch (IOException | JsonSyntaxException e) {
            System.out.println("Error reading configuration: " + e.getMessage());
            return null;
        }
    }


}