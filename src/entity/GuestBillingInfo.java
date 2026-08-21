/*
 * Entity class representing guest information and billing details.
 */
package entity;

public class GuestBillingInfo {

    private String confirmationNumber;
    private Customer customer;
    private Room room;
    private double dailyRoomRate;
    private double totalBillAmount;

    public GuestBillingInfo(String confirmationNumber, Customer customer, Room room, double dailyRoomRate) {
        this.confirmationNumber = confirmationNumber;
        this.customer = customer;
        this.room = room;
        this.dailyRoomRate = dailyRoomRate;
        this.totalBillAmount = calculateTotalBill();
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
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

    public double getDailyRoomRate() {
        return dailyRoomRate;
    }

    public void setDailyRoomRate(double dailyRoomRate) {
        this.dailyRoomRate = dailyRoomRate;
        this.totalBillAmount = calculateTotalBill();
    }

    public double getTotalBillAmount() {
        return totalBillAmount;
    }

    public void recalculateBill() {
        this.totalBillAmount = calculateTotalBill();
    }

    public double calculateTotalBill() {
        if (customer == null) {
            return 0.0;
        }
        return customer.getNightsStayed() * dailyRoomRate;
    }

    @Override
    public String toString() {
        String roomStr = (room != null) ? "Room " + room.getRoomNumber() : "Waiting for assignment";
        return "Confirmation Number: " + confirmationNumber +
               "\nCustomer Name: " + (customer != null ? customer.getCustomerName() : "N/A") +
               "\nPax: " + (customer != null ? customer.getPax() : 0) +
               "\nCheck-in Date: " + (customer != null ? customer.getCheckInDate() : "N/A") +
               "\nCheck-out Date: " + (customer != null ? customer.getCheckOutDate() : "N/A") +
               "\nNights Stayed: " + (customer != null ? customer.getNightsStayed() : 0) +
               "\nAssigned Room: " + roomStr +
               "\nDaily Room Rate: $" + String.format("%.2f", dailyRoomRate) +
               "\nTotal Bill Amount: $" + String.format("%.2f", totalBillAmount);
    }
}
