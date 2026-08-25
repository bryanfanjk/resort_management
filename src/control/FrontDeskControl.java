/*
 * Control class implementing business logic for Front-Desk operations.
 * Manages guest retrieval via Non-Linear ADT (BinarySearchTree), room availability, and billing calculations.
 */
package control;

import adt.BinarySearchTree;
import dao.ApprovedReservationData;
import dao.CheckedOutReservationData;
import dao.RoomData;
import dao.StandardWaitingCustomerData;
import dao.VipWaitingCustomerData;
import entity.CustomerType;
import entity.GuestBillingInfo;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import entity.WaitingCustomer;

/* author: Loh Chun Yi */
public class FrontDeskControl {
/* author: Loh Chun Yi */
    private BinarySearchTree<String, GuestBillingInfo> guestBst;

    public FrontDeskControl() {
        this(RoomData.createRooms());
    }

    public FrontDeskControl(Room[] rooms) {
        this.guestBst = new BinarySearchTree<>();
        loadInitialDaoData(rooms != null ? rooms : RoomData.createRooms());
    }

    /**
     * Loads initial seed data from DAO files into BST with CONF0001 - CONF0012.
     */

    /* author: Loh Chun Yi */
    public void loadInitialDaoData(Room[] rooms) {
        int counter = 1;

        // 1. Approved Reservations (Active)
        Reservation[] approved = ApprovedReservationData.createNew(rooms);
        for (Reservation res : approved) {
            String code = String.format("CONF%04d", counter++);
            res.setConfirmationNumber(code);
            if (res.getCustomer() != null) {
                res.getCustomer().setConfirmationNumber(code);
            }
            double rate = getDailyRate(res.getRoom() != null ? res.getRoom().getRoomType() : null);
            registerGuestInfo(new GuestBillingInfo(code, res.getCustomer(), res.getRoom(), rate));
        }

        // 2. Checked-Out Reservations (Historical)
        Reservation[] checkedOut = CheckedOutReservationData.createNew(rooms);
        for (Reservation res : checkedOut) {
            String code = String.format("CONF%04d", counter++);
            res.setConfirmationNumber(code);
            if (res.getCustomer() != null) {
                res.getCustomer().setConfirmationNumber(code);
            }
            double rate = getDailyRate(res.getRoom() != null ? res.getRoom().getRoomType() : null);
            registerGuestInfo(new GuestBillingInfo(code, res.getCustomer(), res.getRoom(), rate));
        }

        // 3. VIP Waiting Customers
        WaitingCustomer[] vips = VipWaitingCustomerData.createNew();
        for (WaitingCustomer vip : vips) {
            String code = String.format("CONF%04d", counter++);
            vip.setConfirmationNumber(code);
            double rate = getDailyRate(vip.getRequestedRoomType());
            registerGuestInfo(new GuestBillingInfo(code, vip, null, rate));
        }

        // 4. Standard Waiting Customers
        WaitingCustomer[] stds = StandardWaitingCustomerData.createNew();
        for (WaitingCustomer std : stds) {
            String code = String.format("CONF%04d", counter++);
            std.setConfirmationNumber(code);
            double rate = getDailyRate(std.getRequestedRoomType());
            registerGuestInfo(new GuestBillingInfo(code, std, null, rate));
        }
    }

    /* author: Loh Chun Yi */
    public static double getDailyRate(RoomType roomType) {
        if (roomType == null) {
            return 150.0;
        }
        switch (roomType) {
            case DELUXE:
                return 150.0;
            case PREMIUM:
                return 250.0;
            case PLATINUM:
                return 400.0;
            default:
                return 150.0;
        }
    }

    /**
     * Registers or updates guest billing info into the Non-Linear BST ADT indexed by 8-digit confirmation number.
     */
    /* author: Loh Chun Yi */
    public void registerGuestInfo(GuestBillingInfo guestInfo) {
        if (guestInfo != null && guestInfo.getConfirmationNumber() != null) {
            guestBst.insert(guestInfo.getConfirmationNumber().trim().toUpperCase(), guestInfo);
        }
    }

    /**
     * Updates room assignment for an existing guest in BST.
     */
    /* author: Loh Chun Yi */
    public void updateGuestRoomAssignment(String confirmationNumber, Room room) {
        if (confirmationNumber == null) return;
        GuestBillingInfo info = searchGuestByConfirmation(confirmationNumber);
        if (info != null) {
            info.setRoom(room);
            if (room != null && room.getRoomType() != null) {
                info.setDailyRoomRate(getDailyRate(room.getRoomType()));
            }
            registerGuestInfo(info);
        }
    }

    /**
     * Updates checkout date for an existing guest in BST.
     */
    /* author: Loh Chun Yi */
    public void updateGuestCheckout(String confirmationNumber, String checkOutDate) {
        if (confirmationNumber == null) return;
        GuestBillingInfo info = searchGuestByConfirmation(confirmationNumber);
        if (info != null && info.getCustomer() != null) {
            info.getCustomer().setCheckOutDate(checkOutDate);
            info.recalculateBill();
            registerGuestInfo(info);
        }
    }

