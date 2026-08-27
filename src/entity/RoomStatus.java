package entity;

/* author: Ho Jia Ming */
public enum RoomStatus {
    AVAILABLE("Available", 0),
    OCCUPIED("Occupied", 0),
    UNAVAILABLE("Unavailable", 0),
    DIRTY("Dirty", 1),
    CLEANING_IN_PROGRESS("Cleaning In Progress", 2),
    INSPECTED("Inspected", 3),
    READY("Ready for Check-In", 4);

    private final String label;
    private final int sequenceNumber;

    RoomStatus(String label, int sequenceNumber) {
        this.label = label;
        this.sequenceNumber = sequenceNumber;
    }

    public String getLabel() {
        return label;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public boolean canTransitionTo(RoomStatus newStatus) {
        // Allow only forward sequential transitions (housekeeping pipeline only)
        return newStatus.sequenceNumber == this.sequenceNumber + 1;
    }

    public boolean canBeSetByStaff() {
        return this == DIRTY || this == CLEANING_IN_PROGRESS;
    }
}
