package boundary;

import control.HotelController;
import entity.AssignmentResult;
import entity.Customer;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import entity.WaitingCustomer;
import util.DateValidator;
import util.InputUtil;
import entity.CustomerType;


/** Staff console interface. */
public class HotelCheckInUI {
    private final HotelController controller;
    private final GenerateReportUI reportUI;

    public HotelCheckInUI() {
        this(new HotelController());
    }

    public HotelCheckInUI(HotelController controller) {
        this.controller = controller != null ? controller : new HotelController();
        this.reportUI = new GenerateReportUI(this.controller);
    }

    public void start() {
        int choice;
        do {
            System.out.println("\n=======================================");
            System.out.println("Hotel Reservations Management System");
            System.out.println("=======================================");
            System.out.println("1. Add Walk-In Reservation");
            System.out.println("2. Check Out");
            System.out.println("3. Check In Customer");
            System.out.println("4. View Room Status");
            System.out.println("5. View Reports");
            System.out.println("6. Back to main menu");
            System.out.println("=======================================");
            choice = InputUtil.readInt("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1: addWalkInReservation(); break;
                case 2: checkOut(); break;
                case 3: checkInCustomer(); break;
                case 4: displayRoomStatus(); break;
                case 5: viewReports(); break;
                case 6: break;
                default: System.out.println("\nInvalid choice.");
            }
        } while (choice != 6);
    }

    private void addWalkInReservation() {
        System.out.println("\n=================================");
        System.out.println("Add Walk-In Reservation");
        System.out.println("=================================");
        String name = readCustomerName();
        int pax = readPositiveInteger("Number of Pax: ");
        String checkInDate = readDate("Check-in Date (DD/MM/YYYY): ");
        int nightsStayed = readPositiveInteger("Nights Stayed: ");
        RoomType roomType = readRoomType();
        String vipCode = InputUtil.readStringWithSkip("Enter VIP code, or press Enter for standard customer: ");
        
        Customer customer = new Customer(
        name,
        pax,
        checkInDate,
        nightsStayed, CustomerType.STANDARD);

        WaitingCustomer waitingCustomer =
        controller.addWalkInReservation(
                customer,
                roomType,
                vipCode);

        if (waitingCustomer.getCustomerType() == CustomerType.VIP) {
            System.out.println("\nVIP customer added to the VIP waiting list.");
        } else if (!vipCode.isEmpty()) {
            System.out.println("\nInvalid VIP code, defaulting to standard customer.");
        } else {
            System.out.println(
                "\nStandard customer added to the standard waiting list.");
        }
        System.out.println("Confirmation Number: " + waitingCustomer.getConfirmationNumber());
    }

    private void checkOut() {
        System.out.println("\n=================================");
        System.out.println("Check Out");
        System.out.println("=================================");
        int roomNumber = readPositiveInteger("Enter Room Number: ");
        String checkOutDate = readDate("Check-out Date (DD/MM/YYYY): ");
        if (controller.checkOut(roomNumber, checkOutDate)) {
            System.out.println("\nRoom " + roomNumber + " is now Available.");
        } else {
            System.out.println("Room does not exist or is already Available.");
        }
    }

    private void checkInCustomer() {
        AssignmentResult result = controller.allocateRoom();
        System.out.println("\n=================================");
        System.out.println("Check In Customer");
        System.out.println("=================================");
        for (int i = 0; i < result.getSkippedCustomers().size(); i++) {
            WaitingCustomer customer = result.getSkippedCustomers().get(i);
            System.out.println("Cannot assign " + customer.getCustomerName()
                    + ": no available "
                    + customer.getRequestedRoomType().getDisplayName()
                    + " room can fit " + customer.getPax() + " guest(s).");
        }
        if (result.getAssignedReservation() == null) {
            System.out.println("No waiting customer can currently be checked in.");
        } else {
            System.out.println("\nCustomer checked in:");
            printAssignment(result.getAssignedReservation());
        }
    }

    private void displayRoomStatus() {
        System.out.println("\n=================================");
        System.out.println("Room Status");
        System.out.println("=================================");
        System.out.printf("%-10s %-12s %-10s %-12s%n",
                "Room", "Type", "Capacity", "Status");
        for (Room room : controller.getRooms()) {
            System.out.printf("%-10d %-12s %-10d %-12s%n",
                    room.getRoomNumber(), room.getRoomType().getDisplayName(),
                    room.getCapacity(), room.getStatus().getDisplayName());
        }
    }

