/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

public class Customer {
    private String customerName;
    private int pax;
    private String checkInDate;
    private String checkOutDate;
    private int nightsStayed;
    private CustomerType customerType;

    private String confirmationNumber;

    public Customer(String customerName, int pax, String checkInDate,
            int nightsStayed, CustomerType customerType) {
        this(customerName, pax, checkInDate, null, nightsStayed,
                customerType, "");
    }

    public Customer(String customerName, int pax,
                    String checkInDate, String checkOutDate,
                    int nightsStayed, CustomerType customerType) {
        this(customerName, pax, checkInDate, checkOutDate, nightsStayed,
                customerType, "");
    }

    public Customer(String customerName, int pax,
                    String checkInDate, String checkOutDate,
                    int nightsStayed, CustomerType customerType,
                    String confirmationNumber) {

        this.customerName = customerName;
        this.pax = pax;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.nightsStayed = nightsStayed;
        this.customerType = customerType;
        this.confirmationNumber = confirmationNumber != null ? confirmationNumber : "";
    }
    
    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber != null ? confirmationNumber : "";
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
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

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
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
