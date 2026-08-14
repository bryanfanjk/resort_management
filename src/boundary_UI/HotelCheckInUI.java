package boundary_UI;

import java.util.Scanner;
import adt.List;
import control.HotelController;
import entity.Customer;
import entity.LoyaltyTier;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import utility.DateValidator;

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
            System.out.println("1. Check In");
            System.out.println("2. Check Out");
            System.out.println("3. Assign Rooms to Waiting Customers");
            System.out.println("4. View Room Status");
            System.out.println("5. View Reports");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = readInteger();

            switch (choice) {
                case 1: checkIn(); break;
                case 2: checkOut(); break;
                case 3: assignWaitingCustomers(); break;
                case 4: displayRoomStatus(); break;
                case 5: viewReports(); break;
                case 6: System.out.println("\nSystem closed."); break;
                default: System.out.println("\nInvalid choice.");
            }
        } while (choice != 6);
    }

    private void checkIn() {
        System.out.println("\nCheck In");
        System.out.println("=================================");
        String name = readCustomerName();
        int pax = readPositiveInteger("Number of Pax: ");
        String checkInDate = readDate("Check-in Date (DD/MM/YYYY): ");
        String checkOutDate = readDate("Check-out Date (DD/MM/YYYY): ");
        int nightsStayed = readPositiveInteger("Nights Stayed: ");
        RoomType roomType = readRoomType();
        LoyaltyTier tier = readTierChoice();
        int loyaltyPoints = readLoyaltyPoints();

        Reservation reservation = controller.checkIn(new Customer(name, pax,
                checkInDate, checkOutDate, nightsStayed, tier, loyaltyPoints), roomType);
        if (reservation.getRoom() == null) {
            System.out.println("\nNo matching " + roomType.getDisplayName()
                    + " room is currently available.");
            System.out.println("Customer has been added to the waiting queue.");
            System.out.println("Queue Position: " + controller.getWaitingCount());
            return;
        }

        System.out.println("\nCheck-in successful.");
        printAssignment(reservation);
    }

    private void checkOut() {
        System.out.println("\nCheck Out");
        System.out.println("=================================");
        int roomNumber = readPositiveInteger("Enter Room Number: ");
        if (controller.checkOut(roomNumber)) {
            System.out.println("\nRoom " + roomNumber + " is now Available.");
        } else {
            System.out.println("Room does not exist or is already Available.");
        }
    }

    private void assignWaitingCustomers() {
        List<Reservation> assigned = controller.assignWaitingCustomers();
        System.out.println("\nWaiting Customer Assignment");
        System.out.println("=================================");
        if (assigned.isEmpty()) {
            System.out.println("No waiting customers can currently be assigned.");
            return;
        }
        for (int i = 0; i < assigned.size(); i++) {
            printAssignment(assigned.get(i));
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
        System.out.println("1. Reservation Report");
        System.out.println("2. Waiting List Report");
        System.out.println("3. Back");
        System.out.print("Enter your choice: ");
        switch (readInteger()) {
            case 1: reportUI.displayReservationReport(); break;
            case 2: reportUI.displayWaitingReport(); break;
            default: break;
        }
    }

    private void printAssignment(Reservation reservation) {
        System.out.println("Customer: " + reservation.getCustomer().getCustomerName());
        System.out.println("Room Number: " + reservation.getRoom().getRoomNumber());
        System.out.println("Room Type: " + reservation.getRoom().getRoomType().getDisplayName());
    }
    
    private int readLoyaltyPoints(){
        System.out.print("Loyalty Points: ");
        int points = 0;
        try { 
            points = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid points value. Registration cancelled.");
        }
        return points;
    }

    private LoyaltyTier readTierChoice() {
        LoyaltyTier[] tiers = LoyaltyTier.values();
        System.out.println("Select Tier:");
        for (int i = 0; i < tiers.length; i++) {
            System.out.println((i + 1) + ". " + tiers[i].getLabel());
        }
        System.out.print("Choice: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= tiers.length) {
                return tiers[choice - 1];
            }
        } catch (NumberFormatException ignored) {
            // falls through to default below
        }
        System.out.println("Invalid choice, defaulting to STANDARD.");
        return LoyaltyTier.STANDARD;
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
