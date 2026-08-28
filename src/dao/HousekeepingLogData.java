package dao;

import adt.LinkedStack;
import adt.ListInterface;
import entity.HousekeepingLog;
import entity.Room;
import entity.RoomStatus;

/* author: Ho Jia Ming */
public final class HousekeepingLogData {

    private HousekeepingLogData() {
    }

    public static void seedDemoData(ListInterface<Room> rooms, LinkedStack<HousekeepingLog> stack) {
        // Room 101: just checked out, nobody has started cleaning yet.
        // (No log entry - checkout itself is never logged, same as real checkouts.)
        setStatus(rooms, 101, RoomStatus.DIRTY);

        // Room 102: staff cleaned it, supervisor inspected and approved it.
        logChange(stack, rooms, 102, RoomStatus.DIRTY, RoomStatus.CLEANING_IN_PROGRESS,
                "staff1", "08:15:02");
        logChange(stack, rooms, 102, RoomStatus.CLEANING_IN_PROGRESS, RoomStatus.INSPECTED,
                "supervisor1", "08:42:10");

        // Room 104: went all the way through the pipeline - ready for the next guest.
        logChange(stack, rooms, 104, RoomStatus.DIRTY, RoomStatus.CLEANING_IN_PROGRESS,
                "staff2", "09:05:20");
        logChange(stack, rooms, 104, RoomStatus.CLEANING_IN_PROGRESS, RoomStatus.INSPECTED,
                "supervisor1", "09:30:47");
        logChange(stack, rooms, 104, RoomStatus.INSPECTED, RoomStatus.READY,
                "supervisor1", "09:31:55");

        // Room 106: staff is currently mid-clean.
        logChange(stack, rooms, 106, RoomStatus.DIRTY, RoomStatus.CLEANING_IN_PROGRESS,
                "staff3", "10:02:18");
    }

    private static void setStatus(ListInterface<Room> rooms, int roomNumber, RoomStatus status) {
        Room room = findRoom(rooms, roomNumber);
        if (room != null) {
            room.setRoomStatus(status);
        }
    }

    private static void logChange(LinkedStack<HousekeepingLog> stack, ListInterface<Room> rooms,
            int roomNumber, RoomStatus oldStatus, RoomStatus newStatus, String staffName, String timestamp) {
        Room room = findRoom(rooms, roomNumber);
        if (room != null) {
            room.setRoomStatus(newStatus);
        }
        stack.push(new HousekeepingLog(roomNumber, oldStatus, newStatus, staffName, timestamp));
    }

    private static Room findRoom(ListInterface<Room> rooms, int roomNumber) {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }
}
