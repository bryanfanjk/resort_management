package entity;

/* author: Ho Jia Ming */
public class HousekeepingLog {

    private int roomNumber;
    private RoomStatus oldStatus;
    private RoomStatus newStatus;
    private String supervisorName;
    private String timestamp;

    public HousekeepingLog(int roomNumber, RoomStatus oldStatus, RoomStatus newStatus, String supervisorName, String timestamp) {
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

    public RoomStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(RoomStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public RoomStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(RoomStatus newStatus) {
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
