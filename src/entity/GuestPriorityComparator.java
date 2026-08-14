package entity;

import java.util.Comparator;

/**
 * Author: <Your Name Here>
 *
 * Wraps Guest's natural priority ordering (tier, then points, then
 * arrival order) as a Comparator, so that SortUtility - which is written
 * generically against Comparator rather than Comparable - can reuse the
 * exact same ordering the priority queue uses internally.
 *
 * This demonstrates the allowed use of the Comparator interface
 * alongside Comparable (per the assignment QNA: Comparator is allowed,
 * Collections.sort() is not).
 */
public class GuestPriorityComparator implements Comparator<Guest> {

    @Override
    public int compare(Guest g1, Guest g2) {
        return g1.compareTo(g2);
    }
}
