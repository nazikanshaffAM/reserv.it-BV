package Model;



public class Configuration {
    public String eventName;
    public double ticketPrice;
    public int totalTickets;
    public int ticketReleaseRate;
    public int customerRetrievalRate;
    public int maxTicketCapacity;
    public int numberOfVendors;
    public int numberOfCustomers;

    public Configuration(String eventName, double ticketPrice, int totalTickets, double ticketReleaseRate,
                         double customerRetrievalRate, int maxTicketCapacity, int numberOfVendors, int numberOfCustomers) {
        this.eventName = eventName;
        this.ticketPrice = ticketPrice;
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = (int) ticketReleaseRate;
        this.customerRetrievalRate = (int) customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
        this.numberOfVendors = numberOfVendors;
        this.numberOfCustomers = numberOfCustomers;
    }
}