    /**
     * Instantly retrieves guest information using 8-digit confirmation number from Non-Linear BST ADT.
     */
    /* author: Loh Chun Yi */
    public GuestBillingInfo searchGuestByConfirmation(String confirmationNumber) {
        if (confirmationNumber == null || confirmationNumber.trim().isEmpty()) {
            return null;
        }
        return guestBst.search(confirmationNumber.trim().toUpperCase());
    }

    /**
     * Evaluates room availability across all rooms.
     */
    /* author: Loh Chun Yi */
    public String getRoomAvailabilitySummary(Room[] rooms) {
        if (rooms == null || rooms.length == 0) {
            return "No room data available.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-12s %-15s%n", "Room Number", "Capacity", "Status"));
        sb.append("------------------------------------------\n");

        int availableCount = 0;
        for (Room r : rooms) {
            if (r != null) {
                String status = r.getOccupancyStatus().getLabel().toUpperCase();
                if (r.isAvailable()) availableCount++;
                sb.append(String.format("%-12d %-12d %-15s%n", r.getRoomNumber(), r.getCapacity(), status));
            }
        }
        sb.append("------------------------------------------\n");
        sb.append("Total Rooms Available: ").append(availableCount).append(" / ").append(rooms.length);

        return sb.toString();
    }

    /**
     * Returns total record count in BST index.
     */
    /* author: Loh Chun Yi */
    public int getGuestCount() {
        return guestBst.size();
    }

    /**
     * Retrieves all guests stored in the BST.
     */
    /* author: Loh Chun Yi */
    public GuestBillingInfo[] getAllGuests() {
        Object[] raw = guestBst.getAllValues();
        GuestBillingInfo[] result = new GuestBillingInfo[raw.length];
        for (int i = 0; i < raw.length; i++) {
            result[i] = (GuestBillingInfo) raw[i];
        }
        return result;
    }

    /**
     * Retrieves guests filtered by check-in date (DD/MM/YYYY).
     */
    /* author: Loh Chun Yi */
    public GuestBillingInfo[] getFilteredGuestsByCheckIn(String checkInDate) {
        if (checkInDate == null) {
            return new GuestBillingInfo[0];
        }
        GuestBillingInfo[] all = getAllGuests();
        int count = 0;
        for (GuestBillingInfo g : all) {
            if (g != null && g.getCustomer() != null && g.getCustomer().getCheckInDate() != null && checkInDate.equalsIgnoreCase(g.getCustomer().getCheckInDate())) {
                count++;
            }
        }

        GuestBillingInfo[] filtered = new GuestBillingInfo[count];
        int index = 0;
        for (GuestBillingInfo g : all) {
            if (g != null && g.getCustomer() != null && g.getCustomer().getCheckInDate() != null && checkInDate.equalsIgnoreCase(g.getCustomer().getCheckInDate())) {
                filtered[index++] = g;
            }
        }
        return filtered;
    }

    /**
     * Retrieves guests filtered by VIP customer type.
     */
    /* author: Loh Chun Yi */
    public GuestBillingInfo[] getFilteredVipGuests() {
        GuestBillingInfo[] all = getAllGuests();
        int count = 0;
        for (GuestBillingInfo g : all) {
            if (g != null && g.getCustomer() != null && g.getCustomer().getCustomerType() == CustomerType.VIP) {
                count++;
            }
        }

        GuestBillingInfo[] filtered = new GuestBillingInfo[count];
        int index = 0;
        for (GuestBillingInfo g : all) {
            if (g != null && g.getCustomer() != null && g.getCustomer().getCustomerType() == CustomerType.VIP) {
                filtered[index++] = g;
            }
        }
        return filtered;
    }

    /**
     * Generates a comprehensive Financial Report with Daily Revenue calculations.
     */
    /* author: Loh Chun Yi */
    public String generateFinancialReport(Room[] rooms) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================================================\n");
        sb.append("                 FINANCIAL REPORT - DAILY REVENUE & BILLING             \n");
        sb.append("========================================================================\n");

        // 1. Daily Room Revenue Breakdown by Category
        int deluxeOccupied = 0, deluxeTotal = 0;
        int premiumOccupied = 0, premiumTotal = 0;
        int platinumOccupied = 0, platinumTotal = 0;

        if (rooms != null) {
            for (Room r : rooms) {
                if (r != null && r.getRoomType() != null) {
                    boolean isOcc = r.isOccupied();
                    switch (r.getRoomType()) {
                        case DELUXE:
                            deluxeTotal++;
                            if (isOcc) deluxeOccupied++;
                            break;
                        case PREMIUM:
                            premiumTotal++;
                            if (isOcc) premiumOccupied++;
                            break;
                        case PLATINUM:
                            platinumTotal++;
                            if (isOcc) platinumOccupied++;
                            break;
                    }
                }
            }
        }

        double deluxeDailyRate = getDailyRate(RoomType.DELUXE);
        double premiumDailyRate = getDailyRate(RoomType.PREMIUM);
        double platinumDailyRate = getDailyRate(RoomType.PLATINUM);

        double deluxeDailyRev = deluxeOccupied * deluxeDailyRate;
        double premiumDailyRev = premiumOccupied * premiumDailyRate;
        double platinumDailyRev = platinumOccupied * platinumDailyRate;
        double totalDailyRevenue = deluxeDailyRev + premiumDailyRev + platinumDailyRev;

        sb.append("[ 1. DAILY ROOM REVENUE BREAKDOWN ]\n");
        sb.append(String.format("%-15s %-12s %-15s %-15s %-15s%n",
                "Room Type", "Daily Rate", "Occupied/Total", "Occupancy %", "Daily Revenue"));
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("%-15s $%-11.2f %-15s %-14.1f%% $%-14.2f%n",
                "Deluxe", deluxeDailyRate, deluxeOccupied + "/" + deluxeTotal,
                deluxeTotal > 0 ? ((double) deluxeOccupied / deluxeTotal) * 100 : 0.0, deluxeDailyRev));
        sb.append(String.format("%-15s $%-11.2f %-15s %-14.1f%% $%-14.2f%n",
                "Premium", premiumDailyRate, premiumOccupied + "/" + premiumTotal,
                premiumTotal > 0 ? ((double) premiumOccupied / premiumTotal) * 100 : 0.0, premiumDailyRev));
        sb.append(String.format("%-15s $%-11.2f %-15s %-14.1f%% $%-14.2f%n",
                "Platinum", platinumDailyRate, platinumOccupied + "/" + platinumTotal,
                platinumTotal > 0 ? ((double) platinumOccupied / platinumTotal) * 100 : 0.0, platinumDailyRev));
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("%-45s Total Daily Revenue: $%.2f%n%n", "", totalDailyRevenue));

        // 2. Revenue Distribution by Customer Type
        GuestBillingInfo[] allGuests = getAllGuests();
        int vipCount = 0, standardCount = 0;
        double vipTotalBilled = 0.0, standardTotalBilled = 0.0;

        for (GuestBillingInfo g : allGuests) {
            if (g != null && g.getCustomer() != null) {
                if (g.getCustomer().getCustomerType() == CustomerType.VIP) {
                    vipCount++;
                    vipTotalBilled += g.getTotalBillAmount();
                } else {
                    standardCount++;
                    standardTotalBilled += g.getTotalBillAmount();
                }
            }
        }

        double totalCumulativeBilled = vipTotalBilled + standardTotalBilled;

        sb.append("[ 2. CUSTOMER TYPE REVENUE DISTRIBUTION ]\n");
        sb.append(String.format("%-18s %-15s %-20s %-15s%n",
                "Customer Tier", "Total Guests", "Total Billed Revenue", "Share %"));
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("%-18s %-15d $%-19.2f %-14.1f%%%n",
                "VIP Guests", vipCount, vipTotalBilled,
                totalCumulativeBilled > 0 ? (vipTotalBilled / totalCumulativeBilled) * 100 : 0.0));
        sb.append(String.format("%-18s %-15d $%-19.2f %-14.1f%%%n",
                "Standard Guests", standardCount, standardTotalBilled,
                totalCumulativeBilled > 0 ? (standardTotalBilled / totalCumulativeBilled) * 100 : 0.0));
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("%-34s Total Billed Revenue: $%.2f%n%n", "", totalCumulativeBilled));

        // 3. Summary Performance Metrics
        int totalOccupiedRooms = deluxeOccupied + premiumOccupied + platinumOccupied;
        int totalRooms = deluxeTotal + premiumTotal + platinumTotal;
        double overallOccupancy = totalRooms > 0 ? ((double) totalOccupiedRooms / totalRooms) * 100 : 0.0;
        double avgRevPerGuest = allGuests.length > 0 ? totalCumulativeBilled / allGuests.length : 0.0;

        sb.append("[ 3. RESORT FINANCIAL PERFORMANCE SUMMARY ]\n");
        sb.append(String.format("• Current Active Daily Room Revenue : $%.2f%n", totalDailyRevenue));
        sb.append(String.format("• Total Cumulative Resort Billings : $%.2f%n", totalCumulativeBilled));
        sb.append(String.format("• Overall Resort Room Occupancy    : %d / %d (%.1f%%)%n", totalOccupiedRooms, totalRooms, overallOccupancy));
        sb.append(String.format("• Total Registered Guests in System: %d guests%n", allGuests.length));
        sb.append(String.format("• Average Billed Revenue Per Guest : $%.2f%n", avgRevPerGuest));
        sb.append("========================================================================\n");

        return sb.toString();
    }
}