package tarumtresort.entity;

/**
 * Entity class representing a hotel guest and their loyalty metadata.
 * 
 * @author Admin
 */
public class Guest {

    public enum LoyaltyTier {
        STANDARD(0, "Standard", 1.0),
        SILVER(1, "Silver", 1.1),
        GOLD(2, "Gold", 1.25),
        PLATINUM(3, "Platinum", 1.5),
        DIAMOND(4, "Diamond", 1.8),
        ELITE(5, "Elite", 2.0);

        private final int rank;
        private final String label;
        private final double pointMultiplier;

        LoyaltyTier(int rank, String label, double pointMultiplier) {
            this.rank = rank;
            this.label = label;
            this.pointMultiplier = pointMultiplier;
        }

        public int getRank() {
            return rank;
        }

        public String getLabel() {
            return label;
        }

        public double getPointMultiplier() {
            return pointMultiplier;
        }
    }

    private String name;
    private String contactNumber;
    private int loyaltyPoints;
    private LoyaltyTier tier;

    public Guest(String name, String contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.loyaltyPoints = 0;
        this.tier = LoyaltyTier.STANDARD;
    }

    public Guest(String name, String contactNumber, int loyaltyPoints, LoyaltyTier tier) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.loyaltyPoints = loyaltyPoints;
        this.tier = tier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
        updateTier();
    }

    public LoyaltyTier getTier() {
        return tier;
    }

    public void setTier(LoyaltyTier tier) {
        this.tier = tier;
    }

    /**
     * Adds points to the guest profile and automatically checks for tier progression.
     * 
     * @param basePoints The base points earned.
     */
    public void addPoints(int basePoints) {
        int earned = (int) (basePoints * tier.getPointMultiplier());
        this.loyaltyPoints += earned;
        updateTier();
    }

    /**
     * Deducts points for redemptions.
     * 
     * @param pointsToRedeem Points to redeem.
     * @return true if successful, false if insufficient points.
     */
    public boolean redeemPoints(int pointsToRedeem) {
        if (this.loyaltyPoints >= pointsToRedeem) {
            this.loyaltyPoints -= pointsToRedeem;
            updateTier();
            return true;
        }
        return false;
    }

    private void updateTier() {
        if (loyaltyPoints >= 10000) {
            tier = LoyaltyTier.ELITE;
        } else if (loyaltyPoints >= 5000) {
            tier = LoyaltyTier.DIAMOND;
        } else if (loyaltyPoints >= 2500) {
            tier = LoyaltyTier.PLATINUM;
        } else if (loyaltyPoints >= 1000) {
            tier = LoyaltyTier.GOLD;
        } else if (loyaltyPoints >= 300) {
            tier = LoyaltyTier.SILVER;
        } else {
            tier = LoyaltyTier.STANDARD;
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s) | Tier: %s | Points: %d", name, contactNumber, tier.getLabel(), loyaltyPoints);
    }
}
