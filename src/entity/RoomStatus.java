package entity;

/**
 * Combined room-state enum.
 * AVAILABLE / OCCUPIED / UNAVAILABLE describe occupancy (who is in the room
 * and whether it can be booked):
 *   - AVAILABLE   : vacant and ready to be booked (housekeeping = READY)
 *   - OCCUPIED    : a guest is currently staying in the room
 *   - UNAVAILABLE : vacant but not bookable yet (checked out, still going
 *                   through the housekeeping pipeline)
 * DIRTY / CLEANING_IN_PROGRESS / INSPECTED / READY describe the housekeeping
 * (cleaning) pipeline. The two groups are tracked independently on Room
 * (occupancyStatus vs roomStatus) but now share a single enum type/file.
 * Room.setRoomStatus(...) keeps occupancyStatus in sync with the
 * housekeeping pipeline automatically (see Room.java).
 */
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
