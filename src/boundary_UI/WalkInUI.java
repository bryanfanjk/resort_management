package boundary;

import control.RoomAssignmentResult;
import control.WalkInController;
import entity.Customer;
import entity.Room;

import java.util.Scanner;

/**
 * Author: <Your Name Here>
 *
 * WalkInUI is the boundary class - matches the menu skeleton you
 * pasted exactly. Per ECB rules, this class does ONLY I/O: it reads
 * input, calls into WalkInController, and prints the result. All
 * classification/queueing/matching logic lives in the control layer.
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
        Customer customer = controller.checkIn();
        if (customer == null) {
            System.out.println("\nNo more customers to check in from the hardcoded list.");
            return;
        }
        String queueName = customer.getCustomerType() == entity.CustomerType.VIP ? "VIP" : "Standard";
        System.out.println("\n" + customer.getName() + " (" + customer.getCustomerType() + ") checked in.");
        System.out.println("Added to the " + queueName + " queue. Requested room type: " + customer.getRequestedRoomType());
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
