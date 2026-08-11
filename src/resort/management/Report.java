package resort.management;

import tarumtresort.adt.List;
import tarumtresort.adt.Queue;
import tarumtresort.entity.Reservation;

public class Report {

    public static void displayReservationReport(
            List<Reservation> reservations) {

        System.out.println("\nReservation Report");
        System.out.println("=================================");

        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        System.out.printf(
                "%-20s %-8s %-15s %-15s %-12s %-10s%n",
                "Customer Name",
                "Pax",
                "Check-in",
                "Check-out",
                "Nights",
                "Room"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (int i = 0; i < reservations.size(); i++) {

            Reservation reservation = reservations.get(i);

            String roomNumber;

            if (reservation.getRoom() == null) {
                roomNumber = "Waiting";
            } else {
                roomNumber = String.valueOf(
                        reservation.getRoom().getRoomNumber()
                );
            }

            System.out.printf(
                    "%-20s %-8d %-15s %-15s %-12d %-10s%n",
                    reservation.getCustomer().getCustomerName(),
                    reservation.getCustomer().getPax(),
                    reservation.getCustomer().getCheckInDate(),
                    reservation.getCustomer().getCheckOutDate(),
                    reservation.getCustomer().getNightsStayed(),
                    roomNumber
            );
        }
    }

    public static void displayWaitingReport(
            Queue<Reservation> waitingQueue) {

        System.out.println("\nWaiting List Report");
        System.out.println("=================================");

        if (waitingQueue.isEmpty()) {
            System.out.println("No customers are currently waiting.");
            return;
        }

        System.out.printf(
                "%-10s %-20s %-8s %-15s %-15s %-12s%n",
                "Position",
                "Customer Name",
                "Pax",
                "Check-in",
                "Check-out",
                "Nights"
        );

        System.out.println(
                "----------------------------------------------------------------------------"
        );

        for (int i = 0; i < waitingQueue.size(); i++) {

            Reservation reservation = waitingQueue.get(i);

            System.out.printf(
                    "%-10d %-20s %-8d %-15s %-15s %-12d%n",
                    i + 1,
                    reservation.getCustomer().getCustomerName(),
                    reservation.getCustomer().getPax(),
                    reservation.getCustomer().getCheckInDate(),
                    reservation.getCustomer().getCheckOutDate(),
                    reservation.getCustomer().getNightsStayed()
            );
        }
    }
}