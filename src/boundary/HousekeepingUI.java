package boundary;

import adt.LinkedStack;
import adt.ListInterface;
import control.HousekeepingController;
import entity.HousekeepingLog;
import entity.Room;
import entity.HousekeepingStatus;
import util.InputUtil;

public class HousekeepingUI {

    private final HousekeepingController controller;
    private final LinkedStack<HousekeepingLog> stack;
    private final ListInterface<Room> rooms;

    public HousekeepingUI(HousekeepingController controller, LinkedStack<HousekeepingLog> stack, ListInterface<Room> rooms) {
        this.controller = controller;
        this.stack = stack;
        this.rooms = rooms;
    }

    public void start() {
        while (true) {
            InputUtil.displayHeader("Housekeeping and Room Status Management");
            System.out.println("1. Update Room Housekeeping Status");
            System.out.println("2. Rollback Last Status Log (Undo - LIFO Pop)");
            System.out.println("3. Bulk Rollback Multiple Actions (ADT popMany)");
            System.out.println("4. View Current Housekeeping Rollback Stack");
            System.out.println("5. View Room Cleaning Status Registry");
            System.out.println("6. Back to Main Menu");

            int choice = InputUtil.readInt("Enter choice (1-6): ", 1, 6);
            switch (choice) {
                case 1:
                    updateStatus();
                    break;
                case 2:
                    rollbackSingle();
                    break;
                case 3:
                    rollbackMultiple();
                    break;
                case 4:
                    viewStack();
                    break;
                case 5:
                    viewRoomsRegistry();
                    break;
                case 6:
                    return;
            }
        }
    }

    private void updateStatus() {
        System.out.println("\n--- Update Room Cleaning Status ---");
        int roomNum = InputUtil.readIntWithExit("Enter Room Number (101-510, or -1 to cancel): ", 101, 510);
        if (roomNum == -1) {
            System.out.println("Operation cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        System.out.println("Select New Cleaning Status:");
        System.out.println("1. Dirty");
        System.out.println("2. Cleaning In Progress");
        System.out.println("3. Inspected");
        System.out.println("4. Ready for Check-In");
        int statusChoice = InputUtil.readIntWithExit("Enter choice (1-4, or -1 to cancel): ", 1, 4);
        if (statusChoice == -1) {
            System.out.println("Operation cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }
        HousekeepingStatus newStatus = HousekeepingStatus.values()[statusChoice - 1];

        String supervisor = InputUtil.readString("Enter Supervisor Name (or -1 to cancel): ");
        if (supervisor.equals("-1")) {
            System.out.println("Operation cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        HousekeepingLog log = controller.updateRoomStatus(roomNum, newStatus, supervisor);
        if (log != null) {
            System.out.println("\nSUCCESS: Housekeeping log registered! Room status updated.");
            System.out.println(log);
        } else {
            System.out.println("ERROR: Room number not found in our resort catalog.");
        }
        InputUtil.pressEnterToContinue();
    }

    private void rollbackSingle() {
        System.out.println("\n--- Rollback Last Action (Pop Stack) ---");
        if (stack.isEmpty()) {
            System.out.println("No housekeeping log history available to rollback.");
            InputUtil.pressEnterToContinue();
            return;
        }

        HousekeepingLog lastLog = stack.peek();
        System.out.println("You are about to roll back the following action:");
        System.out.println("  " + lastLog);
        String confirm = InputUtil.readString("Are you sure you want to rollback this action? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Rollback cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        HousekeepingLog undone = controller.rollbackLastAction();
        if (undone != null) {
            System.out.println("SUCCESS: Rolled back the last action!");
            System.out.printf("Undone Log: %s\n", undone);
            System.out.printf("Room %d has been restored to: %s\n", undone.getRoomNumber(), undone.getOldStatus().getLabel());
        } else {
            System.out.println("ERROR: Rollback failed.");
        }
        InputUtil.pressEnterToContinue();
    }

    private void rollbackMultiple() {
        System.out.println("\n--- Bulk Rollback Actions (popMany Stack) ---");
        if (stack.isEmpty()) {
            System.out.println("No housekeeping log history available to rollback.");
            InputUtil.pressEnterToContinue();
            return;
        }

        System.out.printf("Currently there are %d updates in the log stack.\n", stack.getSize());
        int count = InputUtil.readIntWithExit("Enter number of actions to roll back (or -1 to cancel): ", 1, stack.getSize());
        if (count == -1) {
            System.out.println("Operation cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        String confirm = InputUtil.readString("Are you sure you want to rollback the last " + count + " actions? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Bulk rollback cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        ListInterface<HousekeepingLog> rolledBack = controller.rollbackMultipleActions(count);
        System.out.printf("\nSUCCESS: Popped %d items. The following updates have been undone:\n", rolledBack.size());
        for (int i = 0; i < rolledBack.size(); i++) {
            System.out.println("  - " + rolledBack.get(i));
        }
        InputUtil.pressEnterToContinue();
    }

    private void viewStack() {
        System.out.println("\n--- Current Housekeeping Stack History (Top is Last Logged) ---");
        if (stack.isEmpty()) {
            System.out.println("[Log stack is empty]");
            InputUtil.pressEnterToContinue();
            return;
        }

        ListInterface<HousekeepingLog> logs = stack.toList();
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-10s | %-8s | %-15s | %-20s | %-20s\n",
                "Depth", "Timestamp", "Room", "Supervisor", "Old Status", "New Status");
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (int i = 0; i < logs.size(); i++) {
            HousekeepingLog l = logs.get(i);
            System.out.printf("%-5d | %-10s | %-8d | %-15s | %-20s | %-20s\n",
                    i + 1, l.getTimestamp(), l.getRoomNumber(), l.getSupervisorName(), l.getOldStatus().getLabel(), l.getNewStatus().getLabel());
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        InputUtil.pressEnterToContinue();
    }

    private void viewRoomsRegistry() {
        System.out.println("\n--- Resort Rooms Clean Registry ---");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%-8s | %-12s | %-22s | %-15s\n", "Room No", "Room Type", "Housekeeping Status", "Occupancy");
        System.out.println("-----------------------------------------------------------------------------");
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            String occupancy = r.isVacant() ? "Vacant" : "Occupied (" + r.getCurrentGuestConfirmation() + ")";
            System.out.printf("%-8d | %-12s | %-22s | %-15s\n",
                    r.getRoomNumber(), r.getRoomType().getDisplayName(), r.getHousekeepingStatus().getLabel(), occupancy);
        }
        System.out.println("-----------------------------------------------------------------------------");
        InputUtil.pressEnterToContinue();
    }
}
