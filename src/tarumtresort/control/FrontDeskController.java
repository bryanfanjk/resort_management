package tarumtresort.control;

import tarumtresort.adt.BSTInterface;
import tarumtresort.adt.ListInterface;
import tarumtresort.entity.Booking;

/**
 * Control class managing front-desk room inquiries and guest search.
 * Utilizes a Binary Search Tree (BST) for fast search and range retrieval.
 * 
 * @author Admin
 */
public class FrontDeskController {

    private final BSTInterface<String, Booking> bookingTree;

    public FrontDeskController(BSTInterface<String, Booking> bookingTree) {
        this.bookingTree = bookingTree;
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
}
