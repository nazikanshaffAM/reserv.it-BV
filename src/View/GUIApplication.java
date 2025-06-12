package View;

import Controller.Main;
import Model.TicketPool;
import Model.Configuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GUIApplication extends Application {

    private TextField eventNameField, ticketPriceField, totalTicketsField, releaseRateField,
            retrievalRateField, maxCapacityField, vendorCountField, customerCountField;
    private TextArea outputArea;
    private boolean isRunning = false;
    private Configuration configuration;
    private TicketPool ticketPool;
    private ExecutorService executorService;



    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Real-Time Event Ticketing System");

        // Initial Page Layout
        VBox initialLayout = new VBox(20);
        initialLayout.setStyle("-fx-background-color: #1e1e2f;");
        initialLayout.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to The System");
        welcomeLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button configureButton = new Button("Explore");
        configureButton.setStyle("-fx-background-radius: 20px; -fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 18px;");
        configureButton.setOnAction(event -> openConfigurationWindow(primaryStage));

        configureButton.setOnMouseEntered(event -> configureButton.setStyle("-fx-background-radius: 20px; -fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px;"));
        configureButton.setOnMouseExited(event -> configureButton.setStyle("-fx-background-radius: 20px; -fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 18px;"));

        initialLayout.getChildren().addAll(welcomeLabel, configureButton);

        Scene mainScene = new Scene(initialLayout, 900, 700);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void openConfigurationWindow(Stage primaryStage) {
        // Input Grid
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setHgap(15);
        inputGrid.setVgap(20);
        inputGrid.setStyle("-fx-background-color: #25283d; -fx-border-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 2, 2);");

        // Input Fields
        eventNameField = createLabeledTextField("Enter Event Name:", inputGrid, 0);
        ticketPriceField = createLabeledTextField("Ticket Price:", inputGrid, 1);
        totalTicketsField = createLabeledTextField("Total Tickets:", inputGrid, 2);
        releaseRateField = createLabeledTextField("Ticket Release Rate (s):", inputGrid, 3);
        retrievalRateField = createLabeledTextField("Customer Retrieval Rate (s):", inputGrid, 4);
        maxCapacityField = createLabeledTextField("Max Ticket pool Capacity:", inputGrid, 5);
        vendorCountField = createLabeledTextField("Vendor Count:", inputGrid, 6);
        customerCountField = createLabeledTextField("Customer Count:", inputGrid, 7);

        // Output Area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefWidth(500);
        outputArea.setPrefHeight(300);
        outputArea.setStyle("-fx-border-radius: 5px; -fx-border-color: #3498db; -fx-background-color: #2b2f3e; -fx-text-fill: black; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 3, 3);");

        // Layout Adjustment
        VBox leftSide = new VBox(inputGrid);
        leftSide.setPadding(new Insets(15));
        leftSide.setSpacing(15);

        // Output is now placed below the input grid
        VBox rightSide = new VBox(outputArea);
        rightSide.setPadding(new Insets(15));
        rightSide.setSpacing(15);

        // Main Layout
        HBox mainLayout = new HBox(15, leftSide, rightSide);
        mainLayout.setPadding(new Insets(15));

        Button startButton = createStyledButton("Start", "#3498db");
        Button stopButton = createStyledButton("Stop", "#e74c3c");
        Button exitButton = createStyledButton("Exit", "#e74c3c");
        stopButton.setDisable(true);

        HBox buttonBox = new HBox(10, startButton, stopButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        VBox fullLayout = new VBox(mainLayout, buttonBox);
        fullLayout.setPadding(new Insets(15));
        fullLayout.setSpacing(30);
        fullLayout.setAlignment(Pos.CENTER);

        startButton.setOnAction(event -> startTicketing(stopButton, startButton));
        stopButton.setOnAction(event -> stopTicketing(stopButton, startButton));
        exitButton.setOnAction(event -> Platform.exit()); // Exit button action

        Scene configScene = new Scene(fullLayout, 1000, 600);
        primaryStage.setScene(configScene);
    }

    private TextField createLabeledTextField(String labelText, GridPane grid, int rowIndex) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #95a5a6;");
        textField.setStyle("-fx-background-color: #2b2f3e; -fx-text-fill: #ecf0f1; -fx-border-color: #3498db; -fx-border-radius: 5px; -fx-padding: 5px;");
        grid.add(label, 0, rowIndex);
        grid.add(textField, 1, rowIndex);
        return textField;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-radius: 20px; -fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px;");
        return button;
    }

    private void startTicketing(Button stopButton, Button startButton) {
        if (isRunning) {
            showAlert("Error", "Simulation is already running.");
            return;
        }

        try {
            // Validate Inputs
            String eventName = validateTextInput(eventNameField, "Event Name");
            double ticketPrice = validateDoubleInput(ticketPriceField, "Ticket Price");
            int totalTickets = validateIntegerInput(totalTicketsField, "Total Tickets");
            int ticketReleaseRate = validateIntegerInput(releaseRateField, "Ticket Release Rate");
            int customerRetrievalRate = validateIntegerInput(retrievalRateField, "Customer Retrieval Rate");
            int maxTicketCapacity = validateIntegerInput(maxCapacityField, "Max Pool Capacity");
            int numberOfVendors = validateIntegerInput(vendorCountField, "Vendor Count");
            int numberOfCustomers = validateIntegerInput(customerCountField, "Customer Count");

            // Create Configuration and TicketPool
            configuration = new Configuration(
                    eventName, ticketPrice, totalTickets, ticketReleaseRate,
                    customerRetrievalRate, maxTicketCapacity, numberOfVendors, numberOfCustomers
            );

            ticketPool = new TicketPool(
                    configuration.maxTicketCapacity,
                    configuration.totalTickets,
                    configuration.eventName,
                    configuration.ticketPrice
            );

            // Create ExecutorService to manage threads
            executorService = Executors.newCachedThreadPool();

            // Start Simulation in a separate thread
            executorService.submit(() -> {
                try {
                    Main.executeOperations(configuration, ticketPool);
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        outputMessage("Error during simulation: " + e.getMessage());
                        stopTicketing(stopButton, startButton);
                    });
                }
            });

            outputMessage("Ticketing started...");
            startButton.setDisable(true);
            stopButton.setDisable(false);
            isRunning = true;

        } catch (IllegalArgumentException e) {
            showAlert("Input Error", e.getMessage());
        }
    }

    private void stopTicketing(Button stopButton, Button startButton) {
        if (!isRunning) {
            showAlert("Error", "Simulation is not running.");
            return;
        }

        try {
            // Shutdown executor service
            if (executorService != null) {
                executorService.shutdownNow();
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            }

            outputMessage("Ticketing stopped.");
            isRunning = false;
            startButton.setDisable(false);
            stopButton.setDisable(true);

        } catch (InterruptedException e) {
            outputMessage("Error stopping simulation: " + e.getMessage());
        }
    }

    private void outputMessage(String message) {
        Platform.runLater(() -> outputArea.appendText(message + "\n"));
    }

    private int validateIntegerInput(TextField field, String fieldName) {
        try {
            int value = Integer.parseInt(field.getText());
            if (value <= 0) {
                throw new IllegalArgumentException(fieldName + " must be a positive integer.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input for " + fieldName + ". Please enter a valid integer.");
        }
    }

    private double validateDoubleInput(TextField field, String fieldName) {
        try {
            double value = Double.parseDouble(field.getText());
            if (value <= 0) {
                throw new IllegalArgumentException(fieldName + " must be a positive number.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input for " + fieldName + ". Please enter a valid number.");
        }
    }

    private String validateTextInput(TextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return text;
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
