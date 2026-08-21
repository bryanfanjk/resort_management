package boundary;

import adt.List;
import control.HotelController;
import entity.Reservation;
import entity.RoomType;
import entity.WaitingCustomer;

/** Displays active, waiting, and completed-reservation reports. */
public class GenerateReportUI {

    private final HotelController controller;

    public GenerateReportUI(HotelController controller) {
        this.controller = controller;
    }

    /** Displays all reservations, optionally filtered by room type or status. */
    public void displayReservationReport(RoomType roomTypeFilter,
                                         Boolean checkedOutFilter) {
        List<Reservation> reservations = controller.getAllReservationsSorted();
        System.out.println("\n==============================================================================================");
        System.out.println("All Reservations Report");
        System.out.println("==============================================================================================");
        System.out.println("Filter: " + reservationFilterLabel(roomTypeFilter,
                checkedOutFilter));
        System.out.println("Sort By: Check-In Date -> Room Capacity -> Nights Stayed");
        System.out.println("==============================================================================================");
        printReservationHeader();

        int displayed = 0;
        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            boolean typeMatches = roomTypeFilter == null
                    || reservation.getRoom().getRoomType() == roomTypeFilter;
            boolean statusMatches = checkedOutFilter == null
                    || (reservation.getCustomer().getCheckOutDate() != null)
                    == checkedOutFilter;
            if (typeMatches && statusMatches) {
                printReservation(reservation);
                displayed++;
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------");
        if (displayed == 0) {
            System.out.println("No reservations found.");
        }
    }

    /** Displays waiting customers in waiting-position order. */
    public void displayWaitingReport(RoomType roomTypeFilter) {
        List<WaitingCustomer> waitingCustomers = controller.getWaitingCustomers();
        System.out.println("\n================================================================================");
        System.out.println("Standard Customers Waiting List Report");
        System.out.println("================================================================================");
        System.out.println("Filter: " + filterLabel(roomTypeFilter));
        System.out.println("Sort By: Waiting Position Number");
        System.out.println("================================================================================");
        System.out.printf("%-10s %-20s %-8s %-15s %-12s %-12s%n",
                "Position", "Customer Name", "Pax", "Check-in", "Nights",
                "Room Type");
        System.out.println("--------------------------------------------------------------------------------");

        int displayed = 0;
        for (int i = 0; i < waitingCustomers.size(); i++) {
            WaitingCustomer customer = waitingCustomers.get(i);
            if (roomTypeFilter == null
                    || customer.getRequestedRoomType() == roomTypeFilter) {
                System.out.printf("%-10d %-20s %-8d %-15s %-12d %-12s%n",
                        customer.getWaitingPosition(), customer.getCustomerName(),
                        customer.getPax(), customer.getCheckInDate(),
                        customer.getNightsStayed(),
                        customer.getRequestedRoomType().getDisplayName());
                displayed++;
            }
        }
        System.out.println("--------------------------------------------------------------------------------");
        if (displayed == 0) {
            System.out.println("No waiting customers found.");
        }
    }

    private void printReservationHeader() {
        System.out.printf("%-20s %-8s %-15s %-15s %-12s %-12s %-10s%n",
                "Customer Name", "Pax", "Check-in", "Check-out", "Nights",
                "Room Type", "Room");
        System.out.println("-----------------------------------------------------------------------------------------------");
    }

    private void printReservation(Reservation reservation) {
        System.out.printf("%-20s %-8d %-15s %-15s %-12d %-12s %-10d%n",
                reservation.getCustomer().getCustomerName(),
                reservation.getCustomer().getPax(),
                reservation.getCustomer().getCheckInDate(),
                reservation.getCustomer().getCheckOutDate() == null
                        ? "-" : reservation.getCustomer().getCheckOutDate(),
                reservation.getCustomer().getNightsStayed(),
                reservation.getRoom().getRoomType().getDisplayName(),
                reservation.getRoom().getRoomNumber());
    }

    private String filterLabel(RoomType roomTypeFilter) {
        return roomTypeFilter == null ? "All Room Types"
                : roomTypeFilter.getDisplayName() + " Room Type";
    }

    private String reservationFilterLabel(RoomType roomTypeFilter,
                                          Boolean checkedOutFilter) {
        String typeLabel = filterLabel(roomTypeFilter);
        if (checkedOutFilter == null) {
            return typeLabel;
        }
        return typeLabel + " | "
                + (checkedOutFilter ? "Checked Out Customers" : "Active Customers");
    }
}
