/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

public class Room {
    private int roomNumber;
    private int capacity;
    private RoomType roomType;
    private RoomStatus status;
    private HousekeepingStatus housekeepingStatus;
    private String currentGuestConfirmation;

    
    public Room(int roomNumber, int capacity) {
        this(roomNumber, capacity, RoomType.DELUXE);
    }

    public Room(int roomNumber, int capacity, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.roomType = roomType;
        this.status = RoomStatus.AVAILABLE;
        this.housekeepingStatus = HousekeepingStatus.READY;
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
        return status == RoomStatus.AVAILABLE;
    }

    public void setAvailable(boolean available) {
        this.status = available ? RoomStatus.AVAILABLE : RoomStatus.OCCUPIED;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public HousekeepingStatus getHousekeepingStatus() {
        return housekeepingStatus;
    }

    public void setHousekeepingStatus(HousekeepingStatus housekeepingStatus) {
        this.housekeepingStatus = housekeepingStatus;
    }

    public boolean isVacant() {
        return status == RoomStatus.AVAILABLE;
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
               " | Status: " + status.getDisplayName() +
               " | Housekeeping: " + housekeepingStatus.getLabel();
    }
}
