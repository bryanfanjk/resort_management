package utility;

import entity.Guest;

/**
 * Author: <Your Name Here>
 *
 * SearchUtility is a static utility class providing a hand-written
 * linear search over a Guest array. A linear scan is used (rather than
 * binary search) because the source data - a snapshot straight out of
 * the heap - is not sorted by confirmation number; sorting purely to
 * enable a single lookup would cost more than the O(n) scan it replaces.
 */
public class SearchUtility {

    private SearchUtility() {
        // Utility class - never instantiated.
    }

    /**
     * @return the index of the guest whose confirmation number matches,
     * or -1 if no match is found within the first {@code length} slots
     * of the array.
     */
    public static int linearSearchByConfirmationNumber(Guest[] array, int length, String confirmationNumber) {
        for (int i = 0; i < length; i++) {
            if (array[i] != null && array[i].getConfirmationNumber().equals(confirmationNumber)) {
                return i;
            }
        }
        return -1;
    }
}
