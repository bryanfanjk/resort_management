package entity;

import java.util.Comparator;

/**
 * Author: <Your Name Here>
 *
 * Alternate ordering strategy: alphabetical by guest name. This is
 * unrelated to allocation priority - it exists purely so the UI can
 * offer a "browse all guests" view that isn't in heap/priority order,
 * demonstrating that the same collection can be viewed through
 * different Comparators without changing its internal structure.
 */
public class GuestNameComparator implements Comparator<Guest> {

    @Override
    public int compare(Guest g1, Guest g2) {
        return g1.getGuestName().compareToIgnoreCase(g2.getGuestName());
    }
}
