package resort.management;

import java.util.Scanner;
import tarumtresort.adt.Queue;
import tarumtresort.entity.Customer;
import tarumtresort.entity.Reservation;
import tarumtresort.entity.Room;
import tarumtresort.entity.RoomType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import tarumtresort.adt.List;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static Queue<Reservation> waitingQueue = new Queue<>(100);
    static List<Reservation> reservations = new List<>(100);

    static Room[] rooms = generateRooms();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\nHotel Check-In System");
            System.out.println("=================================");
            System.out.println("1. Check In");
            System.out.println("2. Release Room");
            System.out.println("3. View Reports");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    checkIn();
                    break;

                case 2:
                    releaseRoom();
                    break;

                case 3:
                    viewReports();
                    break;

                case 4:
                    System.out.println("\nSystem closed.");
                    break;

                default:
                    System.out.println("\nInvalid choice.");
            }

        } while (choice != 4);
    }
    
    public static Room[] generateRooms(){
        
            Room[] rooms = new Room[6];
            rooms[0] = new Room(101, 1, RoomType.DELUXE);
            rooms[1] = new Room(102, 1, RoomType.DELUXE);
            rooms[2] = new Room(201, 2, RoomType.PREMIUM);
            rooms[3] = new Room(202, 2, RoomType.PREMIUM);
            rooms[4] = new Room(301, 3, RoomType.PLATINUM);
            rooms[5] = new Room(302, 3, RoomType.PLATINUM);
            
            return rooms;
    };

    public static void checkIn() {

        System.out.println("\nCheck In");
        System.out.println("=================================");

        // Customer name
        String name;

        while (true) {

            System.out.print("Customer Name: ");
            name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Customer name cannot be empty.");
                continue;
            }

            if (customerExists(name)) {
                System.out.println("Customer name already exists.");
                continue;
            }

            break;
        }

        // Number of pax
        int pax;

        while (true) {

            System.out.print("Number of Pax: ");
            String input = scanner.nextLine().trim();

            try {
                pax = Integer.parseInt(input);

                if (pax <= 0) {
                    System.out.println("Number of pax must be greater than 0.");
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                System.out.println("Number of pax must be a number.");
            }
        }

        // Check-in date
        String checkInDate;

        while (true) {

            System.out.print("Check-in Date (DD/MM/YYYY): ");
            checkInDate = scanner.nextLine().trim();

            if (isValidDate(checkInDate)) {
                break;
            }

            System.out.println(
                    "Invalid date. Please use DD/MM/YYYY."
            );
        }

        // Check-out date
        String checkOutDate;

        while (true) {

            System.out.print("Check-out Date (DD/MM/YYYY): ");
            checkOutDate = scanner.nextLine().trim();

            if (isValidDate(checkOutDate)) {
                break;
            }

            System.out.println(
                    "Invalid date. Please use DD/MM/YYYY."
            );
        }

        // Nights stayed
        int nightsStayed;

        while (true) {

            System.out.print("Nights Stayed: ");
            String input = scanner.nextLine().trim();

            try {
                nightsStayed = Integer.parseInt(input);

                if (nightsStayed <= 0) {
                    System.out.println(
                            "Nights stayed must be greater than 0."
                    );
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                System.out.println(
                        "Nights stayed must be a number."
                );
            }
        }

        RoomType requestedRoomType = readRoomType();

        Customer customer = new Customer(
                name,
                pax,
                checkInDate,
                checkOutDate,
                nightsStayed
        );

        Room availableRoom = findAvailableRoom(pax, requestedRoomType);

        if (availableRoom != null) {

            availableRoom.setAvailable(false);

            Reservation reservation =
                    new Reservation(customer, availableRoom, requestedRoomType);
            reservations.add(reservation);

            System.out.println("\nCheck-in successful.");
            System.out.println("Customer: " + name);
            System.out.println("Room Number: "
                    + availableRoom.getRoomNumber());
            System.out.println("Room Type: "
                    + availableRoom.getRoomType().getDisplayName());

        } else {

            Reservation reservation =
                    new Reservation(customer, null, requestedRoomType);

            waitingQueue.enqueue(reservation);

            System.out.println("\nNo suitable room is currently available.");
            System.out.println("Customer has been added to the waiting queue.");
            System.out.println("Queue Position: "
                    + waitingQueue.size());
        }
    }
    
    public static void viewReports() {

        int choice;

        do {
            System.out.println("\nView Reports");
            System.out.println("=================================");
            System.out.println("1. Reservation Report");
            System.out.println("2. Waiting List Report");
            System.out.println("3. Back");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    Report.displayReservationReport(reservations);
                    break;

                case 2:
                    Report.displayWaitingReport(waitingQueue);
                    break;

                case 3:
                    return;

                default:
                    System.out.println("\nInvalid choice.");
            }

        } while (choice != 3);
    }

    public static void releaseRoom() {

        System.out.println("\nRelease Room");
        System.out.println("=================================");

        System.out.print("Enter Room Number: ");
        int roomNumber = scanner.nextInt();
        scanner.nextLine();

        Room room = findRoom(roomNumber);

        if (room == null) {
            System.out.println("Room does not exist.");
            return;
        }

        if (room.isAvailable()) {
            System.out.println("Room is already available.");
            return;
        }
        
        for (int i = 0; i < reservations.size(); i++) {

            Reservation reservation = reservations.get(i);

            if (reservation.getRoom() != null
                    && reservation.getRoom().getRoomNumber() == roomNumber) {

                reservations.remove(i);
                break;
            }
        }

        room.setAvailable(true);
        

        System.out.println("\nRoom " + roomNumber
                + " has been released.");

        assignRoomToWaitingCustomer();
    }

    public static Room findAvailableRoom(int pax, RoomType roomType) {

        for (Room room : rooms) {

            if (room != null
                    && room.isAvailable()
                    && room.getRoomType() == roomType
                    && room.getCapacity() >= pax) {

                return room;
            }
        }

        return null;
    }

    /**
     * Retained for callers that do not need type filtering.
     */
    public static Room findAvailableRoom(int pax) {
        for (Room room : rooms) {
            if (room != null && room.isAvailable()
                    && room.getCapacity() >= pax) {
                return room;
            }
        }
        return null;
    }

    public static Room findRoom(int roomNumber) {

        for (Room room : rooms) {

            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }

        return null;
    }

    public static void assignRoomToWaitingCustomer() {

    if (waitingQueue.isEmpty()) {
        System.out.println("No customers are waiting.");
        return;
    }

    boolean assigned = false;

    for (int i = 0; i < waitingQueue.size(); i++) {

            Reservation reservation = waitingQueue.get(i);

            int requiredPax =
                    reservation.getCustomer().getPax();

            Room suitableRoom = findAvailableRoom(
                    requiredPax, reservation.getRequestedRoomType());

            if (suitableRoom != null) {

                // Remove the customer from the queue
                waitingQueue.remove(i);

                // Assign room
                reservation.setRoom(suitableRoom);
                suitableRoom.setAvailable(false);
                reservations.add(reservation);

                System.out.println("\nRoom assigned.");
                System.out.println("Customer: "
                        + reservation.getCustomer().getCustomerName());

                System.out.println("Room Number: "
                        + suitableRoom.getRoomNumber());
                System.out.println("Room Type: "
                        + suitableRoom.getRoomType().getDisplayName());

                assigned = true;

                // Continue checking other waiting customers
            }
        }

        if (!assigned) {
            System.out.println("No suitable rooms are available.");
        }
    }

    public static RoomType readRoomType() {
        while (true) {
            System.out.println("Desired Room Type:");
            System.out.println("1. Deluxe");
            System.out.println("2. Premium");
            System.out.println("3. Platinum");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    return RoomType.DELUXE;
                case "2":
                    return RoomType.PREMIUM;
                case "3":
                    return RoomType.PLATINUM;
                default:
                    System.out.println("Please choose 1, 2, or 3.");
            }
        }
    }
    
    public static boolean isValidDate(String date) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate.parse(date, formatter);
            return true;

        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    public static boolean customerExists(String name) {

        for (int i = 0; i < reservations.size(); i++) {

            Reservation reservation = reservations.get(i);

            if (reservation.getCustomer()
                    .getCustomerName()
                    .equalsIgnoreCase(name)) {

                return true;
            }
        }

        return false;
    }
    
    
    
    
    
    
}
