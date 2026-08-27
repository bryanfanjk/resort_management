package boundary;

import adt.List;
import control.HotelController;
import entity.Room;
import entity.RoomType;
import entity.WaitingCustomer;
import entity.Reservation;

//author: Ng Yung Onn

public class GenReportVipUI {

    private static final String TITLE_DIVIDER = divider('=', 100);
    private static final String ROW_DIVIDER = divider('-', 100);
    private final HotelController controller;

    public GenReportVipUI(HotelController controller) {
        this.controller = controller;
    }

    /*
     * Report 1:
     * VIP demand compared with currently available rooms.
     */
    public void displayVipDemandAvailabilityReport() {
        System.out.println("\n" + TITLE_DIVIDER);
        System.out.println("VIP Demand vs. Room Availability Summary");
        System.out.println(TITLE_DIVIDER);

        System.out.printf(
                "%-12s %-18s %-18s %-25s%n",
                "Room Type",
                "VIPs Waiting",
                "Rooms Available",
                "Assessment");

        System.out.println(ROW_DIVIDER);

        for (RoomType roomType : RoomType.values()) {
            int vipDemand = countVipDemand(roomType);
            int availableRooms = countAvailableRooms(roomType);

            String assessment;

            if (vipDemand > availableRooms) {
                assessment = "Demand exceeds supply";
            } else if (vipDemand == availableRooms) {
                assessment = "Balanced";
            } else {
                assessment = "Supply exceeds demand";
            }

            System.out.printf(
                    "%-12s %-18d %-18d %-25s%n",
                    roomType.getDisplayName(),
                    vipDemand,
                    availableRooms,
                    assessment);
        }

        System.out.println(ROW_DIVIDER);
    }

    /*
     * Report 2:
     * VIP waiting customers with room-type filtering,
     * name searching, and sorting.
     *
     * sortChoice:
     * 1 = Waiting position
     * 2 = Customer name
     * 3 = Number of guests
     * 4 = Nights stayed
     */
    public void displayVipCustomerReport(
            RoomType roomTypeFilter,
            String nameFilter,
            int sortChoice) {

        List<WaitingCustomer> customers =
                getFilteredVipCustomers(roomTypeFilter, nameFilter);

        sortCustomers(customers, sortChoice);

        System.out.println("\n" + TITLE_DIVIDER);
        System.out.println("VIP Customer Summary Report");
        System.out.println(TITLE_DIVIDER);
        System.out.println("Room Type Filter: "
                + getRoomFilterLabel(roomTypeFilter));
        System.out.println("Name Filter: "
                + getNameFilterLabel(nameFilter));
        System.out.println("Sort By: "
                + getSortLabel(sortChoice));
        System.out.println(TITLE_DIVIDER);

        System.out.printf(
                "%-10s %-20s %-8s %-15s %-12s %-12s %-10s%n",
                "Position",
                "Customer Name",
                "Pax",
                "Check-in",
                "Nights",
                "Room Type",
                "Quantity");

        System.out.println(ROW_DIVIDER);

        boolean[] displayedCustomers = new boolean[customers.size()];
        for (int index = 0; index < customers.size(); index++) {

            WaitingCustomer customer =
                    customers.get(index);
            if (!displayedCustomers[index]) {
                int quantity = 1;
                for (int candidateIndex = index + 1;
                        candidateIndex < customers.size(); candidateIndex++) {
                    if (sameRequirements(customer, customers.get(candidateIndex))) {
                        quantity++;
                        displayedCustomers[candidateIndex] = true;
                    }
                }
                System.out.printf(
                        "%-10d %-20s %-8d %-15s %-12d %-12s %-10d%n",
                        customer.getWaitingPosition(),
                        customer.getCustomerName(),
                        customer.getPax(),
                        customer.getCheckInDate(),
                        customer.getNightsStayed(),
                        customer.getRequestedRoomType().getDisplayName(),
                        quantity);
            }
        }

        System.out.println(ROW_DIVIDER);

        if (customers.isEmpty()) {
            System.out.println(
                    "No VIP customers match the selected criteria.");
        }
    }

    private boolean sameRequirements(WaitingCustomer first,
                                     WaitingCustomer second) {
        return first.getCustomerName().equalsIgnoreCase(second.getCustomerName())
                && first.getPax() == second.getPax()
                && first.getCheckInDate().equals(second.getCheckInDate())
                && first.getNightsStayed() == second.getNightsStayed()
                && first.getRequestedRoomType() == second.getRequestedRoomType();
    }

