package tarumtresort.boundary;

import java.util.Scanner;
import tarumtresort.control.HotelController;
import tarumtresort.entity.AssignmentResult;
import tarumtresort.entity.Customer;
import tarumtresort.entity.Reservation;
import tarumtresort.entity.Room;
import tarumtresort.entity.RoomType;
import tarumtresort.entity.WaitingCustomer;
import tarumtresort.util.DateValidator;

/** Staff console interface. */
public class HotelCheckInUI {

    private final Scanner scanner = new Scanner(System.in);
    private final HotelController controller = new HotelController();
    private final GenerateReportUI reportUI = new GenerateReportUI(controller);

    public void start() {
        int choice;
        do {
            System.out.println("\nHotel Check-In System");
            System.out.println("=================================");
            System.out.println("1. Add Walk-In Reservation");
            System.out.println("2. Check Out");
            System.out.println("3. Check In Customer");
            System.out.println("4. View Room Status");
            System.out.println("5. View Reports");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = readInteger();

            switch (choice) {
                case 1: addWalkInReservation(); break;
                case 2: checkOut(); break;
                case 3: checkInCustomer(); break;
                case 4: displayRoomStatus(); break;
                case 5: viewReports(); break;
                case 6: System.out.println("\nSystem closed."); break;
                default: System.out.println("\nInvalid choice.");
            }
        } while (choice != 6);
    }

    private void addWalkInReservation() {
        System.out.println("\nAdd Walk-In Reservation");
        System.out.println("=================================");
        String name = readCustomerName();
        int pax = readPositiveInteger("Number of Pax: ");
        String checkInDate = readDate("Check-in Date (DD/MM/YYYY): ");
        int nightsStayed = readPositiveInteger("Nights Stayed: ");
        RoomType roomType = readRoomType();

        controller.addWalkInReservation(new Customer(name, pax,
                checkInDate, nightsStayed), roomType);
        System.out.println("\nWalk-in reservation added to the waiting list.");
        System.out.println("Waiting Position: " + controller.getWaitingCount());
        System.out.println("Use 'Check In Customer' when staff are ready to allocate a room.");
    }

    private void checkOut() {
        System.out.println("\nCheck Out");
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
        System.out.println("\nCheck In Customer");
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
        System.out.println("\nRoom Status");
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
        System.out.println("2. Waiting List Report");
        System.out.println("3. Back");
        System.out.print("Enter your choice: ");
        switch (readInteger()) {
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
        while (true) {
            System.out.println("\nFilter all reservations by:");
            System.out.println("1. Deluxe");
            System.out.println("2. Premium");
            System.out.println("3. Platinum");
            System.out.println("4. All Room Types");
            System.out.println("5. Checked Out Customers");
            System.out.println("6. Active Customers");
            System.out.println("7. Back");
            System.out.print("Enter your choice: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= 7) {
                    return choice;
                }
            } catch (NumberFormatException exception) {
                // Show the same validation message below.
            }
            System.out.println("Please choose 1, 2, 3, 4, 5, 6, or 7.");
        }
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
        while (true) {
            System.out.println("\nFilter by room type:");
            System.out.println("1. Deluxe");
            System.out.println("2. Premium");
            System.out.println("3. Platinum");
            System.out.println("4. All");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");
            switch (scanner.nextLine().trim()) {
                case "1": return RoomType.DELUXE;
                case "2": return RoomType.PREMIUM;
                case "3": return RoomType.PLATINUM;
                case "4": return null;
                case "5":
                    lastFilterWasExit = true;
                    return null;
                default: System.out.println("Please choose 1, 2, 3, 4, or 5.");
            }
        }
    }

    private void printAssignment(Reservation reservation) {
        System.out.println("Customer: " + reservation.getCustomer().getCustomerName());
        System.out.println("Room Number: " + reservation.getRoom().getRoomNumber());
        System.out.println("Room Type: " + reservation.getRoom().getRoomType().getDisplayName());
    }

    private String readCustomerName() {
        while (true) {
            System.out.print("Customer Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Customer name cannot be empty.");
            } else if (controller.customerExists(name)) {
                System.out.println("Customer name already exists.");
            } else {
                return name;
            }
        }
    }

    private int readPositiveInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            int value = readInteger();
            if (value > 0) return value;
            System.out.println("Value must be greater than 0.");
        }
    }

    private int readInteger() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.print("Please enter a number: ");
            }
        }
    }

    private String readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String date = scanner.nextLine().trim();
            if (DateValidator.isValid(date)) return date;
            System.out.println("Invalid date. Please use DD/MM/YYYY.");
        }
    }

    private RoomType readRoomType() {
        while (true) {
            System.out.println("Desired Room Type: 1. Deluxe  2. Premium  3. Platinum");
            System.out.print("Enter your choice: ");
            switch (scanner.nextLine().trim()) {
                case "1": return RoomType.DELUXE;
                case "2": return RoomType.PREMIUM;
                case "3": return RoomType.PLATINUM;
                default: System.out.println("Please choose 1, 2, or 3.");
            }
        }
    }
}
