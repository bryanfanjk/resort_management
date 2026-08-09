package tarumtresort.control;

import tarumtresort.adt.BSTInterface;
import tarumtresort.adt.ListInterface;
import tarumtresort.adt.LinkedQueue;
import tarumtresort.entity.Booking;
import tarumtresort.entity.Guest;
import tarumtresort.entity.Room;

/**
 * Control class managing standard walk-in registrations and FIFO room allocations.
 * Adheres to the Entity-Control-Boundary (ECB) design principles.
 * 
 * @author Admin
 */
public class BookingController {

    private final LinkedQueue<Booking> standardQueue;
    private final BSTInterface<String, Booking> bookingTree;
    private final ListInterface<Room> rooms;
    private static long bookingCounter = 1000; // Sequence counter for bookingIndex

    public BookingController(LinkedQueue<Booking> standardQueue, 
                             BSTInterface<String, Booking> bookingTree, 
                             ListInterface<Room> rooms) {
        this.standardQueue = standardQueue;
        this.bookingTree = bookingTree;
        this.rooms = rooms;
    }

    /**
     * Registers a new standard guest booking and places it in the FIFO queue.
     * 
     * @param guest The guest entity registering.
     * @param requestedType The requested room type.
     * @param bookingDate The date of reservation.
     * @return The created Booking entity.
     */
    public Booking registerStandardBooking(Guest guest, Room.RoomType requestedType, String bookingDate) {
        String confirmationNumber = generateConfirmationNumber();
        bookingCounter++;
        Booking booking = new Booking(confirmationNumber, guest, requestedType, false, bookingCounter, bookingDate);
        
        standardQueue.enqueue(booking);
        bookingTree.insert(confirmationNumber, booking);
        
        return booking;
    }

    /**
     * Allocates the next standard booking in the queue to a vacant room of requested type.
     * 
     * @return The booking allocated, or null if queue is empty or no room is available.
     */
    public Booking allocateNextStandardRoom() {
        if (standardQueue.isEmpty()) {
            return null;
        }

        // Peek first to check if room is available before dequeuing
        Booking booking = standardQueue.getFront();
        Room vacantRoom = findVacantRoom(booking.getRequestedRoomType());

        if (vacantRoom != null) {
            // Dequeue and assign
            standardQueue.dequeue();
            vacantRoom.setCurrentGuestConfirmation(booking.getConfirmationNumber());
            booking.setAllocatedRoom(vacantRoom);
            return booking;
        }

        return null; // Room not available (remains in queue)
    }

    /**
     * Non-trivial operation: Manually expedites a standard reservation to the front of the queue.
     * Invokes the original `moveToFront` ADT method.
     * 
     * @param confirmationNumber 8-digit confirmation code.
     * @return true if found and expedited, false otherwise.
     */
    public boolean expediteBooking(String confirmationNumber) {
        Booking booking = bookingTree.search(confirmationNumber);
        if (booking != null && booking.getStatus() == Booking.BookingStatus.PENDING && !booking.isVIP()) {
            return standardQueue.moveToFront(booking);
        }
        return false;
    }

    private Room findVacantRoom(Room.RoomType type) {
        for (int i = 1; i <= rooms.getLength(); i++) {
            Room r = rooms.getEntry(i);
            if (r.getRoomType() == type && r.isVacant() && r.getStatus() == Room.HousekeepingStatus.READY) {
                return r;
            }
        }
        return null;
    }

    private String generateConfirmationNumber() {
        // Generates an 8-digit confirmation number based on system clock and counter
        long num = (System.currentTimeMillis() % 1000000L) * 100 + (bookingCounter % 100);
        return String.format("%08d", Math.abs(num));
    }
}
