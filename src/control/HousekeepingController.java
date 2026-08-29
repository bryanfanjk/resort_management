package control;

import adt.LinkedStack;
import adt.ListInterface;
import entity.HousekeepingLog;
import entity.RoomStatus;
import entity.Room;
import java.text.SimpleDateFormat;
import java.util.Date;

/* author: Ho Jia Ming */
public class HousekeepingController {

    private final LinkedStack<HousekeepingLog> housekeepingStack;
    private final ListInterface<Room> rooms;
    private final AuthController authController;

    public HousekeepingController(LinkedStack<HousekeepingLog> housekeepingStack,
            ListInterface<Room> rooms,
            AuthController authController) {
        this.housekeepingStack = housekeepingStack;
        this.rooms = rooms;
        this.authController = authController;
    }

    public HousekeepingLog updateRoomStatus(int roomNumber, RoomStatus nextStatus,
            String staffName) {
        // Check if user is logged in
        if (!authController.isLoggedIn()) {
            System.out.println("ERROR: Please login first.");
            return null;
        }

        Room room = findRoom(roomNumber);
        if (room == null || nextStatus == null) {
            return null;
        }

        RoomStatus oldStatus = room.getRoomStatus();

        // Check status transition rules
        if (!oldStatus.canTransitionTo(nextStatus)) {
            System.out.printf("ERROR: Cannot change from %s directly to %s. "
                    + "Must follow sequence: Dirty -> Cleaning -> Inspected -> Ready\n",
                    oldStatus.getLabel(), nextStatus.getLabel());
            return null;
        }

        // Role-based permissions
        if (authController.isHousekeepingStaff()) {
            // Staff can only set Dirty or Cleaning In Progress
            if (!nextStatus.canBeSetByStaff()) {
                System.out.printf("ERROR: Housekeeping staff can only set 'Dirty' or "
                        + "'Cleaning In Progress' status. '%s' requires supervisor approval.\n",
                        nextStatus.getLabel());
                return null;
            }
        } else if (authController.isSupervisor()) {
            // Supervisor can set Inspected and Ready
            // Supervisor can also override and set any status (for flexibility)
        }

        // Update room status
        return recordStatusChange(room, oldStatus, nextStatus, staffName);
    }

    // For supervisor to approve/reject cleaning
    public HousekeepingLog approveCleaning(int roomNumber, RoomStatus newStatus,
            String staffName) {
        if (!authController.isSupervisor()) {
            System.out.println("ERROR: Only supervisors can approve or reject cleaning.");
            return null;
        }

        Room room = findRoom(roomNumber);
        if (room == null) {
            System.out.println("ERROR: Room number not found.");
            return null;
        }

        RoomStatus currentStatus = room.getRoomStatus();

        // Supervisor can only change from CLEANING_IN_PROGRESS to INSPECTED or reject back to DIRTY
        if (currentStatus != RoomStatus.CLEANING_IN_PROGRESS) {
            System.out.println("ERROR: Room must be in 'Cleaning In Progress' status for supervisor action.");
            return null;
        }

        if (newStatus != RoomStatus.INSPECTED && newStatus != RoomStatus.DIRTY) {
            System.out.println("ERROR: Supervisor can only set status to 'Inspected' (approve) or 'Dirty' (reject).");
            return null;
        }

        // Approve (-> Inspected) follows the normal forward sequence, but reject
        // (-> Dirty) is a deliberate backward step that updateRoomStatus's
        // forward-only sequence check would otherwise block. Both transitions are
        // already fully validated above, so record the change directly instead of
        // routing through updateRoomStatus.
        return recordStatusChange(room, currentStatus, newStatus, staffName);
    }

    private HousekeepingLog recordStatusChange(Room room, RoomStatus oldStatus,
            RoomStatus newStatus, String staffName) {
        room.setRoomStatus(newStatus);

        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        HousekeepingLog log = new HousekeepingLog(room.getRoomNumber(), oldStatus, newStatus,
                staffName, timestamp);
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
            room.setRoomStatus(log.getOldStatus());
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
                room.setRoomStatus(log.getOldStatus());
            }
        }
        return poppedLogs;
    }

    public void purgeLogsForRoom(int roomNumber) {
        ListInterface<HousekeepingLog> keep = new adt.List<>(housekeepingStack.getSize());
        while (!housekeepingStack.isEmpty()) {
            HousekeepingLog log = housekeepingStack.pop();
            if (log.getRoomNumber() != roomNumber) {
                keep.add(log);
            }
        }

        for (int i = keep.size() - 1; i >= 0; i--) {
            housekeepingStack.push(keep.get(i));
        }
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
