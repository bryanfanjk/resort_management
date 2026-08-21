package control;

import adt.LinkedStack;
import adt.ListInterface;
import entity.HousekeepingLog;
import entity.HousekeepingStatus;
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

    public HousekeepingLog updateRoomStatus(int roomNumber, HousekeepingStatus nextStatus,
            String supervisor) {
        // Check if user is logged in
        if (!authController.isLoggedIn()) {
            System.out.println("ERROR: Please login first.");
            return null;
        }

        Room room = findRoom(roomNumber);
        if (room == null || nextStatus == null) {
            return null;
        }

        HousekeepingStatus oldStatus = room.getHousekeepingStatus();

        // Check status transition rules
        if (!oldStatus.canTransitionTo(nextStatus)) {
            System.out.printf("ERROR: Cannot change from %s directly to %s. "
                    + "Must follow sequence: Dirty → Cleaning → Inspected → Ready\n",
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
        room.setHousekeepingStatus(nextStatus);

        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        HousekeepingLog log = new HousekeepingLog(roomNumber, oldStatus, nextStatus,
                supervisor, timestamp);
        housekeepingStack.push(log);

        return log;
    }

    // For supervisor to approve/reject cleaning
    public HousekeepingLog approveCleaning(int roomNumber, HousekeepingStatus newStatus,
            String supervisor) {
        if (!authController.isSupervisor()) {
            System.out.println("ERROR: Only supervisors can approve or reject cleaning.");
            return null;
        }

        Room room = findRoom(roomNumber);
        if (room == null) {
            return null;
        }

        HousekeepingStatus currentStatus = room.getHousekeepingStatus();

        // Supervisor can only change from CLEANING_IN_PROGRESS to INSPECTED or reject back to DIRTY
        if (currentStatus != HousekeepingStatus.CLEANING_IN_PROGRESS) {
            System.out.println("ERROR: Room must be in 'Cleaning In Progress' status for supervisor action.");
            return null;
        }

        if (newStatus != HousekeepingStatus.INSPECTED && newStatus != HousekeepingStatus.DIRTY) {
            System.out.println("ERROR: Supervisor can only set status to 'Inspected' (approve) or 'Dirty' (reject).");
            return null;
        }

        return updateRoomStatus(roomNumber, newStatus, supervisor);
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
