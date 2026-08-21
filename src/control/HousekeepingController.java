package control;

import adt.LinkedStack;
import adt.ListInterface;
import entity.HousekeepingLog;
import entity.Room;
import entity.HousekeepingStatus;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HousekeepingController {

    private final LinkedStack<HousekeepingLog> housekeepingStack;
    private final ListInterface<Room> rooms;

    public HousekeepingController(LinkedStack<HousekeepingLog> housekeepingStack, ListInterface<Room> rooms) {
        this.housekeepingStack = housekeepingStack;
        this.rooms = rooms;
    }

    public HousekeepingLog updateRoomStatus(int roomNumber, HousekeepingStatus nextStatus, String supervisor) {
        Room room = findRoom(roomNumber);
        if (room == null || nextStatus == null) {
            return null;
        }

        HousekeepingStatus oldStatus = room.getHousekeepingStatus();
        room.setHousekeepingStatus(nextStatus);

        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        HousekeepingLog log = new HousekeepingLog(roomNumber, oldStatus, nextStatus, supervisor, timestamp);
        housekeepingStack.push(log);

        return log;
    }

    public HousekeepingLog rollbackLastAction() {
        if (housekeepingStack.isEmpty()) {
            return null;
        }

        HousekeepingLog log = housekeepingStack.pop();
        Room room = findRoom(log.getRoomNumber());
        if (room != null) {
            room.setHousekeepingStatus(log.getOldStatus());
            return log;
        }
        return null;
    }

    public ListInterface<HousekeepingLog> rollbackMultipleActions(int count) {
        ListInterface<HousekeepingLog> poppedLogs = housekeepingStack.popMany(count);
        for (int i = 0; i < poppedLogs.size(); i++) {
            HousekeepingLog log = poppedLogs.get(i);
            Room room = findRoom(log.getRoomNumber());
            if (room != null) {
                room.setHousekeepingStatus(log.getOldStatus());
            }
        }
        return poppedLogs;
    }

    private Room findRoom(int roomNumber) {
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }
}
