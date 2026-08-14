/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

public class Customer implements Comparable<Customer>{
    private String customerName;
    private int pax;
    private String checkInDate;
    private String checkOutDate;
    private int nightsStayed;
    private LoyaltyTier tier;
    private int loyaltyPoints;
    private final int REGISTRATION_SEQUENCE;
    private static int sequenceCounter = 0;

    public Customer(String customerName, int pax,
                    String checkInDate, String checkOutDate,
                    int nightsStayed, LoyaltyTier tier, int loyaltyPoints) {

        this.customerName = customerName;
        this.pax = pax;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.nightsStayed = nightsStayed;
        this.tier =tier;
        this.loyaltyPoints = loyaltyPoints;
        this.REGISTRATION_SEQUENCE = sequenceCounter++;
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

    public LoyaltyTier getTier() {
        return tier;
    }

    public void setTier(LoyaltyTier tier) {
        this.tier = tier;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
    
    public int getRegistrationSequence(){
        return this.REGISTRATION_SEQUENCE;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public int getNightsStayed() {
        return nightsStayed;
    }
    
        @Override
    public int compareTo(Customer other) {
        int tierComparison = this.tier.getRank() - other.tier.getRank();
        if (tierComparison != 0) {
            return tierComparison;
        }
        int pointsComparison = this.loyaltyPoints - other.loyaltyPoints;
        if (pointsComparison != 0) {
            return pointsComparison;
        }
        // Earlier arrival (smaller sequence number) must produce a LARGER
        // compareTo result, since smaller sequence = higher priority.
        return (int) (other.REGISTRATION_SEQUENCE - this.REGISTRATION_SEQUENCE);
    }


    @Override
    public String toString() {
        return "Customer Name: " + customerName +
               "\nPax: " + pax +
               "\nCheck-in Date: " + checkInDate +
               "\nCheck-out Date: " + checkOutDate +
               "\nNights Stayed: " + nightsStayed +
               "\nTier:"  + tier.getLabel() +
               "\nLoyalty Points: "+ loyaltyPoints;
    }
}
