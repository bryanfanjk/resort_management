/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

public class Reservation {
    private Customer customer;
    private Room room;
    private RoomType requestedRoomType;

    public Reservation(Customer customer, Room room) {
        this(customer, room, room == null ? null : room.getRoomType());
    }

    public Reservation(Customer customer, Room room,
                       RoomType requestedRoomType) {
        this.customer = customer;
        this.room = room;
        this.requestedRoomType = requestedRoomType;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Room getRoom() {
        return room;
    }
    
    public void setRoom(Room room) {
        this.room = room;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public RoomType getRequestedRoomType() {
        return requestedRoomType;
    }
    
    @Override
    public String toString() {
        return "Ref: " + confirmationNumber + " | " + customer.getCustomerName() +
               " -> Room " + (room != null ? room.getRoomNumber() : "Waiting");
    }

}
