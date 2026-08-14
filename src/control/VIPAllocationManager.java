package control;

import adt.EmptyCollectionException;
import adt.MaxHeapPriorityQueue;
import adt.PriorityQueueADT;
import entity.Guest;
import entity.GuestNameComparator;
import entity.GuestPriorityComparator;
import entity.LoyaltyTier;
import utility.SearchUtility;
import utility.SortUtility;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Author: <Your Name Here>
 *
 * VIPAllocationManager is the CONTROL class for the VIP & Loyalty Tier
 * Priority Allocation subsystem. It owns the priority queue ADT and
 * implements all business logic: registering guests, serving the next
 * highest-priority guest, cancelling, searching, and generating reports.
 *
 * Per ECB rules, this class contains NO input/output statements - all
 * interaction with the actor happens through VIPAllocationUI, which
 * calls these methods and prints their results.
 *
 * Note the field is declared using the INTERFACE type (PriorityQueueADT)
 * rather than the concrete MaxHeapPriorityQueue class. This means every
 * method in this class only relies on the operations the ADT promises,
 * so the underlying implementation could be swapped later without
 * touching any other code in this class.
 */
public class VIPAllocationManager {

    private final PriorityQueueADT<Guest> vipQueue;

    public VIPAllocationManager() {
        this.vipQueue = new MaxHeapPriorityQueue<>();
        loadSampleData();
    }

    // ------------------------------------------------------------------
    // Core operations
    // ------------------------------------------------------------------

    public void registerGuest(String confirmationNumber, String guestName, LoyaltyTier tier, int loyaltyPoints) {
        Guest guest = new Guest(confirmationNumber, guestName, tier, loyaltyPoints);
        vipQueue.insert(guest);
    }

    public Guest peekNextGuest() throws EmptyCollectionException {
        return vipQueue.peekHighestPriority();
    }

    public Guest serveNextGuest() throws EmptyCollectionException {
        return vipQueue.removeHighestPriority();
    }

    public boolean cancelGuest(String confirmationNumber) {
        Guest target = findGuestByConfirmation(confirmationNumber);
        if (target == null) {
            return false;
        }
        return vipQueue.remove(target);
    }

    public int getQueueSize() {
        return vipQueue.size();
    }

    public boolean isQueueEmpty() {
        return vipQueue.isEmpty();
    }

    // ------------------------------------------------------------------
    // Searching
    // ------------------------------------------------------------------

    public Guest findGuestByConfirmation(String confirmationNumber) {
        Guest[] snapshot = takeSnapshot();
        int index = SearchUtility.linearSearchByConfirmationNumber(snapshot, snapshot.length, confirmationNumber);
        return (index == -1) ? null : snapshot[index];
    }

    public Guest[] getGuestsSortedByName() {
        Guest[] snapshot = takeSnapshot();
        SortUtility.quickSort(snapshot, snapshot.length, new GuestNameComparator());
        return snapshot;
    }

    // ------------------------------------------------------------------
    // Reports
    // ------------------------------------------------------------------

    /**
     * Report 1: Current Priority Queue Order.
     * Takes a non-destructive snapshot of the heap (via the ADT's
     * iterator), sorts it with our own quicksort using the natural
     * priority ordering, and returns it. This is the exact order guests
     * would be served in if no more guests were added or removed.
     */
    public Guest[] generatePriorityOrderReport() {
        Guest[] snapshot = takeSnapshot();
        Comparator<Guest> priorityOrder = new GuestPriorityComparator();
        SortUtility.quickSort(snapshot, snapshot.length, priorityOrder);
        return snapshot;
    }

    /**
     * Report 2: Tier Summary & Filtered Listing.
     * Filters the queue down to guests of a given tier whose points meet
     * or exceed a minimum threshold (multi-criteria filter), sorts the
     * matches by points descending, and also computes aggregate
     * statistics (count and average points) per tier across the WHOLE
     * queue for a management-style summary section.
     */
    public TierSummaryResult generateTierSummaryReport(LoyaltyTier filterTier, int minimumPoints) {
        Guest[] snapshot = takeSnapshot();

        // --- Filtering (tier AND minimum points) ---
        Guest[] matches = new Guest[snapshot.length];
        int matchCount = 0;
        for (Guest g : snapshot) {
            if (g.getTier() == filterTier && g.getLoyaltyPoints() >= minimumPoints) {
                matches[matchCount++] = g;
            }
        }
        Guest[] trimmedMatches = new Guest[matchCount];
        System.arraycopy(matches, 0, trimmedMatches, 0, matchCount);

        // --- Sorting the matches by points (descending) ---
        SortUtility.quickSort(trimmedMatches, trimmedMatches.length, new PointsDescendingComparator());

        // --- Aggregate statistics per tier across the whole queue ---
        int tierTypeCount = LoyaltyTier.values().length;
        int[] tierCounts = new int[tierTypeCount];
        int[] tierPointTotals = new int[tierTypeCount];
        for (Guest g : snapshot) {
            int rankIndex = g.getTier().ordinal();
            tierCounts[rankIndex]++;
            tierPointTotals[rankIndex] += g.getLoyaltyPoints();
        }

        return new TierSummaryResult(trimmedMatches, tierCounts, tierPointTotals);
    }

    // ------------------------------------------------------------------
    // Helpers1
    
    // ------------------------------------------------------------------

    private Guest[] takeSnapshot() {
        Guest[] snapshot = new Guest[vipQueue.size()];
        Iterator<Guest> it = vipQueue.getIterator();
        int i = 0;
        while (it.hasNext()) {
            snapshot[i++] = it.next();
        }
        return snapshot;
    }

    private void loadSampleData() {
        // Hard-coded seed data - no database or file storage used, as
        // required. Includes a deliberate tie (two Platinum guests with
        // identical points) to demonstrate the arrival-order tiebreaker.
        registerGuest("10234567", "Aiman Hafiz", LoyaltyTier.DIAMOND, 8200);
        registerGuest("10234568", "Wei Ling Tan", LoyaltyTier.GOLD, 3100);
        registerGuest("10234569", "Nur Ain Batrisyia", LoyaltyTier.DIAMOND, 5400);
        registerGuest("10234570", "Raj Kumar", LoyaltyTier.STANDARD, 150);
        registerGuest("10234571", "Siti Zulaikha", LoyaltyTier.SILVER, 1800);
        registerGuest("10234572", "Chen Jia Hao", LoyaltyTier.DIAMOND, 5400);
        registerGuest("10234573", "Farah Adila", LoyaltyTier.DIAMOND, 7600);
        registerGuest("10234574", "Muhammad Iqbal", LoyaltyTier.GOLD, 2950);
    }

    // ------------------------------------------------------------------
    // Small data holder returned by generateTierSummaryReport()
    // ------------------------------------------------------------------

    public static class TierSummaryResult {
        public final Guest[] filteredMatches;
        public final int[] tierCounts;
        public final int[] tierPointTotals;

        public TierSummaryResult(Guest[] filteredMatches, int[] tierCounts, int[] tierPointTotals) {
            this.filteredMatches = filteredMatches;
            this.tierCounts = tierCounts;
            this.tierPointTotals = tierPointTotals;
        }
    }

    /** Local comparator used only by the tier summary report. */
    private static class PointsDescendingComparator implements Comparator<Guest> {
        @Override
        public int compare(Guest g1, Guest g2) {
            return g2.getLoyaltyPoints() - g1.getLoyaltyPoints();
        }
    }
}
