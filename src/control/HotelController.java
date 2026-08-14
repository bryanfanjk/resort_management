package control;

import adt.EmptyCollectionException;
import adt.List;
import adt.MaxHeapPriorityQueue;
import adt.PriorityQueue;
import adt.Queue;
import dao.RoomData;
import entity.Customer;
import entity.Reservation;
import entity.Room;
import entity.RoomStatus;
import entity.RoomType;

/** The check-in, checkout processes, and waiting-list assignment. */
public class HotelController {

    private final PriorityQueue<Customer> vipQueue;

    public HotelController() {
        this(RoomData.createRooms());
        this.vipQueue = new MaxHeapPriorityQueue<>();
    }
    private final Room[] rooms;
    private final Queue<Reservation> waitingQueue = new Queue<>(100);
    private final List<Reservation> reservations = new List<>(100);


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
    
    public Customer serveNextGuest() throws EmptyCollectionException {
        return vipQueue.removeHighestPriority();
    }

    /** Marks an occupied room Available*/
    public boolean checkOut(int roomNumber) {
        Room room = findRoom(roomNumber);
        if (room == null || room.getStatus() != RoomStatus.OCCUPIED) {
            return false;
        }

        removeReservationForRoom(roomNumber);
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

    private Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room != null && room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    private void removeReservationForRoom(int roomNumber) {
        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            if (reservation.getRoom().getRoomNumber() == roomNumber) {
                reservations.remove(i);
                return;
            }
        }
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
}
