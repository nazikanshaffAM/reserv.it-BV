package Model;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int retrievalRate;
    private final int customerId;

    public Customer(TicketPool ticketPool, int retrievalRate, int customerId) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        while (ticketPool.hasTicketsLeft()) {
            Ticket ticket = ticketPool.buyTicket();
            if (ticket != null) {
                System.out.println("Customer " + customerId + " purchased Ticket ID " + ticket.getTicketId() +
                        " from Vendor " + ticket.getVendorId() + ".");
            }
            try {
                Thread.sleep(retrievalRate * 1000L);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

