package entity;

public enum RoomStatus {
    AVAILABLE("Available"),
    OCCUPIED("Occupied");

    private final String displayName;

    RoomStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
