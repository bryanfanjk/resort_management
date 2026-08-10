package tarumtresort.boundary;

import tarumtresort.adt.ListInterface;
import tarumtresort.control.FrontDeskController;
import tarumtresort.entity.Booking;
import tarumtresort.entity.Guest;
import tarumtresort.entity.Room;
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
            System.out.println("4. Query Room Availability Summary");
            System.out.println("5. View Billing Details & Invoice");
            System.out.println("6. Back to Main Menu");

            int choice = InputUtil.readInt("Enter choice (1-6): ", 1, 6);
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
                    queryRoomAvailability();
                    break;
                case 5:
                    viewBillingDetails();
                    break;
                case 6:
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
            System.out.printf(" Stay Duration: %-13s | Reservation Date: %s\n", booking.getNights() + " night(s)", booking.getBookingDate());
            System.out.printf(" Queue Sequence: %-14d | Loyalty Points: %d pts\n", booking.getBookingIndex(), booking.getGuest().getLoyaltyPoints());
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

    private void queryRoomAvailability() {
        System.out.println("\n--- Query Room Availability Summary ---");
        ListInterface<Room> rooms = controller.getRooms();
        if (rooms == null || rooms.isEmpty()) {
            System.out.println("No rooms registered in the system.");
            InputUtil.pressEnterToContinue();
            return;
        }

        // Initialize counters based on RoomType enum values
        int[] total = new int[Room.RoomType.values().length];
        int[] vacantReady = new int[Room.RoomType.values().length];
        int[] vacantNotReady = new int[Room.RoomType.values().length];
        int[] occupied = new int[Room.RoomType.values().length];

        for (int i = 1; i <= rooms.getLength(); i++) {
            Room r = rooms.getEntry(i);
            int typeIdx = r.getRoomType().ordinal();
            total[typeIdx]++;
            if (r.isVacant()) {
                if (r.getStatus() == Room.HousekeepingStatus.READY) {
                    vacantReady[typeIdx]++;
                } else {
                    vacantNotReady[typeIdx]++;
                }
            } else {
                occupied[typeIdx]++;
            }
        }

        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-12s | %-12s | %-15s | %-12s | %-12s\n", 
                "Room Type", "Rate/Night", "Total Rooms", "Vacant (Ready)", "Vacant (Busy)", "Occupied");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (Room.RoomType type : Room.RoomType.values()) {
            int idx = type.ordinal();
            System.out.printf("%-15s | $%-11.2f | %-12d | %-15d | %-12d | %-12d\n",
                    type.getLabel(), type.getRate(), total[idx], vacantReady[idx], vacantNotReady[idx], occupied[idx]);
        }
        System.out.println("---------------------------------------------------------------------------------------------");

        System.out.println("1. View detailed list of a specific Room Type");
        System.out.println("2. Return to Front-Desk Menu");
        int subChoice = InputUtil.readInt("Enter choice (1-2): ", 1, 2);

        if (subChoice == 1) {
            System.out.println("\nSelect Room Type to view details:");
            for (int i = 0; i < Room.RoomType.values().length; i++) {
                System.out.printf("%d. %s\n", i + 1, Room.RoomType.values()[i].getLabel());
            }
            int typeChoice = InputUtil.readInt("Enter choice (1-4): ", 1, 4);
            Room.RoomType selectedType = Room.RoomType.values()[typeChoice - 1];

            System.out.printf("\n--- Detailed Status of %s Rooms ---\n", selectedType.getLabel());
            System.out.println("--------------------------------------------------------------------------------------");
            System.out.printf("%-10s | %-20s | %-15s | %-20s\n", "Room No", "Housekeeping Status", "Vacancy", "Guest Confirmation");
            System.out.println("--------------------------------------------------------------------------------------");
            for (int i = 1; i <= rooms.getLength(); i++) {
                Room r = rooms.getEntry(i);
                if (r.getRoomType() == selectedType) {
                    String vacancyStr = r.isVacant() ? "VACANT" : "OCCUPIED";
                    String confStr = r.getCurrentGuestConfirmation() != null ? r.getCurrentGuestConfirmation() : "-";
                    System.out.printf("%-10d | %-20s | %-15s | %-20s\n", 
                            r.getRoomNumber(), r.getStatus().getLabel(), vacancyStr, confStr);
                }
            }
            System.out.println("--------------------------------------------------------------------------------------");
        }
        InputUtil.pressEnterToContinue();
    }

    private void viewBillingDetails() {
        System.out.println("\n--- View Billing Details & Invoice ---");
        String code = InputUtil.readConfirmationNumber("Enter 8-digit confirmation code: ");

        Booking booking = controller.searchBooking(code);
        if (booking == null) {
            System.out.println("\nERROR: No reservation records match that confirmation number.");
            InputUtil.pressEnterToContinue();
            return;
        }

        Guest guest = booking.getGuest();
        Room.RoomType roomType = booking.getRequestedRoomType();
        double rate = roomType.getRate();
        int nights = booking.getNights();
        double baseCharges = rate * nights;

        double discountRate = controller.getDiscountRate(guest.getTier());
        double discountAmount = baseCharges * discountRate;

        // Points redemption logic
        int pointsRedeemed = 0;
        double pointsOffset = 0.0;
        int availablePoints = guest.getLoyaltyPoints();

        if (availablePoints > 0) {
            System.out.printf("Guest has %d loyalty points available.\n", availablePoints);
            String redeemChoice = InputUtil.readString("Redeem points to offset bill? (Y/N): ").trim().toUpperCase();
            if (redeemChoice.equals("Y") || redeemChoice.equals("YES")) {
                // Calculate maximum points redeemable (remaining amount to pay, 10 points = $1.00 offset)
                double maxOffsetNeeded = baseCharges - discountAmount;
                int maxPointsRedeemable = (int) Math.min(availablePoints, Math.ceil(maxOffsetNeeded * 10));

                System.out.printf("Enter points to redeem (10 points = $1.00 offset, max %d pts): ", maxPointsRedeemable);
                pointsRedeemed = InputUtil.readInt("", 1, maxPointsRedeemable);
                pointsOffset = pointsRedeemed / 10.0;

                // Confirm redemption
                System.out.printf("Are you sure you want to redeem %d points for a -$%.2f discount? (Y/N): ", pointsRedeemed, pointsOffset);
                String confirm = InputUtil.readString("").trim().toUpperCase();
                if (confirm.equals("Y") || confirm.equals("YES")) {
                    boolean success = controller.redeemGuestPoints(guest, pointsRedeemed);
                    if (success) {
                        System.out.println("Points successfully redeemed!");
                    } else {
                        System.out.println("Redemption failed. Continuing with standard invoice.");
                        pointsRedeemed = 0;
                        pointsOffset = 0.0;
                    }
                } else {
                    System.out.println("Redemption cancelled.");
                    pointsRedeemed = 0;
                    pointsOffset = 0.0;
                }
            }
        }

        double netAmount = Math.max(0.0, baseCharges - discountAmount - pointsOffset);
        double serviceCharge = netAmount * 0.10;
        double serviceTax = netAmount * 0.06;
        double totalBill = netAmount + serviceCharge + serviceTax;

        System.out.println("\n=========================================================================");
        System.out.println("                      TARUMT RESORTS BILLING INVOICE                     ");
        System.out.println("=========================================================================");
        System.out.printf(" Confirmation Number:  %-15s | Booking Date: %s\n", booking.getConfirmationNumber(), booking.getBookingDate());
        System.out.printf(" Guest Name:           %-15s | Loyalty Tier: %s\n", guest.getName(), guest.getTier().getLabel());
        System.out.printf(" Room Type:            %-15s | Room Number:  %s\n", 
                roomType.getLabel(), (booking.getAllocatedRoom() != null ? String.valueOf(booking.getAllocatedRoom().getRoomNumber()) : "Pending Assignment"));
        System.out.printf(" Stay Duration:        %-15s | Current Points: %d pts\n", nights + " night(s)", guest.getLoyaltyPoints());
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf(" Base Room Charges (%d nights @ $%.2f/night):    $%12.2f\n", nights, rate, baseCharges);
        if (discountAmount > 0) {
            System.out.printf(" Loyalty Member Tier Discount (%.0f%%):              -$%12.2f\n", (discountRate * 100), discountAmount);
        }
        if (pointsOffset > 0) {
            System.out.printf(" Loyalty Points Offset (%d points redeemed):       -$%12.2f\n", pointsRedeemed, pointsOffset);
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf(" Net Taxable Amount:                               $%12.2f\n", netAmount);
        System.out.printf(" Service Charge (10%%):                             $%12.2f\n", serviceCharge);
        System.out.printf(" Government Tourism/Service Tax (6%%):              $%12.2f\n", serviceTax);
        System.out.println("=========================================================================");
        System.out.printf(" TOTAL AMOUNT DUE:                                 $%12.2f\n", totalBill);
        System.out.println("=========================================================================");
        
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
