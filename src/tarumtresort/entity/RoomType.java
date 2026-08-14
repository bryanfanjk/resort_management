package tarumtresort.entity;

/** The accommodation categories offered by the resort. */
public enum RoomType {
    DELUXE("Deluxe"),
    PREMIUM("Premium"),
    PLATINUM("Platinum");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
