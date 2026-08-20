package entity;

/**
 * Author: <Your Name Here>
 *
 * Room is a plain data object (POJO) - no Scanner input, no System.out
 * output, per ECB rules for entity classes.
 */
public class Room {

    private final int roomNumber;
    private final int floor;
    private final RoomType roomType;
    private boolean available;

    public Room(int roomNumber, int floor, RoomType roomType) {
        this(roomNumber, floor, roomType, true);
    }

    public Room(int roomNumber, int floor, RoomType roomType, boolean available) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.roomType = roomType;
        this.available = available;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Room)) {
            return false;
        }
        Room other = (Room) obj;
        return this.roomNumber == other.roomNumber;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(roomNumber);
    }

    @Override
    public String toString() {
        return String.format("Room %d (Floor %d, %s) - %s",
                roomNumber, floor, roomType, available ? "Available" : "Occupied");
    }
}
