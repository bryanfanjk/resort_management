package dao;

import entity.Customer;
import entity.CustomerType;
import entity.Reservation;
import entity.Room;

public final class CheckedOutReservationData {

    private CheckedOutReservationData() {
    }

    public static Reservation[] createNew(Room[] rooms) {
        return new Reservation[]{
            createReservation("Hannah Low", 1, "01/08/2026",
                    "03/08/2026", 2, CustomerType.STANDARD,
                    findRoom(rooms, 101)),
            createReservation("Ivan Chong", 2, "01/08/2026",
                    "02/08/2026", 1, CustomerType.VIP,
                    findRoom(rooms, 102))
        };
    }

    private static Reservation createReservation(
            String name, int pax, String checkInDate, String checkOutDate,
            int nightsStayed, CustomerType customerType, Room room) {

        Customer customer = new Customer(name, pax, checkInDate,
                checkOutDate, nightsStayed, customerType);
        return new Reservation(customer, room, room.getRoomType());
    }

    private static Room findRoom(Room[] rooms, int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }
}
