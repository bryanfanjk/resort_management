/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tarumtresort.entity;

public class Customer {
    private String customerName;
    private int pax;
    private String checkInDate;
    private String checkOutDate;
    private int nightsStayed;

    public Customer(String customerName, int pax,
                    String checkInDate, String checkOutDate,
                    int nightsStayed) {

        this.customerName = customerName;
        this.pax = pax;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.nightsStayed = nightsStayed;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getPax() {
        return pax;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public int getNightsStayed() {
        return nightsStayed;
    }

    @Override
    public String toString() {
        return "Customer Name: " + customerName +
               "\nPax: " + pax +
               "\nCheck-in Date: " + checkInDate +
               "\nCheck-out Date: " + checkOutDate +
               "\nNights Stayed: " + nightsStayed;
    }
}
