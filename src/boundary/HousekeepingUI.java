package boundary;

import adt.LinkedStack;
import adt.List;
import adt.ListInterface;
import control.AuthController;
import control.HousekeepingController;
import control.HousekeepingReportController;
import entity.HousekeepingLog;
import entity.HousekeepingReport;
import entity.RoomStatus;
import entity.Room;
import entity.RoomType;
import util.InputUtil;

/* author: Ho Jia Ming */
public class HousekeepingUI {

    private final HousekeepingController controller;
    private final LinkedStack<HousekeepingLog> stack;
    private final ListInterface<Room> rooms;
    private final AuthController authController;
    private final HousekeepingReportController reportController;

    public HousekeepingUI(HousekeepingController controller,
            LinkedStack<HousekeepingLog> stack,
            ListInterface<Room> rooms,
            AuthController authController) {
        this.controller = controller;
        this.stack = stack;
        this.rooms = rooms;
        this.authController = authController;
        // Pass the stack directly - will convert to list when needed
        this.reportController = new HousekeepingReportController(stack, rooms);
    }

    public void start() {
        if (!login()) {
            System.out.println("Failed to login. Returning to main menu.");
            return;
        }

        while (true) {
            InputUtil.displayHeader("Housekeeping and Room Status Management");
            System.out.println("Current User: " + authController.getCurrentUser());
            System.out.println("1. Update Room Housekeeping Status");
            System.out.println("2. Supervisor Action (Approve/Reject Cleaning)");
            System.out.println("3. Rollback Last Status Log (Undo - LIFO Pop)");
            System.out.println("4. Bulk Rollback Multiple Actions (ADT popMany)");
            System.out.println("5. View Current Housekeeping Rollback Stack");
            System.out.println("6. View Room Cleaning Status Registry");
            System.out.println("7. View Housekeeping Reports");
            System.out.println("8. Logout");
            System.out.println("9. Back to Main Menu");

            int choice = InputUtil.readInt("Enter choice (1-9): ", 1, 9);
            switch (choice) {
                case 1:
                    updateStatus();
                    break;
                case 2:
                    supervisorAction();
                    break;
                case 3:
                    rollbackSingle();
                    break;
                case 4:
                    rollbackMultiple();
                    break;
                case 5:
                    viewStack();
                    break;
                case 6:
                    viewRoomsRegistry();
                    break;
                case 7:
                    reportMenu();
                    break;
                case 8:
                    logout();
                    return;
                case 9:
                    logout();
                    return;
            }
        }
    }

    private boolean login() {
        System.out.println("\n=============================================================");
        System.out.println("HOUSEKEEPING SYSTEM LOGIN");
        System.out.println("=============================================================");

        System.out.println("\nAvailable Users:");
        System.out.println(" Staff: staff1,staff2,staff3 (Password: staff123/456/789)");
        System.out.println(" Supervisors: supervisor1,supervisor2 (Password: sup123/456)");
        System.out.println("=============================================================");

        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Username: ");
            String username = InputUtil.readString("");
            System.out.print("Password: ");
            String password = InputUtil.readString("");

            if (authController.login(username, password)) {
                System.out.println("=============================================================");
                System.out.println("\nLogin successful! Welcome, " + username + "!");
                System.out.println("Role: " + authController.getCurrentUser().getRole().getDisplayName());
                InputUtil.pressEnterToContinue();
                return true;
            }

            attempts++;
            System.out.println("Invalid username or password. Attempts remaining: " + (3 - attempts));
        }

