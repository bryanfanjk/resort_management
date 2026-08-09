package tarumtresort.entity;

/**
 * Entity class representing a logged housekeeping task/status change.
 * Used by the Housekeeping log history to support status rollback.
 * 
 * @author Admin
 */
public class HousekeepingLog {

    private int roomNumber;
    private Room.HousekeepingStatus oldStatus;
    private Room.HousekeepingStatus newStatus;
    private String supervisorName;
    private String timestamp;

    public HousekeepingLog(int roomNumber, Room.HousekeepingStatus oldStatus, Room.HousekeepingStatus newStatus, String supervisorName, String timestamp) {
        this.roomNumber = roomNumber;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.supervisorName = supervisorName;
        this.timestamp = timestamp;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Room.HousekeepingStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(Room.HousekeepingStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public Room.HousekeepingStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Room.HousekeepingStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] Supervisor %s updated Room %d from %s to %s",
                timestamp, supervisorName, roomNumber, oldStatus.getLabel(), newStatus.getLabel());
    }
}
