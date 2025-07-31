# Event Ticketing System (Java Multi-threaded)

A concurrent **Java-based event ticketing system** that simulates real-time ticket sales and releases using multi-threading and synchronization. Developed as part of a university coursework project to demonstrate concurrency control, data integrity, and interactive reporting in a simulated ticketing environment.

---

## Key Features

- Multi-threaded design with real-time simulation
- Ticket release by multiple vendor threads
- Ticket purchase by multiple customer threads
- Synchronization to prevent race conditions
- Real-time reporting of available, sold, and remaining tickets
- Manual GUI or CLI-based input option (optional)
- Designed to showcase concurrency control and thread safety

---

## Technologies Used

- **Language**: Java
- **Concepts**: Multithreading, Synchronization, Shared Resources
- **Tools**: NetBeans / IntelliJ / Eclipse (any Java IDE)

---

## How to Run

1. Clone the repository:
```bash
git clone https://github.com/yourusername/event-ticketing-system.git
cd event-ticketing-system
```
2. Open the project in your Java IDE.

3. Compile and run the Main class (e.g., Main.java).

## How It Works

-TicketPool: A shared object that holds the number of available tickets.
-VendorThread: Releases tickets into the pool (e.g., adds 5 tickets every 10 seconds).
-CustomerThread: Attempts to purchase tickets from the pool.
-Synchronized Methods: Ensure that no overselling or duplicate access occurs.

## Author
Anshaff Ameer


