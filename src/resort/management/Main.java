package resort.management;

import java.util.Scanner;
import tarumtresort.adt.Queue;
import tarumtresort.entity.Customer;
import tarumtresort.entity.Reservation;
import tarumtresort.entity.Room;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static Queue<Reservation> waitingQueue = new Queue<>(100);

    static Room[] rooms = generateRooms();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\nHotel Check-In System");
            System.out.println("=================================");
            System.out.println("1. Check In");
            System.out.println("2. Release Room");
            System.out.println("3. Exit");
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
                    System.out.println("\nSystem closed.");
                    break;

                default:
                    System.out.println("\nInvalid choice.");
            }

        } while (choice != 3);
    }
    
    public static Room[] generateRooms(){
        
            Room[] rooms = new Room[10];
            rooms[0] = new Room(101, 1);
            rooms[0] = new Room(102, 1);
            rooms[0] = new Room(201, 2);
            rooms[0] = new Room(202, 2);
            rooms[0] = new Room(301, 3);
            rooms[0] = new Room(302, 3);
            
            return rooms;
    };

    public static void checkIn() {

        System.out.println("\nCheck In");
        System.out.println("=================================");

        System.out.print("Customer Name: ");
        String name = scanner.nextLine();

        System.out.print("Number of Pax: ");
        int pax = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Check-in Date: ");
        String checkInDate = scanner.nextLine();

        System.out.print("Check-out Date: ");
        String checkOutDate = scanner.nextLine();

        System.out.print("Nights Stayed: ");
        int nightsStayed = scanner.nextInt();
        scanner.nextLine();

        Customer customer = new Customer(
                name,
                pax,
                checkInDate,
                checkOutDate,
                nightsStayed
        );

        Room availableRoom = findAvailableRoom(pax);

        if (availableRoom != null) {

            availableRoom.setAvailable(false);

            Reservation reservation =
                    new Reservation(customer, availableRoom);

            System.out.println("\nCheck-in successful.");
            System.out.println("Customer: " + name);
            System.out.println("Room Number: "
                    + availableRoom.getRoomNumber());

        } else {

            Reservation reservation =
                    new Reservation(customer, null);

            waitingQueue.enqueue(reservation);

            System.out.println("\nNo suitable room is currently available.");
            System.out.println("Customer has been added to the waiting queue.");
            System.out.println("Queue Position: "
                    + waitingQueue.size());
        }
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

        room.setAvailable(true);

        System.out.println("\nRoom " + roomNumber
                + " has been released.");

        assignRoomToWaitingCustomer(room);
    }

    public static Room findAvailableRoom(int pax) {

        for (Room room : rooms) {

            if (room.isAvailable()
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

    public static void assignRoomToWaitingCustomer(Room releasedRoom) {

        if (waitingQueue.isEmpty()) {
            System.out.println("No customers are waiting.");
            return;
        }

        Reservation nextReservation = waitingQueue.peek();

        int requiredPax =
                nextReservation.getCustomer().getPax();

        if (releasedRoom.getCapacity() >= requiredPax) {

            nextReservation = waitingQueue.dequeue();

            nextReservation.setRoom(releasedRoom);

            releasedRoom.setAvailable(false);

            System.out.println("\nRoom automatically assigned.");
            System.out.println("Customer: "
                    + nextReservation.getCustomer()
                                     .getCustomerName());

            System.out.println("Room Number: "
                    + releasedRoom.getRoomNumber());

        } else {

            System.out.println(
                    "Released room is not suitable for the next customer."
            );
        }
    }
}
