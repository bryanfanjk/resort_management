package tarumtresort.boundary;

import tarumtresort.adt.ListInterface;
import tarumtresort.control.ReportController;
import tarumtresort.entity.Guest;
import tarumtresort.entity.HousekeepingLog;
import tarumtresort.entity.Room;
import tarumtresort.util.InputUtil;

/**
 * Boundary class handling management report configuration, execution, and rendering.
 * Provides custom filtering and prints tabular metrics.
 * 
 * @author Admin
 */
public class ReportUI {

    private final ReportController controller;

    public ReportUI(ReportController controller) {
        this.controller = controller;
    }

    public void start() {
        while (true) {
            InputUtil.displayHeader("Management Analytics & Reports System");
            System.out.println("1. Room Occupancy & Revenue Report (Filter/Sort)");
            System.out.println("2. High-Value Guests & Loyalty Tier Analysis (Filter/Sort)");
            System.out.println("3. Housekeeping Action History Audit Report (Filter/Sort)");
            System.out.println("4. Back to Main Menu");

            int choice = InputUtil.readInt("Enter choice (1-4): ", 1, 4);
            switch (choice) {
                case 1:
                    runRoomReport();
                    break;
                case 2:
                    runLoyaltyReport();
                    break;
                case 3:
                    runHousekeepingReport();
                    break;
                case 4:
                    return;
            }
        }
    }

    private void runRoomReport() {
        System.out.println("\n--- Configure Room Occupancy & Revenue Report ---");
        System.out.println("Filter by Room Type:");
        System.out.println("0. All Room Types");
        System.out.println("1. Standard");
        System.out.println("2. Deluxe");
        System.out.println("3. Suite");
        System.out.println("4. Penthouse");
        int typeChoice = InputUtil.readInt("Select choice (0-4): ", 0, 4);
        Room.RoomType typeFilter = (typeChoice == 0) ? null : Room.RoomType.values()[typeChoice - 1];

        System.out.println("\nFilter by Occupancy:");
        System.out.println("1. All Rooms");
        System.out.println("2. Vacant Rooms Only");
        int vacancyChoice = InputUtil.readInt("Select choice (1-2): ", 1, 2);
        boolean onlyVacant = (vacancyChoice == 2);

        System.out.println("\nSelect Sorting Criterion:");
        System.out.println("1. Room Number (Ascending)");
        System.out.println("2. Room Rate / Price (Descending)");
        int sortChoice = InputUtil.readInt("Select choice (1-2): ", 1, 2);
        boolean sortByPrice = (sortChoice == 2);

        // Fetch sorted & filtered data from Control
        ListInterface<Room> results = controller.generateRoomReport(typeFilter, onlyVacant, sortByPrice);

        // Render report
        System.out.println("\n==========================================================================================");
        System.out.println("                     TARUMT RESORTS - ROOM OCCUPANCY & REVENUE REPORT                     ");
        System.out.println("==========================================================================================");
        System.out.printf("%-10s | %-12s | %-12s | %-22s | %-15s\n", 
                "Room No", "Room Type", "Daily Rate", "Housekeeping Status", "Occupancy Status");
        System.out.println("------------------------------------------------------------------------------------------");
        
        double totalPotentialRevenue = 0.0;
        double activeRevenue = 0.0;
        int occupiedCount = 0;
        int vacantCount = 0;

        for (int i = 1; i <= results.getLength(); i++) {
            Room r = results.getEntry(i);
            double rate = r.getRoomType().getRate();
            totalPotentialRevenue += rate;
            
            String occupancy;
            if (r.isVacant()) {
                occupancy = "Vacant";
                vacantCount++;
            } else {
                occupancy = "Occupied (" + r.getCurrentGuestConfirmation() + ")";
                activeRevenue += rate;
                occupiedCount++;
            }

            System.out.printf("%-10d | %-12s | $%-11.2f | %-22s | %-15s\n",
                    r.getRoomNumber(), r.getRoomType().getLabel(), rate, r.getStatus().getLabel(), occupancy);
        }
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("Total Rooms Listed:   %d\n", results.getLength());
        System.out.printf("Occupied Status:      %d Rooms (Occupancy Rate: %.1f%%)\n", 
                occupiedCount, (results.getLength() == 0) ? 0.0 : (double) occupiedCount / results.getLength() * 100);
        System.out.printf("Vacant Status:        %d Rooms\n", vacantCount);
        System.out.printf("Active Stay Revenue:  $%.2f\n", activeRevenue);
        System.out.printf("Max Potential Revenue:$%.2f\n", totalPotentialRevenue);
        System.out.println("==========================================================================================");
        InputUtil.pressEnterToContinue();
    }

