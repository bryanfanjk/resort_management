/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

public class Room {
    private int roomNumber;
    private int capacity;
    private RoomType roomType;
    private RoomStatus occupancyStatus;
    private RoomStatus roomStatus;
    private String currentGuestConfirmation;

    
    public Room(int roomNumber, int capacity) {
        this(roomNumber, capacity, RoomType.DELUXE);
    }

    public Room(int roomNumber, int capacity, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.roomType = roomType;
        this.occupancyStatus = RoomStatus.AVAILABLE;
        this.roomStatus = RoomStatus.READY;
        this.currentGuestConfirmation = null;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public boolean isAvailable() {
        return occupancyStatus == RoomStatus.AVAILABLE;
    }

    public void setAvailable(boolean available) {
        this.occupancyStatus = available ? RoomStatus.AVAILABLE : RoomStatus.OCCUPIED;
    }

    public RoomStatus getOccupancyStatus() {
        return occupancyStatus;
    }

    public void setOccupancyStatus(RoomStatus occupancyStatus) {
        this.occupancyStatus = occupancyStatus;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    /**
     * Sets the housekeeping status and keeps occupancy in sync:
     * reaching READY makes the room bookable (AVAILABLE); any earlier
     * housekeeping step (DIRTY/CLEANING_IN_PROGRESS/INSPECTED) keeps it
     * UNAVAILABLE. Does nothing to occupancy while a guest is checked in.
     */
    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
        if (occupancyStatus != RoomStatus.OCCUPIED) {
            occupancyStatus = (roomStatus == RoomStatus.READY)
                    ? RoomStatus.AVAILABLE : RoomStatus.UNAVAILABLE;
        }
    }

    public boolean isOccupied() {
        return occupancyStatus == RoomStatus.OCCUPIED;
    }

    public boolean isVacant() {
        return occupancyStatus != RoomStatus.OCCUPIED;
    }

    public String getCurrentGuestConfirmation() {
        return currentGuestConfirmation;
    }

    public void setCurrentGuestConfirmation(String currentGuestConfirmation) {
        this.currentGuestConfirmation = currentGuestConfirmation;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber +
               " | Type: " + roomType.getDisplayName() +
               " | Capacity: " + capacity +
               " | Status: " + occupancyStatus.getLabel() +
               " | Housekeeping: " + roomStatus.getLabel();
    }
}
