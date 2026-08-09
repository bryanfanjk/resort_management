package tarumtresort.boundary;

import tarumtresort.control.LoyaltyController;
import tarumtresort.entity.Guest;
import tarumtresort.util.InputUtil;

/**
 * Boundary class handling interactions for Loyalty & Reward Profiles.
 * 
 * @author Admin
 */
public class LoyaltyUI {

    private final LoyaltyController controller;

    public LoyaltyUI(LoyaltyController controller) {
        this.controller = controller;
    }

    public void start() {
        while (true) {
            InputUtil.displayHeader("Loyalty and Rewards program");
            System.out.println("1. Register Loyalty Member Profile");
            System.out.println("2. Lookup Member Points & Loyalty Tier");
            System.out.println("3. Accumulate Points from Hotel Bill");
            System.out.println("4. Process Rewards Points Redemption");
            System.out.println("5. View Personalized Member Promotions");
            System.out.println("6. Back to Main Menu");

            int choice = InputUtil.readInt("Enter choice (1-6): ", 1, 6);
            switch (choice) {
                case 1:
                    registerMember();
                    break;
                case 2:
                    lookupMember();
                    break;
                case 3:
                    accumulatePoints();
                    break;
                case 4:
                    redeemPoints();
                    break;
                case 5:
                    viewPromotions();
                    break;
                case 6:
                    return;
            }
        }
    }

    private void registerMember() {
        System.out.println("\n--- Register Loyalty Member ---");
        String name = InputUtil.readString("Enter Member Name: ");
        String contact = InputUtil.readContactNumber("Enter Contact Number: ");

        Guest guest = controller.registerMember(name, contact);
        System.out.println("\nSUCCESS: Loyalty Profile Created/Retrieved!");
        System.out.println(guest);
        InputUtil.pressEnterToContinue();
    }

    private void lookupMember() {
        System.out.println("\n--- Lookup Member Details ---");
        String contact = InputUtil.readContactNumber("Enter Member Contact Number: ");

        Guest guest = controller.findMember(contact);
        if (guest != null) {
            System.out.println("\n=======================================================");
            System.out.printf(" Member Name:   %s\n", guest.getName());
            System.out.printf(" Contact Number: %s\n", guest.getContactNumber());
            System.out.printf(" Loyalty Tier:   %s (Multiplier: %.2fx)\n", guest.getTier().getLabel(), guest.getTier().getPointMultiplier());
            System.out.printf(" Reward Points:  %d Points\n", guest.getLoyaltyPoints());
            
            // Show tier progress info
            int points = guest.getLoyaltyPoints();
            String nextTier = "MAX";
            int req = 0;
            if (points < 300) { nextTier = "Silver"; req = 300 - points; }
            else if (points < 1000) { nextTier = "Gold"; req = 1000 - points; }
            else if (points < 2500) { nextTier = "Platinum"; req = 2500 - points; }
            else if (points < 5000) { nextTier = "Diamond"; req = 5000 - points; }
            else if (points < 10000) { nextTier = "Elite"; req = 10000 - points; }
            
            System.out.printf(" Next Tier Goal: %s (requires %d more points)\n", nextTier, req);
            System.out.println("=======================================================");
        } else {
            System.out.println("\nERROR: No loyalty member found with that contact number.");
        }
        InputUtil.pressEnterToContinue();
    }

    private void accumulatePoints() {
        System.out.println("\n--- Accumulate Stay Points ---");
        String contact = InputUtil.readContactNumber("Enter Member Contact Number: ");
        double bill = InputUtil.readDouble("Enter Total Billing Bill Amount ($): ", 0.0);

        String result = controller.addPointsForStay(contact, bill);
        System.out.println("\n" + result);
        InputUtil.pressEnterToContinue();
    }

    private void redeemPoints() {
        System.out.println("\n--- Redeem Reward Voucher ---");
        String contact = InputUtil.readContactNumber("Enter Member Contact Number: ");
        
        Guest guest = controller.findMember(contact);
        if (guest == null) {
            System.out.println("ERROR: Loyalty member profile not found.");
            InputUtil.pressEnterToContinue();
            return;
        }

        System.out.printf("Welcome, %s! Available Points: %d\n", guest.getName(), guest.getLoyaltyPoints());
        System.out.println("\nAvailable Redemption Packages:");
        System.out.println("1. $10 Dining voucher (200 Points)");
        System.out.println("2. $30 Room Discount Coupon (500 Points)");
        System.out.println("3. $100 Resort Cash Voucher (1500 Points)");
        System.out.println("4. Free Deluxe Suite Upgrade (2500 Points)");
        System.out.println("5. Premium Spa Massage Package (4000 Points)");
        
        int choice = InputUtil.readInt("Select package (1-5): ", 1, 5);
        
        int points = 0;
        String reward = "";
        switch (choice) {
            case 1: points = 200; reward = "$10 Dining Voucher"; break;
            case 2: points = 500; reward = "$30 Room Discount Coupon"; break;
            case 3: points = 1500; reward = "$100 Resort Cash Voucher"; break;
            case 4: points = 2500; reward = "Free Deluxe Suite Upgrade"; break;
            case 5: points = 4000; reward = "Premium Spa Massage Package"; break;
        }

        String result = controller.redeemReward(contact, points, reward);
        System.out.println("\n" + result);
        InputUtil.pressEnterToContinue();
    }

    private void viewPromotions() {
        System.out.println("\n--- View Targeted Loyalty Offers ---");
        String contact = InputUtil.readContactNumber("Enter Member Contact Number: ");
        
        Guest guest = controller.findMember(contact);
        if (guest == null) {
            System.out.println("ERROR: Member profile not found.");
            InputUtil.pressEnterToContinue();
            return;
        }

        System.out.printf("\nPersonalized Promotions for %s [Tier: %s]:\n", guest.getName(), guest.getTier().getLabel());
        System.out.println("==========================================================================");
        switch (guest.getTier()) {
            case STANDARD:
                System.out.println("- Double your welcome stay points! Earn 2x points on next check-in.");
                System.out.println("- Upgrade to Silver Tier in just 300 points to unlock room discount vouchers!");
                break;
            case SILVER:
                System.out.println("- Silver Exclusive: complimentary welcome soft drink at our Azure Bar.");
                System.out.println("- 10% off resort laundry services during your stay.");
                break;
            case GOLD:
                System.out.println("- Gold Exclusive: Late checkout up to 1:00 PM (subject to availability).");
                System.out.println("- 15% discount on all spa bookings.");
                break;
            case PLATINUM:
                System.out.println("- Platinum Exclusive: Guaranteed late checkout up to 2:00 PM.");
                System.out.println("- Free continental breakfast buffet for up to 2 guests.");
                break;
            case DIAMOND:
                System.out.println("- Diamond Exclusive: Priority room allocation & 30% points bonus.");
                System.out.println("- Welcome bottle of champagne on arrival in your suite.");
                System.out.println("- Dedicated VIP concierge lane access.");
                break;
            case ELITE:
                System.out.println("- Elite Exclusive: Ultimate VIP privileges.");
                System.out.println("- Free suite upgrades (subject to availability upon check-in).");
                System.out.println("- Unlimited executive lounge entry & complimentary butler service.");
                System.out.println("- 2x points on all expenses throughout our luxury resort chain.");
                break;
        }
        System.out.println("==========================================================================");
        InputUtil.pressEnterToContinue();
    }
}
