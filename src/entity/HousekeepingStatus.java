package entity;

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
