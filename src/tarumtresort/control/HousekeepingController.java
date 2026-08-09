package tarumtresort.control;

import tarumtresort.adt.LinkedStack;
import tarumtresort.adt.ListInterface;
import tarumtresort.entity.HousekeepingLog;
import tarumtresort.entity.Room;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Control class managing room cleaning transitions and history undo operations.
 * Utilizes a LIFO Stack for rollback capability.
 * 
 * @author Admin
 */
public class HousekeepingController {

    private final LinkedStack<HousekeepingLog> housekeepingStack;
    private final ListInterface<Room> rooms;

    public HousekeepingController(LinkedStack<HousekeepingLog> housekeepingStack, ListInterface<Room> rooms) {
        this.housekeepingStack = housekeepingStack;
        this.rooms = rooms;
    }

    /**
     * Updates the housekeeping status of a room and logs the transaction.
     * 
     * @param roomNumber The room to update.
     * @param nextStatus The new HousekeepingStatus.
     * @param supervisor The supervisor name logging the task.
     * @return The created log entry, or null if room is not found or invalid transition.
     */
    public HousekeepingLog updateRoomStatus(int roomNumber, Room.HousekeepingStatus nextStatus, String supervisor) {
        Room room = findRoom(roomNumber);
        if (room == null || nextStatus == null) {
            return null;
        }

        Room.HousekeepingStatus oldStatus = room.getStatus();
        room.setStatus(nextStatus);

        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        HousekeepingLog log = new HousekeepingLog(roomNumber, oldStatus, nextStatus, supervisor, timestamp);
        housekeepingStack.push(log);

        return log;
    }

    /**
     * Rolls back the last single housekeeping status update from the log.
     * 
     * @return The undone HousekeepingLog, or null if the log stack is empty.
     */
    public HousekeepingLog rollbackLastAction() {
        if (housekeepingStack.isEmpty()) {
            return null;
        }

        HousekeepingLog log = housekeepingStack.pop();
        Room room = findRoom(log.getRoomNumber());
        if (room != null) {
            room.setStatus(log.getOldStatus());
            return log;
        }
        return null;
    }

    /**
     * Non-trivial original control operation: Performs a batch rollback of multiple actions.
     * Invokes the custom `popMany` ADT method.
     * 
     * @param count Number of updates to roll back.
     * @return List of rolled back logs.
     */
    public ListInterface<HousekeepingLog> rollbackMultipleActions(int count) {
        ListInterface<HousekeepingLog> poppedLogs = housekeepingStack.popMany(count);
        for (int i = 1; i <= poppedLogs.getLength(); i++) {
            HousekeepingLog log = poppedLogs.getEntry(i);
            Room room = findRoom(log.getRoomNumber());
            if (room != null) {
                room.setStatus(log.getOldStatus());
            }
        }
        return poppedLogs;
    }

    private Room findRoom(int roomNumber) {
        for (int i = 1; i <= rooms.getLength(); i++) {
            Room r = rooms.getEntry(i);
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }
}
