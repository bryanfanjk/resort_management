package control;

import adt.List;
import dao.ApprovedReservationData;
import dao.CheckedOutReservationData;
import dao.RoomData;
import dao.StandardWaitingCustomerData;
import dao.VipWaitingCustomerData;
import entity.AssignmentResult;
import entity.Customer;
import entity.CustomerType;
import entity.GuestBillingInfo;
import entity.HousekeepingStatus;
import entity.Reservation;
import entity.Room;
import entity.RoomStatus;
import entity.RoomType;
import entity.WaitingCustomer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Coordinates check-in, checkout, and list-based waiting-customer assignment.
 */
/* author: All Members have contributed in this controller. */
public class HotelController {

    private final Room[] rooms;
    private final List<WaitingCustomer> waitingCustomers = new List<>(100);
    private final List<Reservation> activeReservations = new List<>(100);
    private final List<Reservation> completedReservations = new List<>(100);
    private final VipAllocationController vipController = new VipAllocationController();
    private final FrontDeskControl frontDeskControl;
    private int confirmationCounter = 1;

    public HotelController() {
        this(RoomData.createRooms());
    }

    public HotelController(Room[] rooms) {
        this.rooms = rooms;
        this.frontDeskControl = new FrontDeskControl(this.rooms);
        loadApprovedReservations();
        loadCheckedOutReservations();
        loadVipWaitingCustomers();
        loadStandardWaitingCustomers();
    }

    public FrontDeskControl getFrontDeskControl() {
        return frontDeskControl;
    }

    /* author: Fan Jin Kit & Ng Yung Onn*/
    public boolean customerExists(String name) {
        return containsReservationCustomer(activeReservations, name)
                || containsReservationCustomer(completedReservations, name)
                || containsWaitingCustomer(name)
                || containsVipWaitingCustomer(name);
    }

    /* author: Fan Jin Kit & Ng Yung Onn*/
    public WaitingCustomer addWalkInReservation(Customer customer,
            RoomType requestedRoomType) {
        return addWalkInReservation(customer, requestedRoomType, null);
    }

    public WaitingCustomer addWalkInReservation(Customer customer,
            RoomType requestedRoomType,
            String vipCode) {
        CustomerType customerType = vipController.isValidVipCode(vipCode)
                ? CustomerType.VIP : CustomerType.STANDARD;
        customer.setCustomerType(customerType);

        String confCode = String.format("CONF%04d", confirmationCounter++);
        customer.setConfirmationNumber(confCode);

        WaitingCustomer waitingCustomer = new WaitingCustomer(customer,
                requestedRoomType, getTotalWaitingCount() + 1);
        if (customerType == CustomerType.VIP) {
            vipController.addVip(waitingCustomer);
        } else {
            waitingCustomers.add(waitingCustomer);
        }

        double rate = FrontDeskControl.getDailyRate(requestedRoomType);
        GuestBillingInfo guestInfo = new GuestBillingInfo(confCode, customer, null, rate);
        frontDeskControl.registerGuestInfo(guestInfo);

        return waitingCustomer;
    }

    /* author: Fan Jin Kit */
    /**
     * Completes checkout and records the reservation in history.
     */
    public boolean checkOut(int roomNumber, String checkOutDate) {
        Room room = findRoom(roomNumber);
        if (room == null || room.getStatus() != RoomStatus.OCCUPIED) {
            return false;
        }

        Reservation reservation = removeReservationForRoom(roomNumber);
        if (reservation == null) {
            return false;
        }
        reservation.getCustomer().setCheckOutDate(checkOutDate);
        completedReservations.add(reservation);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setHousekeepingStatus(HousekeepingStatus.DIRTY);
        room.setCurrentGuestConfirmation(null);

        frontDeskControl.updateGuestCheckout(reservation.getConfirmationNumber(), checkOutDate);
        return true;
    }

