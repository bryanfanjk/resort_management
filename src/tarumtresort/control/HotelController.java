package tarumtresort.control;

import tarumtresort.adt.List;
import tarumtresort.adt.Queue;
import tarumtresort.dao.RoomData;
import tarumtresort.entity.Customer;
import tarumtresort.entity.Reservation;
import tarumtresort.entity.Room;
import tarumtresort.entity.RoomStatus;
import tarumtresort.entity.RoomType;

/** The check-in, checkout processes, and waiting-list assignment. */
public class HotelController {

    private final Room[] rooms;
    private final Queue<Reservation> waitingQueue = new Queue<>(100);
    private final List<Reservation> reservations = new List<>(100);
    private final List<Reservation> completedReservations = new List<>(100);

    public HotelController() {
        this(RoomData.createRooms());
        generateInitialCustomers();
        generateWaitingCustomers();
    }

    public HotelController(Room[] rooms) {
        this.rooms = rooms;
    }

    public boolean customerExists(String name) {
        return containsCustomer(reservations, name)
                || containsCustomer(waitingQueue, name);
    }

    public Reservation checkIn(Customer customer, RoomType requestedRoomType) {
        Room room = findAvailableRoom(customer.getPax(), requestedRoomType);
        Reservation reservation = new Reservation(customer, room, requestedRoomType);

        if (room == null) {
            waitingQueue.enqueue(reservation);
        } else {
            room.setStatus(RoomStatus.OCCUPIED);
            reservations.add(reservation);
        }
        return reservation;
    }

    /** Marks an occupied room Available*/
    public boolean checkOut(int roomNumber, String checkOutDate) {
        Room room = findRoom(roomNumber);
        if (room == null || room.getStatus() != RoomStatus.OCCUPIED) {
            return false;
        }

        Reservation reservation = removeReservationForRoom(roomNumber);
        reservation.getCustomer().setCheckOutDate(checkOutDate);
        completedReservations.add(reservation);
        room.setStatus(RoomStatus.AVAILABLE);
        return true;
    }

    /** Assigns every waiting customer for whom a matching available room exists as many as possible. */
    public List<Reservation> assignWaitingCustomers() {
        List<Reservation> assignedReservations = new List<>(waitingQueue.size());
        int index = 0;

        while (index < waitingQueue.size()) {
            Reservation reservation = waitingQueue.get(index);
            Room room = findAvailableRoom(reservation.getCustomer().getPax(),
                    reservation.getRequestedRoomType());

            if (room == null) {
                index++;
                continue;
            }

            waitingQueue.remove(index);
            reservation.setRoom(room);
            room.setStatus(RoomStatus.OCCUPIED);
            reservations.add(reservation);
            assignedReservations.add(reservation);
        }
        return assignedReservations;
    }

    public Room findAvailableRoom(int pax, RoomType roomType) {
        for (Room room : rooms) {
            if (room != null && room.getStatus() == RoomStatus.AVAILABLE
                    && room.getRoomType() == roomType
                    && room.getCapacity() >= pax) {
                return room;
            }
        }
        return null;
    }

    public int getWaitingCount() {
        return waitingQueue.size();
    }

    public Room[] getRooms() {
        return rooms.clone();
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public Queue<Reservation> getWaitingQueue() {
        return waitingQueue;
    }

    public List<Reservation> getCompletedReservations() {
        return completedReservations;
    }

    private Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room != null && room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    private Reservation removeReservationForRoom(int roomNumber) {
        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            if (reservation.getRoom().getRoomNumber() == roomNumber) {
                reservations.remove(i);
                return reservation;
            }
        }
        return null;
    }

    private boolean containsCustomer(List<Reservation> source, String name) {
        for (int i = 0; i < source.size(); i++) {
            if (source.get(i).getCustomer().getCustomerName()
                    .equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsCustomer(Queue<Reservation> source, String name) {
        for (int i = 0; i < source.size(); i++) {
            if (source.get(i).getCustomer().getCustomerName()
                    .equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /** Populate data for checked in customers */
    private void generateInitialCustomers() {
        checkIn(new Customer("Aina Rahman", 1, "14/08/2026", 2),
                RoomType.DELUXE);
        checkIn(new Customer("Brandon Lee", 2, "14/08/2026", 3),
                RoomType.PREMIUM);
        checkIn(new Customer("Chong Mei Ling", 3, "14/08/2026", 4),
                RoomType.PLATINUM);
    }

    /** Populate data for waiting customers */
    private void generateWaitingCustomers() {
        waitingQueue.enqueue(new Reservation(
                new Customer("Daniel Kumar", 2, "14/08/2026", 2),
                null, RoomType.DELUXE));
        waitingQueue.enqueue(new Reservation(
                new Customer("Evelyn Tan", 3, "14/08/2026", 3),
                null, RoomType.PREMIUM));
    }
}
