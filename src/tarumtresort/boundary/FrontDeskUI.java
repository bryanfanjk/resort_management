package tarumtresort.boundary;

import tarumtresort.adt.ListInterface;
import tarumtresort.control.FrontDeskController;
import tarumtresort.entity.Booking;
import tarumtresort.util.InputUtil;

/**
 * Boundary class handling front desk queries and binary search tree searches.
 * Displays search timings and results.
 * 
 * @author Admin
 */
public class FrontDeskUI {

    private final FrontDeskController controller;

    public FrontDeskUI(FrontDeskController controller) {
        this.controller = controller;
    }

    public void start() {
        while (true) {
            InputUtil.displayHeader("Front-Desk Service & Reservation Search");
            System.out.println("1. Retrieve Guest Details (8-Digit Search)");
            System.out.println("2. Query Booking Records in Range (BST Range Query)");
            System.out.println("3. Display All System Bookings (BST In-Order Traversal)");
            System.out.println("4. Back to Main Menu");

            int choice = InputUtil.readInt("Enter choice (1-4): ", 1, 4);
            switch (choice) {
                case 1:
                    searchSingleBooking();
                    break;
                case 2:
                    searchRangeBookings();
                    break;
                case 3:
                    displayAllBookings();
                    break;
                case 4:
                    return;
            }
        }
    }

    private void searchSingleBooking() {
        System.out.println("\n--- Retrieve Guest Information ---");
        String code = InputUtil.readConfirmationNumber("Enter 8-digit confirmation code (e.g. 00384729 or VIP00123): ");

        long startTime = System.nanoTime();
        Booking booking = controller.searchBooking(code);
        long endTime = System.nanoTime();

        if (booking != null) {
            System.out.println("\nSUCCESS: Reservation Retrieved!");
            System.out.printf("Search execution time: %d ns (O(log n) BST lookup)\n\n", (endTime - startTime));
            System.out.println("=========================================================================");
            System.out.printf(" Confirmation: %-15s | Status: %s\n", booking.getConfirmationNumber(), booking.getStatus().getLabel());
            System.out.printf(" Guest Name:   %-15s | Contact: %s\n", booking.getGuest().getName(), booking.getGuest().getContactNumber());
            System.out.printf(" Member Tier:  %-15s | VIP Status: %s\n", booking.getGuest().getTier().getLabel(), booking.isVIP() ? "YES" : "NO");
            System.out.printf(" Room Requested: %-14s | Room Allocated: %s\n", 
                    booking.getRequestedRoomType().getLabel(), 
                    (booking.getAllocatedRoom() != null) ? "Room " + booking.getAllocatedRoom().getRoomNumber() : "None (Pending Allocation)");
            System.out.printf(" Reservation Date: %-12s | Queue Sequence: %d\n", booking.getBookingDate(), booking.getBookingIndex());
            System.out.println("=========================================================================");
        } else {
            System.out.println("\nERROR: No reservation records match that confirmation number.");
        }
        InputUtil.pressEnterToContinue();
    }

    private void searchRangeBookings() {
        System.out.println("\n--- Reservation Code Range Query ---");
        String startCode = InputUtil.readConfirmationNumber("Enter START confirmation number: ");
        String endCode = InputUtil.readConfirmationNumber("Enter END confirmation number: ");

        long startTime = System.nanoTime();
        ListInterface<Booking> results = controller.searchBookingsByRange(startCode, endCode);
        long endTime = System.nanoTime();

        System.out.printf("\nSearch completed in %d ns. Found %d bookings in range.\n", (endTime - startTime), results.getLength());
        if (results.isEmpty()) {
            System.out.println("No records found in that confirmation range.");
        } else {
            displayBookingTable(results);
        }
        InputUtil.pressEnterToContinue();
    }

    private void displayAllBookings() {
        System.out.println("\n--- All Reservations (BST In-Order traversal) ---");
        ListInterface<Booking> all = controller.getAllBookingsSorted();
        if (all.isEmpty()) {
            System.out.println("No reservations logged in the system.");
        } else {
            displayBookingTable(all);
        }
        InputUtil.pressEnterToContinue();
    }

    private void displayBookingTable(ListInterface<Booking> bookings) {
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-16s | %-12s | %-14s | %-10s | %-20s\n", 
                "Conf Code", "Guest Name", "Loyalty Tier", "Requested Type", "Room Alloc", "Booking Status");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        for (int i = 1; i <= bookings.getLength(); i++) {
            Booking b = bookings.getEntry(i);
            String roomStr = (b.getAllocatedRoom() != null) ? "Room " + b.getAllocatedRoom().getRoomNumber() : "Pending";
            System.out.printf("%-12s | %-16s | %-12s | %-14s | %-10s | %-20s\n",
                    b.getConfirmationNumber(), b.getGuest().getName(), b.getGuest().getTier().getLabel(), 
                    b.getRequestedRoomType().getLabel(), roomStr, b.getStatus().getLabel());
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }
}