    private List<WaitingCustomer> getFilteredVipCustomers(
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

    private void sortCustomers(
            List<WaitingCustomer> customers,
            int sortChoice) {

        for (int start = 0;
             start < customers.size() - 1;
             start++) {

            int selectedIndex = start;

            for (int index = start + 1;
                 index < customers.size();
                 index++) {

                if (compareCustomers(
                        customers.get(index),
                        customers.get(selectedIndex),
                        sortChoice) < 0) {

                    selectedIndex = index;
                }
            }

            if (selectedIndex != start) {
                WaitingCustomer selectedCustomer =
                        customers.get(selectedIndex);

                customers.set(
                        selectedIndex,
                        customers.get(start));

                customers.set(
                        start,
                        selectedCustomer);
            }
        }
    }

    private int compareCustomers(
            WaitingCustomer first,
            WaitingCustomer second,
            int sortChoice) {

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

    private int countVipDemand(RoomType roomType) {
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

    private int countAvailableRooms(RoomType roomType) {
        int count = 0;

        for (Room room : controller.getRooms()) {
            if (room.getRoomType() == roomType
                    && room.isAvailable()) {

                count++;
            }
        }

        return count;
    }

    private String getRoomFilterLabel(
            RoomType roomTypeFilter) {

        return roomTypeFilter == null
                ? "All Room Types"
                : roomTypeFilter.getDisplayName();
    }

    private String getNameFilterLabel(
            String nameFilter) {

        return nameFilter == null
                || nameFilter.trim().isEmpty()
                ? "All Names"
                : nameFilter.trim();
    }

    private String getSortLabel(int sortChoice) {
        switch (sortChoice) {
            case 2:
                return "Customer Name";
            case 3:
                return "Number of Guests";
            case 4:
                return "Nights Stayed";
            default:
                return "Waiting Position";
        }
    }
    
    
        /*
     * Report 3:
     * Displays VIP customers who have already been served.
     *
     * checkedOutFilter:
     * null  = active and checked-out VIP customers
     * true  = checked-out VIP customers only
     * false = active VIP customers only
     *
     * The reservations returned by getAllReservationsSorted()
     * are already sorted by:
     * 1. Check-in date
     * 2. Room capacity
     * 3. Nights stayed
     */
    public void displayVipReservationHistoryReport( RoomType roomTypeFilter,Boolean checkedOutFilter) {

        List<Reservation> reservations =
                controller.getAllReservationsSorted();

        List<Reservation> vipReservations =
                new List<>(Math.max(1, reservations.size()));

        for (int index = 0;
             index < reservations.size();
             index++) {

            Reservation reservation =
                    reservations.get(index);

            boolean isVip =
                    reservation.getCustomer().getCustomerType()
                    == entity.CustomerType.VIP;

            boolean matchesRoomType =
                    roomTypeFilter == null
                    || reservation.getRoom().getRoomType()
                    == roomTypeFilter;

            boolean isCheckedOut =
                    reservation.getCustomer().getCheckOutDate()
                    != null;

            boolean matchesStatus =
                    checkedOutFilter == null
                    || isCheckedOut == checkedOutFilter;

            if (isVip && matchesRoomType && matchesStatus) {
                vipReservations.add(reservation);
            }
        }

        System.out.println("\n" + TITLE_DIVIDER);
        System.out.println("VIP Reservation History Report");
        System.out.println(TITLE_DIVIDER);
        System.out.println("Room Type Filter: "
                + getRoomFilterLabel(roomTypeFilter));
        System.out.println("Status Filter: "
                + getReservationStatusLabel(checkedOutFilter));
        System.out.println(
                "Sort By: Check-in Date -> Room Capacity -> Nights Stayed");
        System.out.println(TITLE_DIVIDER);

        System.out.printf(
                "%-20s %-8s %-15s %-15s %-12s %-12s %-10s%n",
                "Customer Name",
                "Pax",
                "Check-in",
                "Check-out",
                "Nights",
                "Room Type",
                "Room");

        System.out.println(ROW_DIVIDER);

        for (int index = 0;
             index < vipReservations.size();
             index++) {

            Reservation reservation =
                    vipReservations.get(index);

            System.out.printf(
                    "%-20s %-8d %-15s %-15s %-12d %-12s %-10d%n",
                    reservation.getCustomer().getCustomerName(),
                    reservation.getCustomer().getPax(),
                    reservation.getCustomer().getCheckInDate(),
                    reservation.getCustomer().getCheckOutDate() == null
                            ? "-"
                            : reservation.getCustomer().getCheckOutDate(),
                    reservation.getCustomer().getNightsStayed(),
                    reservation.getRoom()
                            .getRoomType()
                            .getDisplayName(),
                    reservation.getRoom().getRoomNumber());
        }

        System.out.println(ROW_DIVIDER);

        if (vipReservations.isEmpty()) {
            System.out.println(
                    "No VIP reservations match the selected criteria.");
        }
    }
    
    private String getReservationStatusLabel(Boolean checkedOutFilter) {

    if (checkedOutFilter == null) {
        return "Active and Checked-out VIP Customers";
    }

    if (checkedOutFilter) {
        return "Checked-out VIP Customers Only";
    }

    return "Active VIP Customers Only";
    }

    private static String divider(char character, int length) {
        StringBuilder line = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            line.append(character);
        }
        return line.toString();
    }
}
