package tarumtresort.entity;

/**
 * Entity class representing a hotel room.
 * 
 * @author Admin
 */
public class Room {

    public enum RoomType {
        STANDARD("Standard", 150.00),
        DELUXE("Deluxe", 280.00),
        SUITE("Suite", 500.00),
        PENTHOUSE("Penthouse", 1200.00);

        private final String label;
        private final double rate;

        RoomType(String label, double rate) {
            this.label = label;
            this.rate = rate;
        }

        public String getLabel() {
            return label;
        }

        public double getRate() {
            return rate;
        }
    }

    public enum HousekeepingStatus {
        DIRTY("Dirty"),
        CLEANING_IN_PROGRESS("Cleaning In Progress"),
        INSPECTED("Inspected"),
        READY("Ready for Check-In");

        private final String label;

        HousekeepingStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private int roomNumber;
    private RoomType roomType;
    private HousekeepingStatus status;
    private String currentGuestConfirmation; // null if vacant

    public Room(int roomNumber, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = HousekeepingStatus.READY;
        this.currentGuestConfirmation = null;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public HousekeepingStatus getStatus() {
        return status;
    }

    public void setStatus(HousekeepingStatus status) {
        this.status = status;
    }

    public String getCurrentGuestConfirmation() {
        return currentGuestConfirmation;
    }

    public void setCurrentGuestConfirmation(String currentGuestConfirmation) {
        this.currentGuestConfirmation = currentGuestConfirmation;
    }

    public boolean isVacant() {
        return currentGuestConfirmation == null;
    }

    @Override
    public String toString() {
        String vacancy = isVacant() ? "Vacant" : "Occupied (" + currentGuestConfirmation + ")";
        return String.format("Room %d | %s | Status: %s | %s", roomNumber, roomType.getLabel(), status.getLabel(), vacancy);
    }
}
