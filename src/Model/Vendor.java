package Model;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int ticketsToProduce;
    private final int releaseRate;
    private final int vendorId;

    public Vendor(TicketPool ticketPool, int ticketsToProduce, int releaseRate, int vendorId) {
        this.ticketPool = ticketPool;
        this.ticketsToProduce = ticketsToProduce;
        this.releaseRate = releaseRate;
        this.vendorId = vendorId;
    }

    @Override
    public void run() {
        for (int i = 0; i < ticketsToProduce; i++) {
            ticketPool.addTicket(vendorId);
            try {
                Thread.sleep(releaseRate * 1000L);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