        System.out.println("Too many failed attempts. Access denied.");
        InputUtil.pressEnterToContinue();
        return false;
    }

    private void logout() {
        if (authController.isLoggedIn()) {
            System.out.println("Logging out user: " + authController.getCurrentUser().getUsername());
            authController.logout();
        }
    }

    private void updateStatus() {
        System.out.println("\n--- Update Room Cleaning Status ---");
        int roomNum = InputUtil.readIntWithExit("Enter Room Number (101-304, or -1 to cancel): ", 101, 304);
        if (roomNum == -1) {
            System.out.println("Operation cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        Room room = findRoom(roomNum);
        if (room == null) {
            System.out.println("ERROR: Room number not found.");
            InputUtil.pressEnterToContinue();
            return;
        }

        System.out.println("-".repeat(50));
        System.out.println("Current Status: " + room.getRoomStatus().getLabel());
        System.out.println("Available next statuses:");

        RoomStatus current = room.getRoomStatus();
        if (current.getSequenceNumber() < 4) {
            RoomStatus next = getStatusBySequence(current.getSequenceNumber() + 1);
            System.out.println("  " + next.getSequenceNumber() + ". " + next.getLabel());
        } else {
            System.out.println("  (Room is already at final status)");
            InputUtil.pressEnterToContinue();
            return;
        }

        System.out.println("-".repeat(50));
        System.out.println("Select New Cleaning Status:");
        System.out.println("1. Dirty");
        System.out.println("2. Cleaning In Progress");
        System.out.println("3. Inspected (Supervisor only)");
        System.out.println("4. Ready for Check-In (Supervisor only)");
        int statusChoice = InputUtil.readIntWithExit("Enter choice (1-4, or -1 to cancel): ", 1, 4);
        if (statusChoice == -1) {
            System.out.println("Operation cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }
        RoomStatus[] housekeepingChoices = {RoomStatus.DIRTY, RoomStatus.CLEANING_IN_PROGRESS,
            RoomStatus.INSPECTED, RoomStatus.READY};
        RoomStatus newStatus = housekeepingChoices[statusChoice - 1];

        String staffName = InputUtil.readString("Enter Your Name (or -1 to cancel): ");
        if (staffName.equals("-1")) {
            System.out.println("Operation cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        HousekeepingLog log = controller.updateRoomStatus(roomNum, newStatus, staffName);
        System.out.println("-".repeat(50));
        if (log != null) {
            System.out.println("SUCCESS: Housekeeping log registered! Room status updated.");
            System.out.println(log);
        } else {
            System.out.println("ERROR: Failed to update room status.");
        }
        System.out.println("-".repeat(50));
        InputUtil.pressEnterToContinue();
    }

    private void supervisorAction() {
        if (!authController.isSupervisor()) {
            System.out.println("=====================================================");
            System.out.println("ERROR: This action is only available to supervisors.");
            InputUtil.pressEnterToContinue();
            return;
        }

        System.out.println("\n--- Supervisor Action: Approve/Reject Cleaning ---");
        int roomNum = InputUtil.readIntWithExit("Enter Room Number (101-304, or -1 to cancel): ", 101, 304);
        if (roomNum == -1) {
            System.out.println("Operation cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        Room room = findRoom(roomNum);
        if (room == null) {
            System.out.println("ERROR: Room number not found.");
            InputUtil.pressEnterToContinue();
            return;
        }

        RoomStatus current = room.getRoomStatus();
        System.out.println("-".repeat(50));
        System.out.println("Current Status: " + current.getLabel());

        if (current != RoomStatus.CLEANING_IN_PROGRESS) {
            System.out.println("ERROR: Room must be in 'Cleaning In Progress' status for supervisor action.");
            InputUtil.pressEnterToContinue();
            return;
        }

        System.out.println("-".repeat(50));
        System.out.println("Supervisor Actions:");
        System.out.println("1. Approve Cleaning → Inspected");
        System.out.println("2. Reject Cleaning → Dirty");
        int action = InputUtil.readIntWithExit("Enter choice (1-2, or -1 to cancel): ", 1, 2);
        if (action == -1) {
            System.out.println("Operation cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        RoomStatus newStatus = (action == 1)
                ? RoomStatus.INSPECTED : RoomStatus.DIRTY;

        String staffName = InputUtil.readString("Enter Your Name: ");
        HousekeepingLog log = controller.approveCleaning(roomNum, newStatus, staffName);

        System.out.println("-".repeat(50));
        if (log != null) {
            System.out.println("SUCCESS: " + (action == 1 ? "Approved" : "Rejected") + " cleaning!");
            System.out.println(log);
        } else {
            System.out.println("ERROR: Failed to process supervisor action.");
        }
        System.out.println("-".repeat(50));
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
        System.out.println("-".repeat(50));
        System.out.println("You are about to roll back the following action:");
        System.out.println("  " + lastLog);
        System.out.println("-".repeat(50));
        String confirm = InputUtil.readString("Are you sure you want to rollback this action? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Rollback cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        HousekeepingLog undone = controller.rollbackLastAction();
        System.out.println("-".repeat(50));
        if (undone != null) {
            System.out.println("SUCCESS: Rolled back the last action!");
            System.out.printf("Undone Log: %s\n", undone);
            System.out.printf("Room %d has been restored to: %s\n", undone.getRoomNumber(), undone.getOldStatus().getLabel());
        } else {
            System.out.println("ERROR: Rollback failed.");
        }
        System.out.println("-".repeat(50));
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

        System.out.println("-".repeat(50));
        String confirm = InputUtil.readString("Are you sure you want to rollback the last " + count + " actions? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Bulk rollback cancelled.");
            InputUtil.pressEnterToContinue();
            return;
        }

        ListInterface<HousekeepingLog> rolledBack = controller.rollbackMultipleActions(count);
        System.out.println("-".repeat(50));
        System.out.printf("SUCCESS: Popped %d items. The following updates have been undone:\n", rolledBack.size());
        for (int i = 0; i < rolledBack.size(); i++) {
            System.out.println("  - " + rolledBack.get(i));
        }
        System.out.println("-".repeat(50));
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
                "Depth", "Timestamp", "Room", "Staff/Supervisor", "Old Status", "New Status");
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (int i = 0; i < logs.size(); i++) {
            HousekeepingLog l = logs.get(i);
            System.out.printf("%-5d | %-10s | %-8d | %-15s | %-20s | %-20s\n",
                    i + 1, l.getTimestamp(), l.getRoomNumber(), l.getStaffName(),
                    l.getOldStatus().getLabel(), l.getNewStatus().getLabel());
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
                    r.getRoomNumber(), r.getRoomType().getDisplayName(),
                    r.getRoomStatus().getLabel(), occupancy);
        }
        System.out.println("-----------------------------------------------------------------------------");
        InputUtil.pressEnterToContinue();
    }

    // REPORT MENU - 2 Reports
    private void reportMenu() {
        while (true) {
            InputUtil.displayHeader("Housekeeping Reports");
            System.out.println("1. Room Cleaning Status Report");
            System.out.println("2. Housekeeping Action Audit Report");
            System.out.println("3. Back to Main Menu");

            int choice = InputUtil.readInt("Select report (1-3): ", 1, 3);
            switch (choice) {
                case 1:
                    displayRoomCleaningStatusReport();
                    break;
                case 2:
                    displayActionAuditReport();
                    break;
                case 3:
                    return;
            }
        }
    }

    // ============================================================
    // REPORT 1: ROOM CLEANING STATUS REPORT
    // ============================================================
    private void displayRoomCleaningStatusReport() {
        InputUtil.displayHeader("Room Cleaning Status Report");

        // Filter options
        System.out.println("\n--- FILTER OPTIONS ---");
        System.out.println("Filter by Room Type:");
        System.out.println("1. All Room Types");
        System.out.println("2. Deluxe");
        System.out.println("3. Premium");
        System.out.println("4. Platinum");
        int typeChoice = InputUtil.readInt("Select room type: ", 1, 4);
        RoomType roomTypeFilter = (typeChoice == 1) ? null : RoomType.values()[typeChoice - 2];

        System.out.println("\nFilter by Cleaning Status:");
        System.out.println("1. All Statuses");
        System.out.println("2. Dirty");
        System.out.println("3. Cleaning In Progress");
        System.out.println("4. Inspected");
        System.out.println("5. Ready for Check-In");
        int statusChoice = InputUtil.readInt("Select status: ", 1, 5);
        RoomStatus[] housekeepingStatuses = {RoomStatus.DIRTY, RoomStatus.CLEANING_IN_PROGRESS,
            RoomStatus.INSPECTED, RoomStatus.READY};
        RoomStatus statusFilter = (statusChoice == 1) ? null : housekeepingStatuses[statusChoice - 2];

        System.out.println("\nSort by:");
        System.out.println("1. Room Number (Ascending)");
        System.out.println("2. Room Number (Descending)");
        System.out.println("3. Status (Ascending)");
        System.out.println("4. Status (Descending)");
        int sortChoice = InputUtil.readInt("Select sort option: ", 1, 4);

        // Generate report with filters
        HousekeepingReport report = reportController.generateRoomStatusReport(roomTypeFilter, statusFilter);
        ListInterface<Room> sortedRooms = reportController.sortRoomsForReport(sortChoice, report.getRooms());

        // Display Report
        printReportHeader("ROOM CLEANING STATUS REPORT");

        // Show filter summary
        System.out.println("\n--- APPLIED FILTERS ---");
        System.out.println("Room Type: " + (roomTypeFilter == null ? "All" : roomTypeFilter.getDisplayName()));
        System.out.println("Status: " + (statusFilter == null ? "All" : statusFilter.getLabel()));
        System.out.println("Sort: " + getSortDescription(sortChoice));

        // Show statistics
        printStatusStatistics(sortedRooms);

        // Show room details
        System.out.println("\n--- ROOM DETAILS ---");
        System.out.printf("%-10s %-12s %-22s %-15s %-10s\n",
                "Room No", "Room Type", "Cleaning Status", "Occupancy", "Sequence");
        System.out.println("---------------------------------------------------------------------------------");

        int displayCount = 0;
        for (int i = 0; i < sortedRooms.size(); i++) {
            Room room = sortedRooms.get(i);
            String occupancy = room.isVacant() ? "Vacant" : "Occupied";
            System.out.printf("%-10d %-12s %-22s %-15s %-10d\n",
                    room.getRoomNumber(),
                    room.getRoomType().getDisplayName(),
                    room.getRoomStatus().getLabel(),
                    occupancy,
                    room.getRoomStatus().getSequenceNumber());
            displayCount++;
        }

        // Summary
        System.out.println("\n--- SUMMARY ---");
        System.out.printf("Total Rooms Displayed: %d\n", displayCount);
        System.out.printf("Ready for Check-In: %d\n", countRoomsByStatus(sortedRooms, RoomStatus.READY));
        System.out.printf("Needs Cleaning (Dirty): %d\n", countRoomsByStatus(sortedRooms, RoomStatus.DIRTY));
        System.out.printf("Being Cleaned: %d\n", countRoomsByStatus(sortedRooms, RoomStatus.CLEANING_IN_PROGRESS));
        System.out.printf("Pending Inspection: %d\n", countRoomsByStatus(sortedRooms, RoomStatus.INSPECTED));

        printReportFooter();
        InputUtil.pressEnterToContinue();
    }

    // ============================================================
    // REPORT 2: HOUSEKEEPING ACTION AUDIT REPORT (Simplified)
    // ============================================================
    private void displayActionAuditReport() {
        InputUtil.displayHeader("Housekeeping Action Audit Report");

        // Simple filters
        System.out.println("\n--- FILTER OPTIONS ---");
        System.out.println("Filter by Room Number (Optional):");
        System.out.println("Enter room number (or 0 for all rooms): ");
        int roomFilter = InputUtil.readInt("Room number: ", 0, 510);

        System.out.println("\nFilter by Staff Name (Optional):");
        System.out.println("Enter staff name (or leave empty for all): ");
        String staffFilter = InputUtil.readStringWithSkip("Staff name: ");

        System.out.println("\nSort by:");
        System.out.println("1. Timestamp (Newest First)");
        System.out.println("2. Timestamp (Oldest First)");
        System.out.println("3. Room Number (Ascending)");
        int sortChoice = InputUtil.readInt("Select sort option: ", 1, 3);

        // Get filtered and sorted logs - using the stack directly
        ListInterface<HousekeepingLog> allLogs = stack.toList();
        ListInterface<HousekeepingLog> filteredLogs = reportController.filterAuditLogs(allLogs, roomFilter, staffFilter);
        ListInterface<HousekeepingLog> sortedLogs = reportController.sortAuditLogs(filteredLogs, sortChoice);

        // Display Report
        System.out.println("\n=====================================================================================================");
        System.out.println("                     TARUMT RESORTS - HOUSEKEEPING ACTION AUDIT REPORT                     ");
        System.out.println("=====================================================================================================");

        // Show filter summary
        System.out.println("\n--- FILTERS APPLIED ---");
        System.out.println("Room: " + (roomFilter == 0 ? "All" : roomFilter));
        System.out.println("Staff: " + (staffFilter.isEmpty() ? "All" : staffFilter));
        System.out.println("Sort: " + getAuditSortDescription(sortChoice));
        System.out.println("Total Records: " + sortedLogs.size());

        // Show audit log
        System.out.println("\n-----------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-8s | %-15s | %-20s | %-20s\n",
                "Timestamp", "Room No", "Staff/Supervisor", "Old Status", "New Status");
        System.out.println("-----------------------------------------------------------------------------------------------------");

        if (sortedLogs.size() > 0) {
            for (int i = 0; i < sortedLogs.size(); i++) {
                HousekeepingLog log = sortedLogs.get(i);
                System.out.printf("%-10s | %-8d | %-15s | %-20s | %-20s\n",
                        log.getTimestamp(),
                        log.getRoomNumber(),
                        log.getStaffName(),
                        log.getOldStatus().getLabel(),
                        log.getNewStatus().getLabel());
            }
        } else {
            System.out.println("No matching audit records found.");
        }

        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.println("=====================================================================================================");

        InputUtil.pressEnterToContinue();
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================
    private void printReportHeader(String title) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(title);
        System.out.println("Generated: " + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("=".repeat(80));
    }

    private void printReportFooter() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("END OF REPORT");
        System.out.println("=".repeat(80));
    }

    private void printStatusStatistics(ListInterface<Room> rooms) {
        int dirty = 0, cleaning = 0, inspected = 0, ready = 0;
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            switch (room.getRoomStatus()) {
                case DIRTY:
                    dirty++;
                    break;
                case CLEANING_IN_PROGRESS:
                    cleaning++;
                    break;
                case INSPECTED:
                    inspected++;
                    break;
                case READY:
                    ready++;
                    break;
            }
        }
        System.out.println("\n--- STATUS DISTRIBUTION ---");
        System.out.printf("  %-25s %d\n", "Dirty (Needs Cleaning):", dirty);
        System.out.printf("  %-25s %d\n", "Cleaning In Progress:", cleaning);
        System.out.printf("  %-25s %d\n", "Inspected:", inspected);
        System.out.printf("  %-25s %d\n", "Ready for Check-In:", ready);
    }

    private int countRoomsByStatus(ListInterface<Room> rooms, RoomStatus status) {
        int count = 0;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomStatus() == status) {
                count++;
            }
        }
        return count;
    }

    private Room findRoom(int roomNumber) {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    private RoomStatus getStatusBySequence(int sequence) {
        for (RoomStatus status : RoomStatus.values()) {
            if (status.getSequenceNumber() == sequence) {
                return status;
            }
        }
        return null;
    }

    private String getSortDescription(int choice) {
        switch (choice) {
            case 1:
                return "Room Number (Ascending)";
            case 2:
                return "Room Number (Descending)";
            case 3:
                return "Status (Ascending)";
            case 4:
                return "Status (Descending)";
            default:
                return "Unknown";
        }
    }

    private String getAuditSortDescription(int choice) {
        switch (choice) {
            case 1:
                return "Timestamp (Newest First)";
            case 2:
                return "Timestamp (Oldest First)";
            case 3:
                return "Room Number (Ascending)";
            default:
                return "Unknown";
        }
    }

    private String readStringWithSkip(String prompt) {
        System.out.print(prompt);
        String input = InputUtil.readString("");
        return input.isEmpty() ? "" : input.trim();
    }
}
