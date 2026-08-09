package tarumtresort.control;

import tarumtresort.adt.ArrayList;
import tarumtresort.adt.BSTInterface;
import tarumtresort.adt.LinkedStack;
import tarumtresort.adt.ListInterface;
import tarumtresort.entity.Booking;
import tarumtresort.entity.Guest;
import tarumtresort.entity.HousekeepingLog;
import tarumtresort.entity.Room;
import tarumtresort.util.Sorter;
import java.util.Comparator;

/**
 * Control class generating structured analytical management reports.
 * Employs custom filters, search keys, and explicit custom sorting.
 * 
 * @author Admin
 */
public class ReportController {

    private final ListInterface<Room> rooms;
    private final ListInterface<Guest> guests;
    private final BSTInterface<String, Booking> bookingTree;
    private final LinkedStack<HousekeepingLog> housekeepingStack;

    public ReportController(ListInterface<Room> rooms, 
                            ListInterface<Guest> guests, 
                            BSTInterface<String, Booking> bookingTree,
                            LinkedStack<HousekeepingLog> housekeepingStack) {
        this.rooms = rooms;
        this.guests = guests;
        this.bookingTree = bookingTree;
        this.housekeepingStack = housekeepingStack;
    }

    /**
     * Report 1: Room Occupancy & Revenue Analysis.
     * Filters by room type and occupancy. Sorts by room number or price.
     * 
     * @param typeFilter Selected Room Type (or null for all).
     * @param onlyVacant If true, filters out occupied rooms.
     * @param sortByPrice If true, sorts by room rate descending; else sorts by room number ascending.
     * @return List of rooms matching the criteria, sorted.
     */
    public ListInterface<Room> generateRoomReport(Room.RoomType typeFilter, boolean onlyVacant, boolean sortByPrice) {
        ListInterface<Room> filteredRooms = new ArrayList<>();

        // Filter phase
        for (int i = 1; i <= rooms.getLength(); i++) {
            Room r = rooms.getEntry(i);
            boolean typeMatches = (typeFilter == null || r.getRoomType() == typeFilter);
            boolean vacancyMatches = (!onlyVacant || r.isVacant());
            
            if (typeMatches && vacancyMatches) {
                filteredRooms.add(r);
            }
        }

        // Sort phase
        if (sortByPrice) {
            // Sort by Room Rate descending
            Sorter.sort(filteredRooms, new Comparator<Room>() {
                @Override
                public int compare(Room r1, Room r2) {
                    return Double.compare(r2.getRoomType().getRate(), r1.getRoomType().getRate());
                }
            });
        } else {
            // Sort by Room Number ascending
            Sorter.sort(filteredRooms, new Comparator<Room>() {
                @Override
                public int compare(Room r1, Room r2) {
                    return Integer.compare(r1.getRoomNumber(), r2.getRoomNumber());
                }
            });
        }

        return filteredRooms;
    }

    /**
     * Report 2: High-Value Guest & Rewards Report.
     * Filters by minimum points threshold and tier. Sorts by loyalty points descending.
     * 
     * @param minPoints Minimum points required to display.
     * @param tierFilter Selected tier (or null for all tiers).
     * @return List of matching guests, sorted by points descending.
     */
    public ListInterface<Guest> generateLoyaltyReport(int minPoints, Guest.LoyaltyTier tierFilter) {
        ListInterface<Guest> filteredGuests = new ArrayList<>();

        // Filter phase
        for (int i = 1; i <= guests.getLength(); i++) {
            Guest g = guests.getEntry(i);
            boolean pointsMatch = g.getLoyaltyPoints() >= minPoints;
            boolean tierMatch = (tierFilter == null || g.getTier() == tierFilter);

            if (pointsMatch && tierMatch) {
                filteredGuests.add(g);
            }
        }

        // Sort phase: points descending
        Sorter.sort(filteredGuests, new Comparator<Guest>() {
            @Override
            public int compare(Guest g1, Guest g2) {
                return Integer.compare(g2.getLoyaltyPoints(), g1.getLoyaltyPoints());
            }
        });

        return filteredGuests;
    }

    /**
     * Report 3: Housekeeping Action Audit Report.
     * Filters by room number or supervisor. Sorts by room number ascending.
     * 
     * @param roomFilter Room number filter (0 for all).
     * @param supervisorFilter Supervisor name filter (null/empty for all).
     * @return List of matching logs.
     */
    public ListInterface<HousekeepingLog> generateHousekeepingReport(int roomFilter, String supervisorFilter) {
        ListInterface<HousekeepingLog> filteredLogs = new ArrayList<>();
        ListInterface<HousekeepingLog> allLogs = housekeepingStack.toList();

        // Filter phase
        for (int i = 1; i <= allLogs.getLength(); i++) {
            HousekeepingLog log = allLogs.getEntry(i);
            boolean roomMatch = (roomFilter == 0 || log.getRoomNumber() == roomFilter);
            boolean supervisorMatch = (supervisorFilter == null || supervisorFilter.trim().isEmpty() || 
                    log.getSupervisorName().equalsIgnoreCase(supervisorFilter.trim()));

            if (roomMatch && supervisorMatch) {
                filteredLogs.add(log);
            }
        }

        // Sort phase: Room Number ascending, secondary by timestamp descending
        Sorter.sort(filteredLogs, new Comparator<HousekeepingLog>() {
            @Override
            public int compare(HousekeepingLog l1, HousekeepingLog l2) {
                int cmp = Integer.compare(l1.getRoomNumber(), l2.getRoomNumber());
                if (cmp != 0) {
                    return cmp;
                }
                return l2.getTimestamp().compareTo(l1.getTimestamp());
            }
        });

        return filteredLogs;
    }
}
