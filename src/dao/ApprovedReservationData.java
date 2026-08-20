package dao;

import entity.Customer;
import entity.CustomerType;
import entity.Reservation;
import entity.Room;

public final class ApprovedReservationData {

    private ApprovedReservationData() {
    }

    public static Reservation[] createNew(Room[] rooms) {
        return new Reservation[]{
            createReservation("Yung Onn", 3, "14/08/2026", 2,
                    CustomerType.STANDARD, findRoom(rooms, 301)),
            createReservation("Jia Ming", 1, "14/08/2026", 1,
                    CustomerType.STANDARD, findRoom(rooms, 201)),
            createReservation("Chun Yi", 2, "14/08/2026", 4,
                    CustomerType.STANDARD, findRoom(rooms, 103)),
            createReservation("VIP Guest One", 2, "15/08/2026", 3,
                    CustomerType.VIP, findRoom(rooms, 203))
        };
    }

    private static Reservation createReservation(
            String name, int pax, String checkInDate, int nightsStayed,
            CustomerType customerType, Room room) {
        Customer customer = new Customer(name, pax, checkInDate,
                nightsStayed, customerType);
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
