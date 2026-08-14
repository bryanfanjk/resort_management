package tarumtresort.boundary;

import tarumtresort.adt.List;
import tarumtresort.adt.Queue;
import tarumtresort.control.HotelController;
import tarumtresort.entity.Reservation;

/** Displays reports */
public class GenerateReportUI {

    private final HotelController controller;

    public GenerateReportUI(HotelController controller) {
        this.controller = controller;
    }

    public void displayReservationReport() {
        List<Reservation> reservations = controller.getReservations();
        System.out.println("\nActive Reservation Report");
        System.out.println("=================================");

        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        System.out.printf("%-20s %-8s %-15s %-15s %-12s %-12s %-10s%n",
                "Customer Name", "Pax", "Check-in", "Check-out", "Nights",
                "Room Type", "Room");
        System.out.println("---------------------------------------------------------------------------------------");

        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
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
    }

    public void displayWaitingReport() {
        Queue<Reservation> waitingQueue = controller.getWaitingQueue();
        System.out.println("\nWaiting List Report");
        System.out.println("=================================");

        if (waitingQueue.isEmpty()) {
            System.out.println("No customers are currently waiting.");
            return;
        }

        System.out.printf("%-10s %-20s %-8s %-15s %-15s %-12s %-12s%n",
                "Position", "Customer Name", "Pax", "Check-in", "Check-out",
                "Nights", "Room Type");
        System.out.println("------------------------------------------------------------------------------------------");

        for (int i = 0; i < waitingQueue.size(); i++) {
            Reservation reservation = waitingQueue.get(i);
            System.out.printf("%-10d %-20s %-8d %-15s %-15s %-12d %-12s%n",
                    i + 1, reservation.getCustomer().getCustomerName(),
                    reservation.getCustomer().getPax(),
                    reservation.getCustomer().getCheckInDate(),
                    reservation.getCustomer().getCheckOutDate() == null
                            ? "-" : reservation.getCustomer().getCheckOutDate(),
                    reservation.getCustomer().getNightsStayed(),
                    reservation.getRequestedRoomType().getDisplayName());
        }
    }

    /** Displays only reservations that have completed checkout. */
    public void displayHistoryReport() {
        List<Reservation> completedReservations =
                controller.getCompletedReservations();
        System.out.println("\nCheckout History Report");
        System.out.println("=================================");

        if (completedReservations.isEmpty()) {
            System.out.println("No checked-out customers found.");
            return;
        }

        System.out.printf("%-20s %-8s %-15s %-15s %-12s %-12s %-10s%n",
                "Customer Name", "Pax", "Check-in", "Check-out", "Nights",
                "Room Type", "Room");
        System.out.println("---------------------------------------------------------------------------------------");

        for (int i = 0; i < completedReservations.size(); i++) {
            Reservation reservation = completedReservations.get(i);
            System.out.printf("%-20s %-8d %-15s %-15s %-12d %-12s %-10d%n",
                    reservation.getCustomer().getCustomerName(),
                    reservation.getCustomer().getPax(),
                    reservation.getCustomer().getCheckInDate(),
                    reservation.getCustomer().getCheckOutDate(),
                    reservation.getCustomer().getNightsStayed(),
                    reservation.getRoom().getRoomType().getDisplayName(),
                    reservation.getRoom().getRoomNumber());
        }
    }
}
