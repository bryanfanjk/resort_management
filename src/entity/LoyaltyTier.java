package entity;

/**
 * Author: <Your Name Here>
 *
 * LoyaltyTier represents the loyalty programme tiers recognised by
 * TARUMT Resorts. Each tier carries an integer rank; a HIGHER rank means
 * HIGHER priority when guests are allocated rooms. This ranking is the
 * primary factor used by Guest.compareTo() to order guests inside the
 * priority queue.
 */
public enum LoyaltyTier {

    STANDARD(1, "Standard"),
    SILVER(2, "Silver"),
    GOLD(3, "Gold"),
    DIAMOND(4, "Diamond");

    private final int rank;
    private final String label;

    LoyaltyTier(int rank, String label) {
        this.rank = rank;
        this.label = label;
    }

    public int getRank() {
        return rank;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
