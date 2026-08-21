package control;

import adt.LinkedStack;
import adt.List;
import adt.ListInterface;
import entity.HousekeepingLog;
import entity.HousekeepingReport;
import entity.HousekeepingStatus;
import entity.Room;
import entity.RoomType;

/* author: Ho Jia Ming */
public class HousekeepingReportController {

    private final LinkedStack<HousekeepingLog> stack;
    private final ListInterface<Room> rooms;

    public HousekeepingReportController(LinkedStack<HousekeepingLog> stack,
            ListInterface<Room> rooms) {
        this.stack = stack;
        this.rooms = rooms;
    }

    // ============================================================
    // REPORT 1: ROOM CLEANING STATUS REPORT
    // ============================================================
    public HousekeepingReport generateRoomStatusReport(RoomType roomTypeFilter,
            HousekeepingStatus statusFilter) {
        ListInterface<Room> filteredRooms = new List<>(rooms.size());

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            boolean matchRoomType = (roomTypeFilter == null || room.getRoomType() == roomTypeFilter);
            boolean matchStatus = (statusFilter == null || room.getHousekeepingStatus() == statusFilter);

            if (matchRoomType && matchStatus) {
                filteredRooms.add(room);
            }
        }

        // Get logs from stack
        ListInterface<HousekeepingLog> logs = stack.toList();
        return new HousekeepingReport(logs, filteredRooms);
    }

    public ListInterface<Room> sortRoomsForReport(int sortChoice, ListInterface<Room> roomsToSort) {
        ListInterface<Room> sortedRooms = new List<>(roomsToSort.size());

        // Copy rooms
        for (int i = 0; i < roomsToSort.size(); i++) {
            sortedRooms.add(roomsToSort.get(i));
        }

        // Selection sort based on choice
        for (int i = 0; i < sortedRooms.size() - 1; i++) {
            int targetIdx = i;
            for (int j = i + 1; j < sortedRooms.size(); j++) {
                int comparison = 0;

                switch (sortChoice) {
                    case 1: // Room Number Ascending
                        comparison = sortedRooms.get(j).getRoomNumber() - sortedRooms.get(targetIdx).getRoomNumber();
                        break;
                    case 2: // Room Number Descending
                        comparison = sortedRooms.get(targetIdx).getRoomNumber() - sortedRooms.get(j).getRoomNumber();
                        break;
                    case 3: // Status Ascending
                        comparison = sortedRooms.get(j).getHousekeepingStatus().getSequenceNumber()
                                - sortedRooms.get(targetIdx).getHousekeepingStatus().getSequenceNumber();
                        break;
                    case 4: // Status Descending
                        comparison = sortedRooms.get(targetIdx).getHousekeepingStatus().getSequenceNumber()
                                - sortedRooms.get(j).getHousekeepingStatus().getSequenceNumber();
                        break;
                }

                if (comparison < 0) {
                    targetIdx = j;
                }
            }

            // Swap
            Room temp = sortedRooms.get(i);
            sortedRooms.set(i, sortedRooms.get(targetIdx));
            sortedRooms.set(targetIdx, temp);
        }

        return sortedRooms;
    }

    // ============================================================
    // REPORT 2: HOUSEKEEPING ACTION AUDIT REPORT
    // ============================================================
    // Filter logs by room number and staff name only
    public ListInterface<HousekeepingLog> filterAuditLogs(
            ListInterface<HousekeepingLog> allLogs, int roomFilter, String staffFilter) {

        ListInterface<HousekeepingLog> filteredLogs = new List<>(allLogs.size());

        for (int i = 0; i < allLogs.size(); i++) {
            HousekeepingLog log = allLogs.get(i);
            boolean matches = true;

            // Filter by room number (0 means all)
            if (roomFilter != 0 && log.getRoomNumber() != roomFilter) {
                matches = false;
            }

            // Filter by staff name (empty means all)
            if (!staffFilter.isEmpty() && !log.getSupervisorName().equalsIgnoreCase(staffFilter)) {
                matches = false;
            }

            if (matches) {
                filteredLogs.add(log);
            }
        }

        return filteredLogs;
    }

    // Sort logs for audit report
    public ListInterface<HousekeepingLog> sortAuditLogs(
            ListInterface<HousekeepingLog> logsToSort, int sortChoice) {

        ListInterface<HousekeepingLog> sortedLogs = new List<>(logsToSort.size());

        // Copy logs
        for (int i = 0; i < logsToSort.size(); i++) {
            sortedLogs.add(logsToSort.get(i));
        }

        // Selection sort based on choice
        for (int i = 0; i < sortedLogs.size() - 1; i++) {
            int targetIdx = i;
            for (int j = i + 1; j < sortedLogs.size(); j++) {
                int comparison = 0;

                switch (sortChoice) {
                    case 1: // Timestamp (Newest First)
                        comparison = sortedLogs.get(j).getTimestamp().compareTo(sortedLogs.get(targetIdx).getTimestamp());
                        break;
                    case 2: // Timestamp (Oldest First)
                        comparison = sortedLogs.get(targetIdx).getTimestamp().compareTo(sortedLogs.get(j).getTimestamp());
                        break;
                    case 3: // Room Number (Ascending)
                        comparison = sortedLogs.get(j).getRoomNumber() - sortedLogs.get(targetIdx).getRoomNumber();
                        break;
                }

                if (comparison < 0) {
                    targetIdx = j;
                }
            }

            // Swap
            HousekeepingLog temp = sortedLogs.get(i);
            sortedLogs.set(i, sortedLogs.get(targetIdx));
            sortedLogs.set(targetIdx, temp);
        }

        return sortedLogs;
    }
}