    /* author: Fan Jin Kit & Ng Yung Onn*/
 /* Scans waiting customers in waiting-position order. Stops immediately after assigning one customer. */
    public AssignmentResult allocateRoom() {
        List<WaitingCustomer> skippedCustomers = new List<>(
                getTotalWaitingCount());

        for (int index = 0; index < vipController.waitingVipCount(); index++) {
            WaitingCustomer waitingCustomer = vipController.getVip(index);
            Room room = findAvailableRoom(waitingCustomer.getPax(),
                    waitingCustomer.getRequestedRoomType());
            if (room != null) {
                vipController.removeVip(index);
                resequenceVipWaitingPositions();
                return approveCustomer(waitingCustomer, room, skippedCustomers);
            }
            skippedCustomers.add(waitingCustomer);
        }

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
            return approveCustomer(waitingCustomer, room, skippedCustomers);
        }
        return new AssignmentResult(skippedCustomers, null);
    }

    /* author: Fan Jin Kit */
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

    /* author: Fan Jin Kit */
    public int getWaitingCount() {
        return getTotalWaitingCount();
    }

    /* author: Ng Yung Onn */
    public Room[] getRooms() {
        return rooms.clone();
    }

    /* author: Fan Jin Kit */
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

    /* author: Fan Jin Kit */
    public List<WaitingCustomer> getWaitingCustomers() {
        return waitingCustomers;
    }

    /* author: Ng Yung Onn */
    public adt.VipList<WaitingCustomer> getVipWaitingCustomers() {
        return vipController.getVipList();
    }

    private Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room != null && room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    /* author: Fan Jin Kit */
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

    /* author: Fan Jin Kit */
    private void resequenceWaitingPositions() {
        for (int index = 0; index < waitingCustomers.size(); index++) {
            waitingCustomers.get(index).setWaitingPosition(index + 1);
        }
    }

    /* author: Fan Jin Kit */
    private void addAll(List<Reservation> destination, List<Reservation> source) {
        for (int i = 0; i < source.size(); i++) {
            destination.add(source.get(i));
        }
    }

    /* author: Fan Jin Kit */
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

    /* author: Fan Jin Kit */
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

    /* author: Fan Jin Kit */
    private boolean containsWaitingCustomer(String name) {
        for (int i = 0; i < waitingCustomers.size(); i++) {
            if (waitingCustomers.get(i).getCustomerName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /* author: Ng Yung Onn */
    private boolean containsVipWaitingCustomer(String name) {
        for (int index = 0; index < vipController.waitingVipCount(); index++) {
            if (vipController.getVip(index).getCustomerName()
                    .equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /* author: Ng Yung Onn*/
    private int getTotalWaitingCount() {
        return waitingCustomers.size() + vipController.waitingVipCount();
    }

    /* author: Fan Jin Kit*/
    private AssignmentResult approveCustomer(
            WaitingCustomer customer, Room room,
            List<WaitingCustomer> skippedCustomers) {
        room.setStatus(RoomStatus.OCCUPIED);
        room.setCurrentGuestConfirmation(customer.getCustomerName());
        Reservation reservation = new Reservation(customer, room,
                customer.getRequestedRoomType(), customer.getConfirmationNumber());
        activeReservations.add(reservation);

        frontDeskControl.updateGuestRoomAssignment(customer.getConfirmationNumber(), room);

        return new AssignmentResult(skippedCustomers, reservation);
    }

    /* author: Ng Yung Onn*/
    private void resequenceVipWaitingPositions() {
        for (int index = 0; index < vipController.waitingVipCount(); index++) {
            vipController.getVip(index).setWaitingPosition(index + 1);
        }
    }

    /* author: Ng Yung Onn*/
    private void loadApprovedReservations() {
        Reservation[] reservations = ApprovedReservationData.createNew(rooms);
        for (Reservation reservation : reservations) {
            String confCode = String.format("CONF%04d", confirmationCounter++);
            reservation.setConfirmationNumber(confCode);
            if (reservation.getCustomer() != null) {
                reservation.getCustomer().setConfirmationNumber(confCode);
            }
            reservation.getRoom().setStatus(RoomStatus.OCCUPIED);
            reservation.getRoom().setCurrentGuestConfirmation(reservation.getCustomer().getCustomerName());
            activeReservations.add(reservation);

            double rate = FrontDeskControl.getDailyRate(reservation.getRoom().getRoomType());
            GuestBillingInfo guestInfo = new GuestBillingInfo(confCode, reservation.getCustomer(), reservation.getRoom(), rate);
            frontDeskControl.registerGuestInfo(guestInfo);
        }
    }

    /* author: Fan Jin Kit */
    private void loadCheckedOutReservations() {
        Reservation[] reservations = CheckedOutReservationData.createNew(rooms);
        for (Reservation reservation : reservations) {
            String confCode = String.format("CONF%04d", confirmationCounter++);
            reservation.setConfirmationNumber(confCode);
            if (reservation.getCustomer() != null) {
                reservation.getCustomer().setConfirmationNumber(confCode);
            }
            completedReservations.add(reservation);
        }
    }
    
    /* author: Ng Yung Onn */
    private void loadVipWaitingCustomers() {
        WaitingCustomer[] customers = VipWaitingCustomerData.createNew();
        for (int index = 0; index < customers.length; index++) {
            String confCode = String.format("CONF%04d", confirmationCounter++);
            customers[index].setConfirmationNumber(confCode);
            customers[index].setWaitingPosition(index + 1);
            vipController.addVip(customers[index]);

            double rate = FrontDeskControl.getDailyRate(customers[index].getRequestedRoomType());
            GuestBillingInfo guestInfo = new GuestBillingInfo(confCode, customers[index], null, rate);
            frontDeskControl.registerGuestInfo(guestInfo);
        }
    }
    
    /* author: Fan Jin Kit */
    private void loadStandardWaitingCustomers() {
        WaitingCustomer[] customers = StandardWaitingCustomerData.createNew();
        for (int index = 0; index < customers.length; index++) {
            String confCode = String.format("CONF%04d", confirmationCounter++);
            customers[index].setConfirmationNumber(confCode);
            customers[index].setWaitingPosition(index + 1);
            waitingCustomers.add(customers[index]);

            double rate = FrontDeskControl.getDailyRate(customers[index].getRequestedRoomType());
            GuestBillingInfo guestInfo = new GuestBillingInfo(confCode, customers[index], null, rate);
            frontDeskControl.registerGuestInfo(guestInfo);
        }
    }
}
