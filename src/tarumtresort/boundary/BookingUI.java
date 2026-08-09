package tarumtresort.boundary;

import tarumtresort.adt.LinkedQueue;
import tarumtresort.adt.ListInterface;
import tarumtresort.control.BookingController;
import tarumtresort.entity.Booking;
import tarumtresort.entity.Guest;
import tarumtresort.entity.Room;
import tarumtresort.util.InputUtil;

/**
 * Boundary class handling interactions for standard walk-in bookings and FIFO allocation.
 * 
 * @author Admin
 */
public class BookingUI {

    private final BookingController controller;
    private final LinkedQueue<Booking> queue;

    public BookingUI(BookingController controller, LinkedQueue<Booking> queue) {
        this.controller = controller;
        this.queue = queue;
    }

    public void start() {
        while (true) {
            InputUtil.displayHeader("Walk-in Registrations & Standard Booking");
            System.out.println("1. Register New Walk-In Standard Booking");
            System.out.println("2. Allocate Room to Next Queue Member (FIFO)");
            System.out.println("3. Expedite Queue Member (Move to Front)");
            System.out.println("4. View Current Standard Queue");
            System.out.println("5. Back to Main Menu");
            
            int choice = InputUtil.readInt("Enter choice (1-5): ", 1, 5);
            switch (choice) {
                case 1:
                    registerBooking();
                    break;
                case 2:
                    allocateRoom();
                    break;
                case 3:
                    expediteQueue();
                    break;
                case 4:
                    viewQueue();
                    break;
                case 5:
                    return;
            }
        }
    }

    private void registerBooking() {
        System.out.println("\n--- Register Walk-in Guest ---");
        String name = InputUtil.readString("Enter Guest Name: ");
        String contact = InputUtil.readContactNumber("Enter Contact Number: ");
        
        System.out.println("Select Requested Room Type:");
        System.out.println("1. Standard ($150.00)");
        System.out.println("2. Deluxe ($280.00)");
        System.out.println("3. Suite ($500.00)");
        System.out.println("4. Penthouse ($1200.00)");
        int typeChoice = InputUtil.readInt("Select choice (1-4): ", 1, 4);
        Room.RoomType requestedType = Room.RoomType.values()[typeChoice - 1];
        
        String date = InputUtil.readDate("Enter Booking Date (YYYY-MM-DD): ");

        // Create standard guest (Standard Tier)
        Guest guest = new Guest(name, contact);
        
        Booking booking = controller.registerStandardBooking(guest, requestedType, date);
        System.out.println("\nSUCCESS: Booking Registered!");
        System.out.printf("Confirmation Number: %s\n", booking.getConfirmationNumber());
        System.out.printf("Position in queue: %d\n", queue.size());
        InputUtil.pressEnterToContinue();
    }

    private void allocateRoom() {
        System.out.println("\n--- Allocate Standard Room ---");
        if (queue.isEmpty()) {
            System.out.println("Queue is empty. No bookings to allocate.");
            InputUtil.pressEnterToContinue();
            return;
        }

        Booking nextBooking = queue.getFront();
        System.out.printf("Processing queue head: %s (%s) requesting %s...\n",
                nextBooking.getGuest().getName(), nextBooking.getConfirmationNumber(), nextBooking.getRequestedRoomType().getLabel());

        Booking allocated = controller.allocateNextStandardRoom();
        if (allocated != null) {
            System.out.printf("SUCCESS: Allocated Room %d to Guest %s!\n",
                    allocated.getAllocatedRoom().getRoomNumber(), allocated.getGuest().getName());
        } else {
            System.out.println("ALLOCATION FAILED: No vacant, clean rooms of the requested type available.");
            System.out.println("Please clean a room or adjust request.");
        }
        InputUtil.pressEnterToContinue();
    }

    private void expediteQueue() {
        System.out.println("\n--- Expedite Booking Queue ---");
        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            InputUtil.pressEnterToContinue();
            return;
        }

        String confCode = InputUtil.readConfirmationNumber("Enter 8-digit confirmation code to expedite: ");
        boolean success = controller.expediteBooking(confCode);
        if (success) {
            System.out.println("SUCCESS: Guest has been moved to the absolute front of the standard queue!");
        } else {
            System.out.println("EXPEDITE FAILED: Confirmation number not found in standard pending queue.");
        }
        InputUtil.pressEnterToContinue();
    }

    private void viewQueue() {
        System.out.println("\n--- Standard Booking Queue (FIFO Order) ---");
        if (queue.isEmpty()) {
            System.out.println("[Queue is currently empty]");
            InputUtil.pressEnterToContinue();
            return;
        }

        ListInterface<Booking> list = queue.toList();
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.printf("%-4s | %-12s | %-16s | %-16s | %-15s\n", "Pos", "Conf Number", "Guest Name", "Room Type", "Status");
        System.out.println("---------------------------------------------------------------------------------------");
        for (int i = 1; i <= list.getLength(); i++) {
            Booking b = list.getEntry(i);
            System.out.printf("%-4d | %-12s | %-16s | %-16s | %-15s\n",
                    i, b.getConfirmationNumber(), b.getGuest().getName(), b.getRequestedRoomType().getLabel(), b.getStatus().getLabel());
        }
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.printf("Total pending standard reservations: %d\n", queue.size());
        InputUtil.pressEnterToContinue();
    }
}
