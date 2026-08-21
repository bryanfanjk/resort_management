package entity;

public enum HousekeepingStatus {
    DIRTY("Dirty", 1),
    CLEANING_IN_PROGRESS("Cleaning In Progress", 2),
    INSPECTED("Inspected", 3),
    READY("Ready for Check-In", 4);

    private final String label;
    private final int sequenceNumber;

    HousekeepingStatus(String label, int sequenceNumber) {
        this.label = label;
        this.sequenceNumber = sequenceNumber;
    }

    public String getLabel() {
        return label;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public boolean canTransitionTo(HousekeepingStatus newStatus) {
        // Allow only forward sequential transitions
        return newStatus.sequenceNumber == this.sequenceNumber + 1;
    }

    public boolean canBeSetByStaff() {
        return this == DIRTY || this == CLEANING_IN_PROGRESS;
    }
}
