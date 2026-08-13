/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tarumtresort.entity;

public class Reservation {
    private Customer customer;
    private Room room;
    private String confirmationNumber;

    public Reservation(Customer customer, Room room) {
        this.customer = customer;
        this.room = room;
        this.confirmationNumber = "";
    }

    public Reservation(Customer customer, Room room, String confirmationNumber) {
        this.customer = customer;
        this.room = room;
        this.confirmationNumber = confirmationNumber;
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
    
    @Override
    public String toString() {
        return "Ref: " + confirmationNumber + " | " + customer.getCustomerName() +
               " -> Room " + (room != null ? room.getRoomNumber() : "Waiting");
    }

}
