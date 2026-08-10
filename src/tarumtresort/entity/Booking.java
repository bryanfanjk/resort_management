package tarumtresort.entity;

/**
 * Entity class representing a room booking/reservation.
 * Implements Comparable to enable VIP allocation priority.
 * Priority is determined first by Loyalty Tier rank (descending),
 * and secondarily by booking arrival sequence (chronological, ascending).
 * 
 * @author Admin
 */
public class Booking implements Comparable<Booking> {

    public enum BookingStatus {
        PENDING("Pending Room Assignment"),
        ALLOCATED("Room Allocated"),
        CHECKED_IN("Checked-In"),
        COMPLETED("Completed & Checked-Out");

        private final String label;

        BookingStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private String confirmationNumber; // 8-digit unique code
    private Guest guest;
    private Room.RoomType requestedRoomType;
    private Room allocatedRoom; // null if pending
    private BookingStatus status;
    private boolean isVIP;
    private long bookingIndex; // Chronological sequence index
    private String bookingDate; // e.g. "2026-07-04"
    private int nights = 1; // Duration of stay in nights

    public Booking(String confirmationNumber, Guest guest, Room.RoomType requestedRoomType, boolean isVIP, long bookingIndex, String bookingDate) {
        this.confirmationNumber = confirmationNumber;
        this.guest = guest;
        this.requestedRoomType = requestedRoomType;
        this.allocatedRoom = null;
        this.status = BookingStatus.PENDING;
        this.isVIP = isVIP;
        this.bookingIndex = bookingIndex;
        this.bookingDate = bookingDate;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room.RoomType getRequestedRoomType() {
        return requestedRoomType;
    }

    public void setRequestedRoomType(Room.RoomType requestedRoomType) {
        this.requestedRoomType = requestedRoomType;
    }

    public Room getAllocatedRoom() {
        return allocatedRoom;
    }

    public void setAllocatedRoom(Room allocatedRoom) {
        this.allocatedRoom = allocatedRoom;
        if (allocatedRoom != null) {
            this.status = BookingStatus.ALLOCATED;
        } else {
            this.status = BookingStatus.PENDING;
        }
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public long getBookingIndex() {
        return bookingIndex;
    }

    public void setBookingIndex(long bookingIndex) {
        this.bookingIndex = bookingIndex;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getNights() {
        return nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    @Override
    public int compareTo(Booking other) {
        // Primary key: Loyalty Tier rank (larger rank = higher priority)
        int tierComparison = this.guest.getTier().getRank() - other.guest.getTier().getRank();
        if (tierComparison != 0) {
            return tierComparison;
        }
        // Secondary key: Booking Index (smaller index = earlier arrival = higher priority)
        // We compare in reverse (other vs this) because in a max-heap, the highest value is dequeued.
        // If other.bookingIndex is larger than this.bookingIndex, it means 'this' is earlier and should be greater.
        return Long.compare(other.bookingIndex, this.bookingIndex);
    }

    @Override
    public String toString() {
        String roomStr = (allocatedRoom != null) ? "Room " + allocatedRoom.getRoomNumber() : "Unassigned";
        return String.format("Conf: %s | Guest: %s | Type: %s | Room: %s | Status: %s",
                confirmationNumber, guest.getName(), requestedRoomType.getLabel(), roomStr, status.getLabel());
    }
}
