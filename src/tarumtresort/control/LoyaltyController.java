package tarumtresort.control;

import tarumtresort.adt.BSTInterface;
import tarumtresort.adt.ListInterface;
import tarumtresort.entity.Guest;

/**
 * Control class managing loyalty profiles, rewards accumulation, and point redemption.
 * 
 * @author Admin
 */
public class LoyaltyController {

    private final ListInterface<Guest> guests;
    private final BSTInterface<String, Guest> guestTree; // Lookup guests by contact number

    public LoyaltyController(ListInterface<Guest> guests, BSTInterface<String, Guest> guestTree) {
        this.guests = guests;
        this.guestTree = guestTree;
    }

    /**
     * Registers a new loyalty member.
     * 
     * @param name Guest name.
     * @param contact Contact number.
     * @return The Guest profile.
     */
    public Guest registerMember(String name, String contact) {
        if (guestTree.contains(contact)) {
            return guestTree.search(contact);
        }
        Guest newGuest = new Guest(name, contact);
        guests.add(newGuest);
        guestTree.insert(contact, newGuest);
        return newGuest;
    }

    /**
     * Searches a member by their unique contact number.
     * 
     * @param contact Contact number.
     * @return The Guest profile, or null if not found.
     */
    public Guest findMember(String contact) {
        return guestTree.search(contact);
    }

    /**
     * Processes points accumulation from a hotel stay.
     * 
     * @param contact Guest contact number.
     * @param billAmount Amount spent.
     * @return String status describing points earned and tier upgrades.
     */
    public String addPointsForStay(String contact, double billAmount) {
        Guest guest = findMember(contact);
        if (guest == null) {
            return "Member not found.";
        }

        Guest.LoyaltyTier oldTier = guest.getTier();
        int basePoints = (int) (billAmount * 0.1); // 1 point per $10 spent
        int beforePoints = guest.getLoyaltyPoints();
        guest.addPoints(basePoints);
        int afterPoints = guest.getLoyaltyPoints();
        int pointsEarned = afterPoints - beforePoints;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Successfully added %d points (with multiplier) for spending $%.2f. Current Points: %d. ",
                pointsEarned, billAmount, afterPoints));

        if (guest.getTier() != oldTier) {
            sb.append(String.format("\n*** CONGRATULATIONS! Upgraded from %s to %s Tier! ***",
                    oldTier.getLabel(), guest.getTier().getLabel()));
        }

        return sb.toString();
    }

    /**
     * Redeems points for discounts or reward packages.
     * 
     * @param contact Guest contact number.
     * @param pointsToRedeem Points to spend.
     * @param rewardDescription Description of the reward.
     * @return Result message.
     */
    public String redeemReward(String contact, int pointsToRedeem, String rewardDescription) {
        Guest guest = findMember(contact);
        if (guest == null) {
            return "Member not found.";
        }

        boolean success = guest.redeemPoints(pointsToRedeem);
        if (success) {
            return String.format("Redemption SUCCESSFUL: Redeemed %d points for '%s'. Remaining Points: %d. New Tier: %s.",
                    pointsToRedeem, rewardDescription, guest.getLoyaltyPoints(), guest.getTier().getLabel());
        } else {
            return String.format("Redemption FAILED: Insufficient points. Required: %d, Available: %d.",
                    pointsToRedeem, guest.getLoyaltyPoints());
        }
    }
}
