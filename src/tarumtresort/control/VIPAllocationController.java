package tarumtresort.control;

import tarumtresort.adt.BSTInterface;
import tarumtresort.adt.HeapPriorityQueue;
import tarumtresort.adt.ListInterface;
import tarumtresort.entity.Booking;
import tarumtresort.entity.Guest;
import tarumtresort.entity.Room;

/**
 * Control class managing priority room allocations for VIPs and elite loyalty members.
 * Utilizes a binary max-heap Priority Queue and provides priority modifications.
 * 
 * @author Admin
 */
public class VIPAllocationController {

    private final HeapPriorityQueue<Booking> vipQueue;
    private final BSTInterface<String, Booking> bookingTree;
    private final ListInterface<Room> rooms;
    private static long bookingCounter = 5000; // Separate VIP sequence counter

    public VIPAllocationController(HeapPriorityQueue<Booking> vipQueue, 
                                   BSTInterface<String, Booking> bookingTree, 
                                   ListInterface<Room> rooms) {
        this.vipQueue = vipQueue;
        this.bookingTree = bookingTree;
        this.rooms = rooms;
    }

    /**
     * Registers an elite guest into the VIP allocation queue.
     * 
     * @param guest Elite guest profile.
     * @param requestedType Requested Room type.
     * @param bookingDate Date of booking.
     * @return The VIP booking.
     */
    public Booking registerVIPBooking(Guest guest, Room.RoomType requestedType, String bookingDate) {
        String confirmationNumber = generateConfirmationNumber();
        bookingCounter++;
        Booking booking = new Booking(confirmationNumber, guest, requestedType, true, bookingCounter, bookingDate);
        
        vipQueue.enqueue(booking);
        bookingTree.insert(confirmationNumber, booking);
        
        return booking;
    }

    /**
     * Allocates the highest priority pending VIP guest to a vacant room.
     * 
     * @return The allocated booking, or null if queue is empty or no room is available.
     */
    public Booking allocateNextVIPRoom() {
        if (vipQueue.isEmpty()) {
            return null;
        }

        // Peek highest priority booking
        Booking booking = vipQueue.peek();
        Room vacantRoom = findVacantRoom(booking.getRequestedRoomType());

        if (vacantRoom != null) {
            // Dequeue and allocate
            vipQueue.dequeue();
            vacantRoom.setCurrentGuestConfirmation(booking.getConfirmationNumber());
            booking.setAllocatedRoom(vacantRoom);
            return booking;
        }

        return null; // Room not available (remains in queue)
    }

    /**
     * Non-trivial original control operation: Upgrades a guest's loyalty tier mid-queue
     * and forces the HeapPriorityQueue to reorganize.
     * 
     * @param confirmationNumber 8-digit confirmation code.
     * @param newTier The newly achieved Loyalty Tier.
     * @return true if successful, false if booking not found or not in pending queue.
     */
    public boolean upgradeVIPQueuePriority(String confirmationNumber, Guest.LoyaltyTier newTier) {
        Booking booking = bookingTree.search(confirmationNumber);
        if (booking != null && booking.getStatus() == Booking.BookingStatus.PENDING && booking.isVIP()) {
            // Create a cloned booking with updated tier for comparison
            Booking oldBooking = new Booking(booking.getConfirmationNumber(), booking.getGuest(), 
                    booking.getRequestedRoomType(), booking.isVIP(), booking.getBookingIndex(), booking.getBookingDate());
            
            // Apply new tier
            booking.getGuest().setTier(newTier);
            
            // Re-heap using the non-trivial ADT changePriority method
            return vipQueue.changePriority(oldBooking, booking);
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
        long num = (System.currentTimeMillis() % 1000000L) * 100 + (bookingCounter % 100);
        return "VIP" + String.format("%05d", Math.abs(num) % 100000);
    }
}
