package boundary;

import control.CheckInResult;
import control.RoomAssignmentResult;
import control.WalkInController;
import entity.Customer;
import entity.Room;
import entity.RoomType;

import java.util.Scanner;

/**
 * Author: <Your Name Here>
 *
 * WalkInUI is the boundary class - matches the menu skeleton you
 * pasted exactly. Per ECB rules, this class does ONLY I/O: it reads
 * input, calls into WalkInController, and prints the result. All
 * classification/queueing/matching/verification logic lives in the
 * control layer.
 */
public class WalkInUI {

    private final WalkInController controller;
    private final Scanner scanner;

    public WalkInUI(WalkInController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int choice;
        do {
            System.out.println("\nHotel Check-In System");
            System.out.println("=================================");
            System.out.println("1. Walk In");
            System.out.println("2. Assign Rooms to Waiting Customers");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = readInteger();
            switch (choice) {
                case 1: checkIn(); break;
                case 2: assignWaitingCustomers(); break;
                case 3: System.out.println("\nSystem closed."); break;
                default: System.out.println("\nInvalid choice.");
            }
        } while (choice != 3);
    }

    private void checkIn() {
        System.out.print("Enter guest name: ");
        String name = scanner.nextLine().trim();

        RoomType requestedRoomType = readRoomTypeChoice();

        System.out.print("Enter VIP code (leave blank if not vip, then press Enter): ");
        String vipCodeInput = scanner.nextLine();

        CheckInResult result = controller.checkIn(name, requestedRoomType, vipCodeInput);
        Customer customer = result.getCustomer();

        switch (result.getOutcome()) {
            case VIP_REGISTERED:
                System.out.println("VIP code verified. " + customer.getName() + " added to the VIP queue.");
                break;
            case STANDARD_NO_CODE:
                System.out.println("No VIP code entered. " + customer.getName() + " added to the Standard queue.");
                break;
            case STANDARD_INVALID_CODE:
                System.out.println("Invalid VIP code - proceeding as a standard guest.");
                System.out.println(customer.getName() + " added to the Standard queue.");
                break;
        }
    }

    private RoomType readRoomTypeChoice() {
        RoomType[] types = RoomType.values();
        System.out.println("Select requested room type:");
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i]);
        }
        System.out.print("Choice: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= types.length) {
                return types[choice - 1];
            }
        } catch (NumberFormatException ignored) {
            // falls through to default below
        }
        System.out.println("Invalid choice, defaulting to DELUXE.");
        return RoomType.DELUXE;
    }

    private void assignWaitingCustomers() {
        RoomAssignmentResult result = controller.assignRoom();
        switch (result.getStatus()) {
            case SUCCESS:
                Customer customer = result.getCustomer();
                Room room = result.getRoom();
                System.out.println("\nRoom assigned: " + room);
                System.out.println("To customer: " + customer);
                break;
            case NO_ROOM_AVAILABLE:
                System.out.println("\n" + result.getCustomer().getName() + " is waiting for a "
                        + result.getCustomer().getRequestedRoomType() + " room, but none are currently available.");
                System.out.println("They remain in their queue.");
                break;
            case NO_CUSTOMERS_WAITING:
                System.out.println("\nNo customers currently waiting for a room.");
                break;
        }
    }

    private int readInteger() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
