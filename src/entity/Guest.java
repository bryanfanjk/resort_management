package entity;

/**
 * Author: <Your Name Here>
 *
 * Guest represents a loyalty-programme member who has requested a room
 * at TARUMT Resorts. This is a plain data object (POJO): it contains NO
 * Scanner input and NO System.out output, per the ECB rules for entity
 * classes (entity objects may only know about other entity objects).
 *
 * Guest implements Comparable<Guest> to define its NATURAL priority
 * ordering, which is exactly the ordering the VIP priority queue (a
 * max-heap) uses to decide who is served first:
 *
 *   1. Higher loyalty tier rank wins (Diamond beats Platinum, etc.)
 *   2. If tiers are equal, higher loyalty points wins
 *   3. If both are equal, EARLIER registration (smaller sequence
 *      number) wins, so two identical-status guests are served in the
 *      order they actually arrived (prevents starvation / unfairness).
 *
 * compareTo() follows the standard Java contract: it returns a positive
 * number when "this" guest has HIGHER priority than "other".
 */
public class Guest implements Comparable<Guest> {

    // Simulates an auto-incrementing "arrival timestamp". Using a simple
    // counter instead of a real clock keeps tie-breaking deterministic
    // and easy to trace/demo.
    private static long sequenceCounter = 0;

    private final String confirmationNumber;
    private String guestName;
    private LoyaltyTier tier;
    private int loyaltyPoints;
    private final long registrationSequence;

    public Guest(String confirmationNumber, String guestName, LoyaltyTier tier, int loyaltyPoints) {
        this.confirmationNumber = confirmationNumber;
        this.guestName = guestName;
        this.tier = tier;
        this.loyaltyPoints = loyaltyPoints;
        this.registrationSequence = sequenceCounter++;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public LoyaltyTier getTier() {
        return tier;
    }

    public void setTier(LoyaltyTier tier) {
        this.tier = tier;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public long getRegistrationSequence() {
        return registrationSequence;
    }

    @Override
    public int compareTo(Guest other) {
        int tierComparison = this.tier.getRank() - other.tier.getRank();
        if (tierComparison != 0) {
            return tierComparison;
        }
        int pointsComparison = this.loyaltyPoints - other.loyaltyPoints;
        if (pointsComparison != 0) {
            return pointsComparison;
        }
        // Earlier arrival (smaller sequence number) must produce a LARGER
        // compareTo result, since smaller sequence = higher priority.
        return (int) (other.registrationSequence - this.registrationSequence);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Guest)) {
            return false;
        }
        Guest other = (Guest) obj;
        return this.confirmationNumber.equals(other.confirmationNumber);
    }

    @Override
    public int hashCode() {
        return confirmationNumber.hashCode();
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Tier: %-9s | Points: %-5d | Arrival Seq: %d",
                confirmationNumber, guestName, tier.getLabel(), loyaltyPoints, registrationSequence);
    }
}
