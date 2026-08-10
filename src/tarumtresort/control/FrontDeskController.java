package tarumtresort.control;

import tarumtresort.adt.BSTInterface;
import tarumtresort.adt.ListInterface;
import tarumtresort.entity.Booking;
import tarumtresort.entity.Guest;
import tarumtresort.entity.Room;

/**
 * Control class managing front-desk room inquiries and guest search.
 * Utilizes a Binary Search Tree (BST) for fast search and range retrieval.
 * 
 * @author Admin
 */
public class FrontDeskController {

    private final BSTInterface<String, Booking> bookingTree;
    private final ListInterface<Room> rooms;

    public FrontDeskController(BSTInterface<String, Booking> bookingTree, ListInterface<Room> rooms) {
        this.bookingTree = bookingTree;
        this.rooms = rooms;
    }

    /**
     * Instantly retrieves complete booking details using an 8-digit confirmation number.
     * 
     * @param confirmationNumber The confirmation number (String).
     * @return The Booking record, or null if not found.
     */
    public Booking searchBooking(String confirmationNumber) {
        if (confirmationNumber == null || confirmationNumber.trim().isEmpty()) {
            return null;
        }
        return bookingTree.search(confirmationNumber);
    }

    /**
     * Checks if a confirmation number exists.
     * 
     * @param confirmationNumber The confirmation number.
     * @return true if exists, false otherwise.
     */
    public boolean containsBooking(String confirmationNumber) {
        return bookingTree.contains(confirmationNumber);
    }

    /**
     * Performs a range-based booking search between two confirmation numbers.
     * Invokes the non-trivial custom `getInOrderRange` tree method.
     * 
     * @param startCode Lower bound confirmation number.
     * @param endCode Upper bound confirmation number.
     * @return List of bookings within the specified range.
     */
    public ListInterface<Booking> searchBookingsByRange(String startCode, String endCode) {
        return bookingTree.getInOrderRange(startCode, endCode);
    }

    /**
     * Returns all registered bookings sorted in-order by confirmation number.
     * 
     * @return List of bookings.
     */
    public ListInterface<Booking> getAllBookingsSorted() {
        return bookingTree.getInOrderValues();
    }

    /**
     * Returns the list of hotel rooms for availability queries.
     * 
     * @return List of rooms.
     */
    public ListInterface<Room> getRooms() {
        return rooms;
    }

    /**
     * Gets the discount rate based on a guest's loyalty tier.
     * 
     * @param tier Guest's loyalty tier.
     * @return Discount multiplier (e.g., 0.10 for 10% discount).
     */
    public double getDiscountRate(Guest.LoyaltyTier tier) {
        if (tier == null) return 0.0;
        switch (tier) {
            case SILVER: return 0.05;
            case GOLD: return 0.10;
            case PLATINUM: return 0.15;
            case DIAMOND: return 0.20;
            case ELITE: return 0.25;
            default: return 0.0;
        }
    }

    /**
     * Redeems loyalty points for a guest.
     * 
     * @param guest The guest redeeming points.
     * @param points The number of points to redeem.
     * @return true if successful, false otherwise.
     */
    public boolean redeemGuestPoints(Guest guest, int points) {
        if (guest == null) return false;
        return guest.redeemPoints(points);
    }
}
