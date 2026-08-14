package boundary;

import adt.EmptyCollectionException;
import control.VIPAllocationManager;
import control.VIPAllocationManager.TierSummaryResult;
import entity.Guest;
import entity.LoyaltyTier;

import java.util.Scanner;

/**
 * Author: <Your Name Here>
 *
 * VIPAllocationUI is the BOUNDARY class for this subsystem. It is the
 * only class that interacts with the actor (front-desk staff running the
 * console app) and the only class that performs input/output. It
 * delegates ALL business logic to VIPAllocationManager (per ECB rules:
 * boundary objects may only communicate with actors and control
 * objects).
 */
public class VIPAllocationUI {

    private final VIPAllocationManager manager;
    private final Scanner scanner;

    public VIPAllocationUI() {
        this.manager = new VIPAllocationManager();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=========================================");
        System.out.println(" TARUMT RESORTS - VIP & LOYALTY TIER");
        System.out.println(" PRIORITY ROOM ALLOCATION QUEUE");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice();
            switch (choice) {
                case 1: handleRegisterGuest(); break;
                case 2: handlePeekNext(); break;
                case 3: handleServeNext(); break;
                case 4: handleCancelGuest(); break;
                case 5: handleSearchGuest(); break;
                case 6: handleViewAllByName(); break;
                case 7: handlePriorityOrderReport(); break;
                case 8: handleTierSummaryReport(); break;
                case 0:
                    running = false;
                    System.out.println("Exiting VIP Allocation module. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("---------------- MENU ----------------");
        System.out.println("1. Register new VIP/Loyalty guest into queue");
        System.out.println("2. View next guest to be allocated (peek)");
        System.out.println("3. Serve/allocate next guest (removes from queue)");
        System.out.println("4. Cancel a guest's reservation (remove by confirmation no.)");
        System.out.println("5. Search guest by confirmation number");
        System.out.println("6. View all queued guests (alphabetical)");
        System.out.println("7. [Report] Current priority allocation order");
        System.out.println("8. [Report] Tier summary & filtered listing");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    private int readMenuChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void handleRegisterGuest() {
        System.out.print("Confirmation Number (8 digits): ");
        String confirmationNumber = scanner.nextLine().trim();

        System.out.print("Guest Name: ");
        String name = scanner.nextLine().trim();

        LoyaltyTier tier = readTierChoice();

        System.out.print("Loyalty Points: ");
        int points;
        try {
            points = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid points value. Registration cancelled.");
            return;
        }

        manager.registerGuest(confirmationNumber, name, tier, points);
        System.out.println("Guest registered and inserted into the priority queue.");
    }

    private LoyaltyTier readTierChoice() {
        LoyaltyTier[] tiers = LoyaltyTier.values();
        System.out.println("Select Tier:");
        for (int i = 0; i < tiers.length; i++) {
            System.out.println((i + 1) + ". " + tiers[i].getLabel());
        }
        System.out.print("Choice: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= tiers.length) {
                return tiers[choice - 1];
            }
        } catch (NumberFormatException ignored) {
            // falls through to default below
        }
        System.out.println("Invalid choice, defaulting to STANDARD.");
        return LoyaltyTier.STANDARD;
    }

    private void handlePeekNext() {
        try {
            Guest next = manager.peekNextGuest();
            System.out.println("Next guest for allocation: " + next);
        } catch (EmptyCollectionException e) {
            System.out.println("No guests currently waiting in the priority queue.");
        }
    }

    private void handleServeNext() {
        try {
            Guest served = manager.serveNextGuest();
            System.out.println("Guest served/allocated: " + served);
        } catch (EmptyCollectionException e) {
            System.out.println("No guests currently waiting in the priority queue.");
        }
    }

    private void handleCancelGuest() {
        System.out.print("Enter confirmation number to cancel: ");
        String confirmationNumber = scanner.nextLine().trim();
        boolean removed = manager.cancelGuest(confirmationNumber);
        System.out.println(removed
                ? "Guest removed from the priority queue."
                : "No guest found with that confirmation number.");
    }

    private void handleSearchGuest() {
        System.out.print("Enter confirmation number to search: ");
        String confirmationNumber = scanner.nextLine().trim();
        Guest found = manager.findGuestByConfirmation(confirmationNumber);
        System.out.println(found != null ? "Found: " + found : "No guest found with that confirmation number.");
    }

    private void handleViewAllByName() {
        Guest[] guests = manager.getGuestsSortedByName();
        System.out.println("---- All Queued Guests (Alphabetical) ----");
        printGuestArray(guests);
    }

    private void handlePriorityOrderReport() {
        Guest[] ordered = manager.generatePriorityOrderReport();
        System.out.println();
        System.out.println("============================================================");
        System.out.println(" REPORT 1: CURRENT VIP PRIORITY ALLOCATION ORDER");
        System.out.println("============================================================");
        System.out.printf("%-4s %s%n", "No.", "Guest Details");
        System.out.println("------------------------------------------------------------");
        for (int i = 0; i < ordered.length; i++) {
            System.out.printf("%-4d %s%n", (i + 1), ordered[i]);
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Total guests in queue: " + ordered.length);
        System.out.println("============================================================");
    }

    private void handleTierSummaryReport() {
        LoyaltyTier tier = readTierChoice();
        System.out.print("Minimum loyalty points to include in filtered list: ");
        int minPoints;
        try {
            minPoints = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            minPoints = 0;
        }

        TierSummaryResult result = manager.generateTierSummaryReport(tier, minPoints);

        System.out.println();
        System.out.println("============================================================");
        System.out.println(" REPORT 2: TIER SUMMARY & FILTERED GUEST LISTING");
        System.out.println("============================================================");
        System.out.println(" Overall Tier Breakdown (All Guests In Queue)");
        System.out.println("------------------------------------------------------------");
        for (LoyaltyTier t : LoyaltyTier.values()) {
            int count = result.tierCounts[t.ordinal()];
            int total = result.tierPointTotals[t.ordinal()];
            double average = (count == 0) ? 0 : (double) total / count;
            System.out.printf("%-10s | Count: %-4d | Avg Points: %.1f%n", t.getLabel(), count, average);
        }
        System.out.println("------------------------------------------------------------");
        System.out.println(" Filtered Listing: Tier = " + tier.getLabel() + ", Points >= " + minPoints);
        System.out.println(" (sorted by loyalty points, highest first)");
        System.out.println("------------------------------------------------------------");
        if (result.filteredMatches.length == 0) {
            System.out.println(" No guests match this filter.");
        } else {
            for (int i = 0; i < result.filteredMatches.length; i++) {
                System.out.printf("%-4d %s%n", (i + 1), result.filteredMatches[i]);
            }
        }
        System.out.println("============================================================");
    }

    private void printGuestArray(Guest[] guests) {
        if (guests.length == 0) {
            System.out.println("(Queue is empty)");
            return;
        }
        for (int i = 0; i < guests.length; i++) {
            System.out.println((i + 1) + ". " + guests[i]);
        }
    }
}
