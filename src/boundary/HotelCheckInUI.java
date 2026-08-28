package boundary;

import adt.List;
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

    public HotelCheckInUI() {
        this(new HotelController());
    }

    public HotelCheckInUI(HotelController controller) {
        this.controller = controller != null ? controller : new HotelController();
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
            System.out.println("5. Back to main menu");
            System.out.println("=======================================");
            choice = InputUtil.readInt("Enter your choice: ", 1, 5);
            switch (choice) {
                case 1: addWalkInReservation(); break;
                case 2: checkOut(); break;
                case 3: checkInCustomer(); break;
                case 4: displayRoomStatus(); break;
                case 5: break;
                default: System.out.println("\nInvalid choice.");
            }
        } while (choice != 5);
    }

    private void addWalkInReservation() {
        System.out.println("\n=================================");
        System.out.println("Add Walk-In Reservation");
        System.out.println("=================================");
        String name = readCustomerName();
        int roomCount = readPositiveInteger("Number of Rooms Required: ");
        String vipCode = InputUtil.readStringWithSkip("Enter VIP code, or press Enter for standard customer: ");
        if (!vipCode.isEmpty() && !controller.isValidVipCode(vipCode)) {
            System.out.println("\nInvalid VIP code. Returning to the previous menu.");
            return;
        }

        Customer[] customers = new Customer[roomCount];
        RoomType[] roomTypes = new RoomType[roomCount];
        for (int roomIndex = 0; roomIndex < roomCount; roomIndex++) {
            System.out.println("\nRoom " + (roomIndex + 1) + " Requirements");
            int pax = readPositiveInteger("Number of Pax: ");
            String checkInDate = readDate("Check-in Date (DD/MM/YYYY): ");
            int nightsStayed = readPositiveInteger("Nights Stayed: ");
            roomTypes[roomIndex] = readRoomType();
            customers[roomIndex] = new Customer(name, pax, checkInDate,
                    nightsStayed, CustomerType.STANDARD);
        }

        List<WaitingCustomer> waitingCustomers = controller.addWalkInReservations(
                customers, roomTypes, vipCode);
        CustomerType customerType = waitingCustomers.get(0).getCustomerType();
        System.out.println(customerType == CustomerType.VIP
                ? "\nVIP customer added to the VIP waiting list."
                : "\nStandard customer added to the standard waiting list.");
        for (int roomIndex = 0; roomIndex < waitingCustomers.size(); roomIndex++) {
            System.out.println("Room " + (roomIndex + 1) + " Confirmation Number: "
                    + waitingCustomers.get(roomIndex).getConfirmationNumber());
        }
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
            System.out.println("Room does not exist or is Unavailable.");
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
                    room.getCapacity(), room.getOccupancyStatus().getLabel());
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
