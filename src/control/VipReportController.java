/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;
import adt.List;
import entity.CustomerType;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import entity.WaitingCustomer;

/**
 *
 * @author Ng Yung Onn 
 */
public class VipReportController {

    private final HotelController controller;

    public VipReportController(HotelController controller) {
        this.controller = controller;
    }

   public List<WaitingCustomer> getFilteredVipCustomers(
            RoomType roomTypeFilter,
            String nameFilter) {

        adt.VipList<WaitingCustomer> vipCustomers =
                controller.getVipWaitingCustomers();

        List<WaitingCustomer> filteredCustomers =
                new List<>(Math.max(1, vipCustomers.size()));

        String normalizedName = nameFilter == null
                ? ""
                : nameFilter.trim().toLowerCase();

        for (int index = 0;
             index < vipCustomers.size();
             index++) {

            WaitingCustomer customer =
                    vipCustomers.get(index);

            boolean matchesRoomType =
                    roomTypeFilter == null
                    || customer.getRequestedRoomType()
                    == roomTypeFilter;

            boolean matchesName =
                    normalizedName.isEmpty()
                    || customer.getCustomerName()
                    .toLowerCase()
                    .contains(normalizedName);

            if (matchesRoomType && matchesName) {
                filteredCustomers.add(customer);
            }
        }

        return filteredCustomers;
    }
   
   public void sortVipCustomers(List<WaitingCustomer> customers, int sortChoice) {

        for (int start = 0;
             start < customers.size() - 1;
             start++) {

            int selectedIndex = start;

            for (int index = start + 1; index < customers.size();index++) {
                if (compareVipCustomers(customers.get(index),customers.get(selectedIndex),sortChoice) < 0) {
                    selectedIndex = index;
                }
            }

            if (selectedIndex != start) { 
                WaitingCustomer selectedCustomer = customers.get(selectedIndex);
                customers.set(selectedIndex,customers.get(start));
                customers.set(start,selectedCustomer);
            }
        }
    }

    private int compareVipCustomers(WaitingCustomer first,WaitingCustomer second, int sortChoice) {

        switch (sortChoice) {
            case 2:
                return first.getCustomerName()
                        .compareToIgnoreCase(
                                second.getCustomerName());

            case 3:
                return Integer.compare(
                        first.getPax(),
                        second.getPax());

            case 4:
                return Integer.compare(
                        first.getNightsStayed(),
                        second.getNightsStayed());

            default:
                return Integer.compare(
                        first.getWaitingPosition(),
                        second.getWaitingPosition());
        }
    }

    public boolean sameRequirements(WaitingCustomer first,WaitingCustomer second) {
        return first.getCustomerName().equalsIgnoreCase(second.getCustomerName())
                && first.getPax() == second.getPax()
                && first.getCheckInDate().equals(second.getCheckInDate())
                && first.getNightsStayed() == second.getNightsStayed()
                && first.getRequestedRoomType() == second.getRequestedRoomType();
    }

    public int countVipDemand(RoomType roomType) {
        int count = 0;

        adt.VipList<WaitingCustomer> vipCustomers =
                controller.getVipWaitingCustomers();

        for (int index = 0;
             index < vipCustomers.size();
             index++) {

            WaitingCustomer customer =
                    vipCustomers.get(index);

            if (customer.getRequestedRoomType()
                    == roomType) {

                count++;
            }
        }

        return count;
    }

    public int countAvailableRooms(RoomType roomType) {
        int count = 0;

        for (Room room : controller.getRooms()) {
            if (room.getRoomType() == roomType
                    && room.isAvailable()) {

                count++;
            }
        }

        return count;
    }

    public String getAssessmentLabel(int gap) {
        if (gap > 0) {
            return "Demand exceeds supply";
        } else if (gap == 0) {
            return "Balanced";
        } else {
            return "Supply exceeds demand";
        }
    }

    public List<Reservation> getVipReservationHistory(
            RoomType roomTypeFilter,
            Boolean checkedOutFilter) {

        List<Reservation> reservations =
                controller.getAllReservationsSorted();

        List<Reservation> vipReservations =
                new List<>(Math.max(1, reservations.size()));

        for (int index = 0; index < reservations.size(); index++) {
            Reservation reservation = reservations.get(index);

            boolean isVip =
                    reservation.getCustomer().getCustomerType()
                    == CustomerType.VIP;

            boolean matchesRoomType =
                    roomTypeFilter == null
                    || reservation.getRoom().getRoomType() == roomTypeFilter;

            boolean isCheckedOut =
                    reservation.getCustomer().getCheckOutDate() != null;

            boolean matchesStatus =
                    checkedOutFilter == null
                    || isCheckedOut == checkedOutFilter;

            if (isVip && matchesRoomType && matchesStatus) {
                vipReservations.add(reservation);
            }
        }

        return vipReservations;
    }
    
    
    public String getReservationStatusLabel(Boolean checkedOutFilter) {

    if (checkedOutFilter == null) {
        return "Active and Checked-out VIP Customers";
    }

    if (checkedOutFilter) {
        return "Checked-out VIP Customers Only";
    }

    return "Active VIP Customers Only";
    }
}