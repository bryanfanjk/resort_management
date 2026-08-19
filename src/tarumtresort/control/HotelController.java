package tarumtresort.control;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import tarumtresort.adt.List;
import tarumtresort.dao.RoomData;
import tarumtresort.entity.AssignmentResult;
import tarumtresort.entity.Customer;
import tarumtresort.entity.Reservation;
import tarumtresort.entity.Room;
import tarumtresort.entity.RoomStatus;
import tarumtresort.entity.RoomType;
import tarumtresort.entity.WaitingCustomer;

/** Coordinates check-in, checkout, and list-based waiting-customer assignment. */
public class HotelController {

    private final Room[] rooms;
    private final List<WaitingCustomer> waitingCustomers = new List<>(100);
    private final List<Reservation> activeReservations = new List<>(100);
    private final List<Reservation> completedReservations = new List<>(100);

    public HotelController() {
        this(RoomData.createRooms());
        generateInitialCustomers();
        generateWaitingCustomers();
        generateInitialCheckedOutReservations();
    }

    public HotelController(Room[] rooms) {
        this.rooms = rooms;
    }

    public boolean customerExists(String name) {
        return containsReservationCustomer(activeReservations, name)
                || containsWaitingCustomer(name);
    }

    /** Records a walk-in reservation; staff allocate a room later. */
    public WaitingCustomer addWalkInReservation(Customer customer,
                                                 RoomType requestedRoomType) {
        WaitingCustomer waitingCustomer = new WaitingCustomer(customer,
                requestedRoomType, waitingCustomers.size() + 1);
        waitingCustomers.add(waitingCustomer);
        return waitingCustomer;
    }

    /** Completes checkout and records the reservation in history. */
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

    /* Scans waiting customers in waiting-position order. Stops immediately after assigning one customer. */
    public AssignmentResult allocateRoom() {
        List<WaitingCustomer> skippedCustomers = new List<>(waitingCustomers.size());

        for (int index = 0; index < waitingCustomers.size(); index++) {
            WaitingCustomer waitingCustomer = waitingCustomers.get(index);
            Room room = findAvailableRoom(waitingCustomer.getPax(),
                    waitingCustomer.getRequestedRoomType());

            if (room == null) {
                skippedCustomers.add(waitingCustomer);
                continue;
            }

            waitingCustomers.remove(index);
            resequenceWaitingPositions();
            room.setStatus(RoomStatus.OCCUPIED);
            Reservation reservation = new Reservation(waitingCustomer, room,
                    waitingCustomer.getRequestedRoomType());
            activeReservations.add(reservation);
            return new AssignmentResult(skippedCustomers, reservation);
        }
        return new AssignmentResult(skippedCustomers, null);
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
        return waitingCustomers.size();
    }

    public Room[] getRooms() {
        return rooms.clone();
    }

    /**
     * Returns active and checked-out reservations, ordered by check-in date,
     * then room capacity, then nights stayed.
     */
    public List<Reservation> getAllReservationsSorted() {
        List<Reservation> allReservations = new List<>(
                activeReservations.size() + completedReservations.size());
        addAll(allReservations, activeReservations);
        addAll(allReservations, completedReservations);

        List<Reservation> sortedReservations = new List<>(allReservations.size());
        boolean[] used = new boolean[allReservations.size()];

        for (int resultIndex = 0; resultIndex < allReservations.size(); resultIndex++) {
            int selectedIndex = -1;
            for (int index = 0; index < allReservations.size(); index++) {
                if (!used[index] && (selectedIndex == -1
                        || compareReservations(allReservations.get(index),
                                allReservations.get(selectedIndex)) < 0)) {
                    selectedIndex = index;
                }
            }
            used[selectedIndex] = true;
            sortedReservations.add(allReservations.get(selectedIndex));
        }
        return sortedReservations;
    }

    public List<WaitingCustomer> getWaitingCustomers() {
        return waitingCustomers;
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
        for (int i = 0; i < activeReservations.size(); i++) {
            Reservation reservation = activeReservations.get(i);
            if (reservation.getRoom().getRoomNumber() == roomNumber) {
                activeReservations.remove(i);
                return reservation;
            }
        }
        return null;
    }

    private void resequenceWaitingPositions() {
        for (int index = 0; index < waitingCustomers.size(); index++) {
            waitingCustomers.get(index).setWaitingPosition(index + 1);
        }
    }

    private void addAll(List<Reservation> destination, List<Reservation> source) {
        for (int i = 0; i < source.size(); i++) {
            destination.add(source.get(i));
        }
    }

    private int compareReservations(Reservation first, Reservation second) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int dateComparison = LocalDate.parse(first.getCustomer().getCheckInDate(),
                formatter).compareTo(LocalDate.parse(second.getCustomer().getCheckInDate(),
                formatter));
        if (dateComparison != 0) {
            return dateComparison;
        }

