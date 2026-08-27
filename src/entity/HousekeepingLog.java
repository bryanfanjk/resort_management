package entity;

/* author: Ho Jia Ming */
public class HousekeepingLog {

    private int roomNumber;
    private RoomStatus oldStatus;
    private RoomStatus newStatus;
    private String staffName;
    private String timestamp;

    public HousekeepingLog(int roomNumber, RoomStatus oldStatus, RoomStatus newStatus, String staffName, String timestamp) {
        this.roomNumber = roomNumber;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.staffName = staffName;
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

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s updated Room %d from %s to %s",
                timestamp, staffName, roomNumber, oldStatus.getLabel(), newStatus.getLabel());
    }
}
