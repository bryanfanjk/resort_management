package boundary;

import adt.StandardList;
import adt.VipList;
import control.CheckInResult;
import control.RoomAssignmentResult;
import control.WalkInController;
import entity.Customer;
import entity.Room;
import entity.RoomType;

import java.util.Scanner;

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
            System.out.println("3. View VIP Waiting List");
            System.out.println("4. View Standard Waiting List");
            System.out.println("5. Exit");

            displayQueueStatus();

            System.out.print("Enter your choice: ");
            choice = readInteger();

            switch (choice) {
                case 1:
                    walkInCustomer();
                    break;
                case 2:
                    assignWaitingCustomers();
                    break;
                case 3:
                    viewVipList();
                    break;
                case 4:
                    viewStandardList();
                    break;
                case 5:
                    System.out.println("\nProgram exited.");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    break;
            }
        } while (choice != 5);
    }

    private void walkInCustomer() {
        System.out.print("\nEnter guest name: ");
        String name = scanner.nextLine().trim();

        RoomType roomType = readRoomTypeChoice();

        System.out.print("Enter VIP code (leave blank if none): ");
        String vipCodeInput = scanner.nextLine();

        CheckInResult result = controller.checkIn(name, roomType, vipCodeInput);

        switch (result.getOutcome()) {
            case VIP_REGISTERED:
                System.out.println("\nVIP code accepted. " + result.getCustomer().getName()
                        + " added to the VIP waiting list.");
                break;
            case STANDARD_NO_CODE:
                System.out.println("\nNo VIP code entered. " + result.getCustomer().getName()
                        + " added to the Standard waiting list.");
                break;
            case STANDARD_INVALID_CODE:
                System.out.println("\nInvalid VIP code. " + result.getCustomer().getName()
                        + " added to the Standard waiting list as regular customer.");
                break;
            default:
                System.out.println("\nUnknown check-in result.");
                break;
        }

        displayQueueStatus();
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
                System.out.println("\nNo room available for the current waiting customer.");
                if (result.getCustomer() != null) {
                    System.out.println("Customer: " + result.getCustomer().getName()
                            + " (" + result.getCustomer().getRequestedRoomType() + ")");
                }
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

    private void viewVipList() {
        VipList<Customer> vipList = controller.getVipList();

        System.out.println("\nVIP Waiting List:");
        if (vipList.isEmpty()) {
            System.out.println("No VIP customers waiting.");
            return;
        }

        for (int i = 0; i < vipList.size(); i++) {
            Customer customer = vipList.get(i);
            System.out.println((i + 1) + ". " + customer.getCustomerId()
                    + " | " + customer.getName()
                    + " | " + customer.getRequestedRoomType());
        }
    }

    private void viewStandardList() {
        StandardList<Customer> standardList = controller.getStandardList();

        System.out.println("\nStandard Waiting List:");
        if (standardList.isEmpty()) {
            System.out.println("No standard customers waiting.");
            return;
        }

        for (int i = 0; i < standardList.size(); i++) {
            Customer customer = standardList.get(i);
            System.out.println((i + 1) + ". " + customer.getCustomerId()
                    + " | " + customer.getName()
                    + " | " + customer.getRequestedRoomType());
        }
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

    private void displayQueueStatus() {
        System.out.println("\nWaiting List Status:");
        System.out.println("VIP waiting: " + controller.getVipListSize());

        Customer nextVip = controller.peekNextVipCustomer();
        if (nextVip == null) {
            System.out.println("Next VIP: None");
        } else {
            System.out.println("Next VIP: " + nextVip.getCustomerId() + " - "
                    + nextVip.getName() + " -> " + nextVip.getRequestedRoomType());
        }

        System.out.println("Standard waiting: " + controller.getStandardListSize());

        Customer nextStandard = controller.peekNextStandardCustomer();
        if (nextStandard == null) {
            System.out.println("Next Standard: None");
        } else {
            System.out.println("Next Standard: " + nextStandard.getCustomerId() + " - "
                    + nextStandard.getName() + " -> " + nextStandard.getRequestedRoomType());
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