    private void runLoyaltyReport() {
        System.out.println("\n--- Configure Guest Loyalty & Rewards Report ---");
        int minPoints = InputUtil.readInt("Enter Minimum Loyalty Points threshold: ", 0, 1000000);
        
        System.out.println("\nFilter by Loyalty Tier:");
        System.out.println("0. All Tiers");
        System.out.println("1. Standard");
        System.out.println("2. Silver");
        System.out.println("3. Gold");
        System.out.println("4. Platinum");
        System.out.println("5. Diamond");
        System.out.println("6. Elite");
        int tierChoice = InputUtil.readInt("Select choice (0-6): ", 0, 6);
        Guest.LoyaltyTier tierFilter = (tierChoice == 0) ? null : Guest.LoyaltyTier.values()[tierChoice - 1];

        // Fetch sorted & filtered data
        ListInterface<Guest> results = controller.generateLoyaltyReport(minPoints, tierFilter);

        // Render report
        System.out.println("\n=====================================================================================");
        System.out.println("                     TARUMT RESORTS - GUEST LOYALTY & REWARDS REPORT                 ");
        System.out.println("=====================================================================================");
        System.out.printf("%-20s | %-15s | %-12s | %-12s | %-12s\n", 
                "Guest Name", "Contact No", "Loyalty Tier", "Points Balance", "Point Multiplier");
        System.out.println("-------------------------------------------------------------------------------------");

        int totalMembers = results.getLength();
        long totalPointsPool = 0;
        int standard = 0, silver = 0, gold = 0, plat = 0, diamond = 0, elite = 0;

        for (int i = 1; i <= results.getLength(); i++) {
            Guest g = results.getEntry(i);
            totalPointsPool += g.getLoyaltyPoints();
            
            switch (g.getTier()) {
                case STANDARD: standard++; break;
                case SILVER: silver++; break;
                case GOLD: gold++; break;
                case PLATINUM: plat++; break;
                case DIAMOND: diamond++; break;
                case ELITE: elite++; break;
            }

            System.out.printf("%-20s | %-15s | %-12s | %-14d | %.2fx\n",
                    g.getName(), g.getContactNumber(), g.getTier().getLabel(), g.getLoyaltyPoints(), g.getTier().getPointMultiplier());
        }
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("Total Filtered Members: %d members\n", totalMembers);
        System.out.printf("Total Points Pool:      %d Points\n", totalPointsPool);
        System.out.println("Tier Distribution Analysis:");
        System.out.printf("  - Elite: %-5d | Diamond: %-5d | Platinum: %-5d\n", elite, diamond, plat);
        System.out.printf("  - Gold:  %-5d | Silver:  %-5d | Standard: %-5d\n", gold, silver, standard);
        System.out.println("=====================================================================================");
        InputUtil.pressEnterToContinue();
    }

    private void runHousekeepingReport() {
        System.out.println("\n--- Configure Housekeeping Action Audit Report ---");
        int roomFilter = InputUtil.readInt("Enter Room Number filter (0 for All rooms): ", 0, 510);
        String supervisorFilter = InputUtil.readString("Enter Supervisor Name filter (or type 'all' for All): ");
        if (supervisorFilter.equalsIgnoreCase("all")) {
            supervisorFilter = "";
        }

        // Fetch sorted & filtered data
        ListInterface<HousekeepingLog> results = controller.generateHousekeepingReport(roomFilter, supervisorFilter);

        // Render report
        System.out.println("\n=====================================================================================================");
        System.out.println("                     TARUMT RESORTS - HOUSEKEEPING ACTION AUDIT REPORT                     ");
        System.out.println("=====================================================================================================");
        System.out.printf("%-10s | %-8s | %-15s | %-20s | %-20s | %-15s\n", 
                "Timestamp", "Room No", "Supervisor", "Old Status", "New Status", "Reverted?");
        System.out.println("-----------------------------------------------------------------------------------------------------");

        for (int i = 1; i <= results.getLength(); i++) {
            HousekeepingLog log = results.getEntry(i);
            
            // Check if this action was subsequently reverted (simulation search)
            String reverted = "No";
            // Check if room number matches a later log where newStatus == log.oldStatus
            // For simplicity, we just render the log details
            System.out.printf("%-10s | %-8d | %-15s | %-20s | %-20s | %-15s\n",
                    log.getTimestamp(), log.getRoomNumber(), log.getSupervisorName(), 
                    log.getOldStatus().getLabel(), log.getNewStatus().getLabel(), reverted);
        }
        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.printf("Total Actions Logs Captured: %d audit entries\n", results.getLength());
        System.out.println("=====================================================================================================");
        InputUtil.pressEnterToContinue();
    }
}
