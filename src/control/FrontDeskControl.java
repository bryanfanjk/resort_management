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
import entity.GuestBillingInfo;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import entity.WaitingCustomer;

public class FrontDeskControl {

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
    public void registerGuestInfo(GuestBillingInfo guestInfo) {
        if (guestInfo != null && guestInfo.getConfirmationNumber() != null) {
            guestBst.insert(guestInfo.getConfirmationNumber().trim().toUpperCase(), guestInfo);
        }
    }

    /**
     * Updates room assignment for an existing guest in BST.
     */
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
    public GuestBillingInfo searchGuestByConfirmation(String confirmationNumber) {
        if (confirmationNumber == null || confirmationNumber.trim().isEmpty()) {
            return null;
        }
        return guestBst.search(confirmationNumber.trim().toUpperCase());
    }

    /**
     * Evaluates room availability across all rooms.
     */
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
                String status = r.isAvailable() ? "AVAILABLE" : "OCCUPIED";
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
    public int getGuestCount() {
        return guestBst.size();
    }

    /**
     * Retrieves all guests stored in the BST.
     */
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
     * Generates Room Availability Summary Report showing room breakdown and statistics.
     */
    public String generateRoomAvailabilitySummaryReport(Room[] rooms) {
        if (rooms == null || rooms.length == 0) {
            return "No room data available.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================================\n");
        sb.append("             ROOM AVAILABILITY SUMMARY REPORT           \n");
        sb.append("========================================================\n");
        sb.append(String.format("%-12s %-12s %-15s%n", "Room Number", "Capacity", "Status"));
        sb.append("--------------------------------------------------------\n");

        int availableCount = 0;
        int occupiedCount = 0;
        for (Room r : rooms) {
            if (r != null) {
                boolean isAvail = r.isAvailable();
                String status = isAvail ? "AVAILABLE" : "OCCUPIED";
                if (isAvail) availableCount++;
                else occupiedCount++;
                sb.append(String.format("%-12d %-12d %-15s%n", r.getRoomNumber(), r.getCapacity(), status));
            }
        }

        int totalRooms = rooms.length;
        double occupancyRate = totalRooms > 0 ? ((double) occupiedCount / totalRooms) * 100 : 0.0;

        sb.append("--------------------------------------------------------\n");
        sb.append(String.format("Total Rooms      : %d%n", totalRooms));
        sb.append(String.format("Available Rooms  : %d%n", availableCount));
        sb.append(String.format("Occupied Rooms   : %d%n", occupiedCount));
        sb.append(String.format("Occupancy Rate   : %.1f%%%n", occupancyRate));
        sb.append("========================================================\n");

        return sb.toString();
    }
}
