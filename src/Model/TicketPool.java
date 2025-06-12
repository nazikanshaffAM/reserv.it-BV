package Model;


import java.util.LinkedList;
import java.util.Queue;

public class TicketPool {
    private final int maxCapacity;
    private final Queue<Ticket> ticketQueue;
    private int nextTicketId = 1;
    private int remainingTickets;
    private final String eventName;
    private final double ticketPrice;

    public TicketPool(int maxCapacity, int totalTickets, String eventName, double ticketPrice) {
        this.maxCapacity = maxCapacity;
        this.ticketQueue = new LinkedList<>();
        this.remainingTickets = totalTickets;
        this.eventName = eventName;
        this.ticketPrice = ticketPrice;
    }

    public synchronized void addTicket(int vendorId) {
        while (ticketQueue.size() >= maxCapacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                return;
            }
        }
        if (remainingTickets > 0) {
            Ticket ticket = new Ticket(nextTicketId++, eventName, ticketPrice, vendorId);
            remainingTickets--;
            ticketQueue.add(ticket);
            System.out.println("Vendor " + vendorId + " released Ticket ID " + ticket.getTicketId() + " to the ticket pool.");
            notifyAll();
        }
    }

    public synchronized Ticket buyTicket() {
        while (ticketQueue.isEmpty()) {
            if (remainingTickets <= 0) {
                return null;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        Ticket ticket = ticketQueue.poll();
        notifyAll();
        return ticket;
    }

    public synchronized boolean hasTicketsLeft() {

        return remainingTickets > 0 || !ticketQueue.isEmpty();
    }

    // Get the remaining tickets
    public synchronized int getRemainingTickets() {

        return remainingTickets;
    }

    // Add the new method to get the current pool size (number of tickets in the queue)
    public synchronized int getCurrentPoolSize() {
        return ticketQueue.size(); // Returns the number of tickets currently in the pool
    }
}