    private void viewReports() {
        System.out.println("\nView Reports");
        System.out.println("1. All Reservations Report");
        System.out.println("2. Standard Customers Waiting List Report");
        System.out.println("3. Back");
        switch (InputUtil.readInt("Enter your choice: ", 1, 3)) {
            case 1: viewFilteredReservationReport(); break;
            case 2: viewFilteredWaitingReport(); break;
            default: break;
        }
    }

    private void viewFilteredReservationReport() {
        reportUI.displayReservationReport(null, null);
        while (true) {
            int filterChoice = readReservationFilterChoice();
            if (filterChoice == 7) {
                return;
            }
            switch (filterChoice) {
                case 1: reportUI.displayReservationReport(RoomType.DELUXE, null); break;
                case 2: reportUI.displayReservationReport(RoomType.PREMIUM, null); break;
                case 3: reportUI.displayReservationReport(RoomType.PLATINUM, null); break;
                case 4: reportUI.displayReservationReport(null, null); break;
                case 5: reportUI.displayReservationReport(null, true); break;
                case 6: reportUI.displayReservationReport(null, false); break;
                default: break;
            }
        }
    }

    private int readReservationFilterChoice() {
            System.out.println("\nFilter all reservations by:");
            System.out.println("1. Deluxe");
            System.out.println("2. Premium");
            System.out.println("3. Platinum");
            System.out.println("4. All Room Types");
            System.out.println("5. Checked Out Customers");
            System.out.println("6. Active Customers");
            System.out.println("7. Back");
            System.out.print("Enter your choice: ");
            return InputUtil.readInt("Enter your choice: ", 1, 7);
    }

    private void viewFilteredWaitingReport() {
        reportUI.displayWaitingReport(null);
        while (true) {
            RoomType filter = readReportFilter();
            if (filter == null && lastFilterWasExit) {
                return;
            }
            reportUI.displayWaitingReport(filter);
        }
    }

    private boolean lastFilterWasExit;

    /** Returns null for All; the flag distinguishes All from Back. */
    private RoomType readReportFilter() {
        lastFilterWasExit = false;
        System.out.println("\nFilter by room type:");
        System.out.println("1. Deluxe");
        System.out.println("2. Premium");
        System.out.println("3. Platinum");
        System.out.println("4. All");
        System.out.println("5. Back");
        int choice = InputUtil.readInt("Enter your choice: ", 1, 5);
        switch (choice) {
            case 1: return RoomType.DELUXE;
            case 2: return RoomType.PREMIUM;
            case 3: return RoomType.PLATINUM;
            case 5: lastFilterWasExit = true; return null;
            default: return null; // choice == 4
        }
    }

    private void printAssignment(Reservation reservation) {
        System.out.println("Customer: " + reservation.getCustomer().getCustomerName());
        System.out.println("Room Number: " + reservation.getRoom().getRoomNumber());
        System.out.println("Room Type: " + reservation.getRoom().getRoomType().getDisplayName());
    }

    private String readCustomerName() {
        while (true) {
            String name = InputUtil.readString("Customer Name: ");
            if (controller.customerExists(name)) {
                System.out.println("Customer name already exists.");
            } else {
                return name;
            }
        }
    }

    private int readPositiveInteger(String prompt) {
        return InputUtil.readInt(prompt, 1, Integer.MAX_VALUE);
    }

    private String readDate(String prompt) {
        while (true) {
            String date = InputUtil.readStringWithSkip(prompt);
            if (DateValidator.isValid(date)) return date;
            System.out.println("Invalid date. Please use DD/MM/YYYY.");
        }
    }

    private RoomType readRoomType() {
        System.out.println("Desired Room Type: (1)Deluxe (2)Premium (3)Platinum");
        int choice = InputUtil.readInt("Enter your choice: ", 1, 3);
        switch (choice) {
            case 1: return RoomType.DELUXE;
            case 2: return RoomType.PREMIUM;
            default: return RoomType.PLATINUM; // choice == 3
        }
    }
}
