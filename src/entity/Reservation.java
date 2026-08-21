/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

public class Reservation {
    private Customer customer;
    private Room room;
    private RoomType requestedRoomType;
    private String confirmationNumber;

    public Reservation(Customer customer, Room room) {
        this(customer, room, room == null ? null : room.getRoomType(), "");
    }

    public Reservation(Customer customer, Room room, RoomType requestedRoomType) {
        this(customer, room, requestedRoomType, "");
    }

    public Reservation(Customer customer, Room room, String confirmationNumber) {
        this(customer, room, room == null ? null : room.getRoomType(), confirmationNumber);
    }

    public Reservation(Customer customer, Room room,
                       RoomType requestedRoomType, String confirmationNumber) {
        this.customer = customer;
        this.room = room;
        this.requestedRoomType = requestedRoomType;
        if (confirmationNumber != null && !confirmationNumber.isEmpty()) {
            this.confirmationNumber = confirmationNumber;
            if (customer != null && (customer.getConfirmationNumber() == null || customer.getConfirmationNumber().isEmpty())) {
                customer.setConfirmationNumber(confirmationNumber);
            }
        } else if (customer != null && customer.getConfirmationNumber() != null && !customer.getConfirmationNumber().isEmpty()) {
            this.confirmationNumber = customer.getConfirmationNumber();
        } else {
            this.confirmationNumber = "";
        }
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
