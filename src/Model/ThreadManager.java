package Model;

import java.util.ArrayList;
import java.util.List;

public class ThreadManager {
    private final TicketPool ticketPool;
    private final Configuration configuration;
    private final List<Thread> vendorThreads = new ArrayList<>();
    private final List<Thread> customerThreads = new ArrayList<>();

    public ThreadManager(TicketPool ticketPool, Configuration configuration) {
        this.ticketPool = ticketPool;
        this.configuration = configuration;
    }

    public void startVendorThreads() {
        int baseTicketsPerVendor = configuration.totalTickets / configuration.numberOfVendors;
        int extraTickets = configuration.totalTickets % configuration.numberOfVendors;

        for (int i = 0; i < configuration.numberOfVendors; i++) {
            int ticketsToProduce = baseTicketsPerVendor + (i < extraTickets ? 1 : 0);
            Vendor vendor = new Vendor(ticketPool, ticketsToProduce, configuration.ticketReleaseRate, i + 1);
            Thread vendorThread = new Thread(vendor, "Vendor-" + (i + 1));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }
    }

    public void startCustomerThreads() {
        for (int i = 0; i < configuration.numberOfCustomers; i++) {
            Customer customer = new Customer(ticketPool, configuration.customerRetrievalRate, i + 1);
            Thread customerThread = new Thread(customer, "Customer-" + (i + 1));
            customerThreads.add(customerThread);
            customerThread.start();
        }
    }

    public void waitForCompletion() {
        for (Thread vendorThread : vendorThreads) {
            try {
                vendorThread.join();
            } catch (InterruptedException ignored) {
            }
        }
        System.out.println("All tickets are released.");

        for (Thread customerThread : customerThreads) {
            try {
                customerThread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }
}