        int capacityComparison = Integer.compare(first.getRoom().getCapacity(),
                second.getRoom().getCapacity());
        if (capacityComparison != 0) {
            return capacityComparison;
        }
        return Integer.compare(first.getCustomer().getNightsStayed(),
                second.getCustomer().getNightsStayed());
    }

    private boolean containsReservationCustomer(List<Reservation> source,
                                                String name) {
        for (int i = 0; i < source.size(); i++) {
            if (source.get(i).getCustomer().getCustomerName()
                    .equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsWaitingCustomer(String name) {
        for (int i = 0; i < waitingCustomers.size(); i++) {
            if (waitingCustomers.get(i).getCustomerName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private void generateInitialCustomers() {
        allocateInitialRoom(new Customer("Yung Onn", 3, "14/08/2026", 2), RoomType.PLATINUM);
        allocateInitialRoom(new Customer("Jia Ming", 1, "14/08/2026", 1), RoomType.PREMIUM);
        allocateInitialRoom(new Customer("Chun Yi", 2, "14/08/2026", 4), RoomType.DELUXE);
        allocateInitialRoom(new Customer("Ali", 1, "14/08/2026", 2), RoomType.DELUXE);
        allocateInitialRoom(new Customer("Brandon Lee", 2, "14/08/2026", 3), RoomType.PREMIUM);
        allocateInitialRoom(new Customer("Mei Ling", 3, "14/08/2026", 4), RoomType.PLATINUM);
    }

    private void generateWaitingCustomers() {
        addWaitingCustomer(new Customer("Daniel Kumar", 2, "14/08/2026", 2),
                RoomType.DELUXE);
        addWaitingCustomer(new Customer("Evelyn Tan", 3, "14/08/2026", 3),
                RoomType.PREMIUM);
        addWaitingCustomer(new Customer("Farid Ismail", 1, "15/08/2026", 1),
                RoomType.DELUXE);
        addWaitingCustomer(new Customer("Grace Wong", 2, "15/08/2026", 2),
                RoomType.PREMIUM);
        addWaitingCustomer(new Customer("Harith Zain", 3, "16/08/2026", 3),
                RoomType.PLATINUM);
        addWaitingCustomer(new Customer("Irene Goh", 1, "16/08/2026", 2),
                RoomType.DELUXE);
        addWaitingCustomer(new Customer("Jason Lim", 3, "17/08/2026", 4),
                RoomType.PREMIUM);
        addWaitingCustomer(new Customer("Kavita Devi", 4, "17/08/2026", 2),
                RoomType.PLATINUM);
        addWaitingCustomer(new Customer("Leon Tan", 2, "18/08/2026", 3),
                RoomType.DELUXE);
        addWaitingCustomer(new Customer("Maya Cheong", 2, "18/08/2026", 1),
                RoomType.PREMIUM);
        addWaitingCustomer(new Customer("Nabil Aziz", 3, "19/08/2026", 5),
                RoomType.PLATINUM);
        addWaitingCustomer(new Customer("Olivia Ng", 1, "19/08/2026", 2),
                RoomType.DELUXE);
        addWaitingCustomer(new Customer("Pavithra Rao", 3, "20/08/2026", 3),
                RoomType.PREMIUM);
        addWaitingCustomer(new Customer("Qamar Shah", 5, "20/08/2026", 2),
                RoomType.PLATINUM);
    }

    private void addWaitingCustomer(Customer customer, RoomType roomType) {
        waitingCustomers.add(new WaitingCustomer(customer, roomType,
                waitingCustomers.size() + 1));
    }

    /** Creates completed reservations with varied data for report demonstrations. */
    private void generateInitialCheckedOutReservations() {
        addCompletedReservation("Hannah Low", 1, "01/08/2026", "03/08/2026", 2, 101);
        addCompletedReservation("Ivan Chong", 2, "01/08/2026", "02/08/2026", 1, 103);
        addCompletedReservation("Jasmine Koh", 1, "02/08/2026", "05/08/2026", 3, 201);
        addCompletedReservation("Khalid Musa", 3, "02/08/2026", "06/08/2026", 4, 203);
        addCompletedReservation("Lina Yap", 3, "03/08/2026", "04/08/2026", 1, 301);
        addCompletedReservation("Nora Lee", 4, "03/08/2026", "08/08/2026", 5, 303);
        addCompletedReservation("Omar Aziz", 1, "04/08/2026", "06/08/2026", 2, 102);
        addCompletedReservation("Priya Nair", 2, "04/08/2026", "09/08/2026", 5, 202);
        addCompletedReservation("Qistina Hamid", 3, "05/08/2026", "07/08/2026", 2, 302);
        addCompletedReservation("Ravi Kumar", 4, "05/08/2026", "12/08/2026", 7, 304);
    }

    private void addCompletedReservation(String name, int pax, String checkInDate,
                                         String checkOutDate, int nightsStayed,
                                         int roomNumber) {
        Room room = findRoom(roomNumber);
        Customer customer = new Customer(name, pax, checkInDate, checkOutDate,
                nightsStayed);
        completedReservations.add(new Reservation(customer, room, room.getRoomType()));
    }

    /** Adds sample customers already allocated to rooms when the app starts. */
    private void allocateInitialRoom(Customer customer, RoomType roomType) {
        Room room = findAvailableRoom(customer.getPax(), roomType);
        if (room != null) {
            room.setStatus(RoomStatus.OCCUPIED);
            activeReservations.add(new Reservation(customer, room, roomType));
        }
    }
}
