package boundary_UI;

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
            System.out.println("\n======================================");
            System.out.println("HOTEL CHECK-IN SYSTEM");
            System.out.println("======================================");
            System.out.println("1. Walk In");
            System.out.println("2. Assign Rooms to Waiting Customers");
            System.out.println("3. Exit");
            displayQueueStatus();
            System.out.print("\nEnter choice: ");
            choice = readInteger();

            switch (choice) {
                case 1:
                    checkIn();
                    break;
                case 2:
                    assignWaitingCustomers();
                    break;
                case 3:
                    System.out.println("\nProgram exited.");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    break;
            }
        } while (choice != 3);
    }

    private void checkIn() {
        System.out.print("\nEnter guest name: ");
        String name = scanner.nextLine().trim();

        RoomType requestedRoomType = readRoomTypeChoice();

        System.out.print("Enter VIP code (leave blank if none): ");
        String vipCodeInput = scanner.nextLine();

        CheckInResult result = controller.checkIn(name, requestedRoomType, vipCodeInput);
        Customer customer = result.getCustomer();

        switch (result.getOutcome()) {
            case VIP_REGISTERED:
                System.out.println("\nVIP code accepted. " + customer.getName() + " added to VIP queue.");
                break;
            case STANDARD_NO_CODE:
                System.out.println("\nNo VIP code entered. " + customer.getName() + " added to Standard queue.");
                break;
            case STANDARD_INVALID_CODE:
                System.out.println("\nInvalid VIP code. " + customer.getName() + " added to Standard queue as fallback.");
                break;
            default:
                System.out.println("\nUnknown result.");
                break;
        }

        displayQueueStatus();
    }

    private RoomType readRoomTypeChoice() {
        System.out.println("\nSelect room type:");
        RoomType[] roomTypes = RoomType.values();
        for (int i = 0; i < roomTypes.length; i++) {
            System.out.println((i + 1) + ". " + roomTypes[i]);
        }

        int choice = -1;
        while (choice < 1 || choice > roomTypes.length) {
            System.out.print("Choice: ");
            choice = readInteger();
            if (choice < 1 || choice > roomTypes.length) {
                System.out.println("Invalid room type. Please try again.");
            }
        }

        return roomTypes[choice - 1];
    }

    private void assignWaitingCustomers() {
        RoomAssignmentResult result = controller.assignRoom();

        switch (result.getStatus()) {
            case SUCCESS:
                System.out.println("\nRoom assigned successfully.");
                System.out.println("Customer: " + result.getCustomer().getName()
                        + " (" + result.getCustomer().getCustomerId() + ")");
                System.out.println("Room: " + result.getRoom().getRoomNumber()
                        + " (" + result.getRoom().getRoomType() + ")");
                break;
            case NO_ROOM_AVAILABLE:
                System.out.println("\nNo matching room available for "
                        + result.getCustomer().getName() + " ("
                        + result.getCustomer().getRequestedRoomType() + ").");
                System.out.println("Customer remains waiting in queue.");
                break;
            case NO_CUSTOMERS_WAITING:
                System.out.println("\nNo customers are currently waiting.");
                break;
            default:
                System.out.println("\nUnexpected assignment result.");
                break;
        }

        displayQueueStatus();
    }

    private void displayQueueStatus() {
        Customer vipNext = controller.peekNextVipCustomer();
        Customer standardNext = controller.peekNextStandardCustomer();

        System.out.println("\nQueue Status:");
        System.out.println("VIP queue: " + controller.getVipQueueSize());
        if (vipNext != null) {
            System.out.println("Next VIP: " + vipNext.getCustomerId() + " - " + vipNext.getName()
                    + " -> " + vipNext.getRequestedRoomType());
        } else {
            System.out.println("Next VIP: None");
        }

        System.out.println("Standard queue: " + controller.getStandardQueueSize());
        if (standardNext != null) {
            System.out.println("Next Standard: " + standardNext.getCustomerId() + " - " + standardNext.getName()
                    + " -> " + standardNext.getRequestedRoomType());
        } else {
            System.out.println("Next Standard: None");
        }
    }

    private int readInteger() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}