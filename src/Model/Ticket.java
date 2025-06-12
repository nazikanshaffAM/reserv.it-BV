package Model;



public class Ticket {
    private final int ticketId;
    private final String eventName;
    private final double ticketPrice;
    private final int vendorId;

    public Ticket(int ticketId, String eventName, double ticketPrice, int vendorId) {
        this.ticketId = ticketId;
        this.eventName = eventName;
        this.ticketPrice = ticketPrice;
        this.vendorId = vendorId;
    }

    public int getTicketId() {

        return ticketId;
    }

    public int getVendorId() {

        return vendorId;
    }
}

