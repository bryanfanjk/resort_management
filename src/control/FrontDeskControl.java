/*
 * Control class implementing business logic for Front-Desk operations.
 * Manages guest retrieval via Non-Linear ADT (BinarySearchTree), room availability, and billing calculations.
 */
package tarumtresort.control;

import tarumtresort.adt.BinarySearchTree;
import tarumtresort.entity.GuestBillingInfo;
import tarumtresort.entity.Room;

public class FrontDeskControl {

    private BinarySearchTree<String, GuestBillingInfo> guestBst;

    public FrontDeskControl() {
        this.guestBst = new BinarySearchTree<>();
    }

    /**
     * Registers guest billing info into the Non-Linear BST ADT indexed by 8-digit confirmation number.
     */
    public void registerGuestInfo(GuestBillingInfo guestInfo) {
        if (guestInfo != null && guestInfo.getConfirmationNumber() != null) {
            guestBst.insert(guestInfo.getConfirmationNumber(), guestInfo);
        }
    }

    /**
     * Instantly retrieves guest information using 8-digit confirmation number from Non-Linear BST ADT.
     */
    public GuestBillingInfo searchGuestByConfirmation(String confirmationNumber) {
        if (confirmationNumber == null || confirmationNumber.trim().isEmpty()) {
            return null;
        }
        return guestBst.search(confirmationNumber.trim());
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
        GuestBillingInfo[] all = getAllGuests();
        int count = 0;
        for (GuestBillingInfo g : all) {
            if (g != null && g.getCustomer() != null && checkInDate.equalsIgnoreCase(g.getCustomer().getCheckInDate())) {
                count++;
            }
        }

        GuestBillingInfo[] filtered = new GuestBillingInfo[count];
        int index = 0;
        for (GuestBillingInfo g : all) {
            if (g != null && g.getCustomer() != null && checkInDate.equalsIgnoreCase(g.getCustomer().getCheckInDate())) {
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
