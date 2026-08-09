package tarumtresort;

import tarumtresort.adt.ArrayList;
import tarumtresort.adt.BinarySearchTree;
import tarumtresort.adt.HeapPriorityQueue;
import tarumtresort.adt.LinkedQueue;
import tarumtresort.adt.LinkedStack;
import tarumtresort.adt.ListInterface;
import tarumtresort.boundary.BookingUI;
import tarumtresort.boundary.FrontDeskUI;
import tarumtresort.boundary.HousekeepingUI;
import tarumtresort.boundary.LoyaltyUI;
import tarumtresort.boundary.MainMenuUI;
import tarumtresort.boundary.ReportUI;
import tarumtresort.boundary.VIPAllocationUI;
import tarumtresort.control.BookingController;
import tarumtresort.control.FrontDeskController;
import tarumtresort.control.HousekeepingController;
import tarumtresort.control.LoyaltyController;
import tarumtresort.control.ReportController;
import tarumtresort.control.VIPAllocationController;
import tarumtresort.entity.Booking;
import tarumtresort.entity.Guest;
import tarumtresort.entity.HousekeepingLog;
import tarumtresort.entity.Room;

/**
 * Main application entry point for TARUMT Resorts reservation system.
 * Initializes all custom collections and seeds initial mockup records.
 * Instantiates the boundaries and controls using the ECB pattern.
 * 
 * @author Admin
 */
public class Main {

    // Central Data Collections (Global within Application Instance)
    private static final ListInterface<Room> roomList = new ArrayList<>();
    private static final ListInterface<Guest> guestList = new ArrayList<>();
    private static final LinkedQueue<Booking> standardBookingQueue = new LinkedQueue<>();
    private static final HeapPriorityQueue<Booking> vipBookingQueue = new HeapPriorityQueue<>();
    private static final LinkedStack<HousekeepingLog> housekeepingHistoryStack = new LinkedStack<>();
    private static final BinarySearchTree<String, Booking> bookingLookupTree = new BinarySearchTree<>();
    private static final BinarySearchTree<String, Guest> guestLookupTree = new BinarySearchTree<>();

    public static void main(String[] args) {
        // 1. Seed initial data
        seedDatabase();

        // 2. Initialize Controllers
        BookingController bookingController = new BookingController(standardBookingQueue, bookingLookupTree, roomList);
        VIPAllocationController vipController = new VIPAllocationController(vipBookingQueue, bookingLookupTree, roomList);
        HousekeepingController housekeepingController = new HousekeepingController(housekeepingHistoryStack, roomList);
        FrontDeskController frontDeskController = new FrontDeskController(bookingLookupTree);
        LoyaltyController loyaltyController = new LoyaltyController(guestList, guestLookupTree);
        ReportController reportController = new ReportController(roomList, guestList, bookingLookupTree, housekeepingHistoryStack);

        // 3. Initialize Boundaries
        BookingUI bookingUI = new BookingUI(bookingController, standardBookingQueue);
        VIPAllocationUI vipUI = new VIPAllocationUI(vipController, vipBookingQueue);
        HousekeepingUI housekeepingUI = new HousekeepingUI(housekeepingController, housekeepingHistoryStack, roomList);
        FrontDeskUI frontDeskUI = new FrontDeskUI(frontDeskController);
        LoyaltyUI loyaltyUI = new LoyaltyUI(loyaltyController);
        ReportUI reportUI = new ReportUI(reportController);

        // 4. Start Router Menu
        MainMenuUI mainMenu = new MainMenuUI(bookingUI, vipUI, housekeepingUI, frontDeskUI, loyaltyUI, reportUI);
        mainMenu.startMenuLoop();
    }

    private static void seedDatabase() {
        // --- Populate Rooms ---
        // Floor 1: Standard Rooms ($150)
        roomList.add(new Room(101, Room.RoomType.STANDARD));
        roomList.add(new Room(102, Room.RoomType.STANDARD));
        roomList.add(new Room(103, Room.RoomType.STANDARD));
        roomList.add(new Room(104, Room.RoomType.STANDARD));
        roomList.add(new Room(105, Room.RoomType.STANDARD));

        // Floor 2: Deluxe Rooms ($280)
        roomList.add(new Room(201, Room.RoomType.DELUXE));
        roomList.add(new Room(202, Room.RoomType.DELUXE));
        roomList.add(new Room(203, Room.RoomType.DELUXE));
        roomList.add(new Room(204, Room.RoomType.DELUXE));
        roomList.add(new Room(205, Room.RoomType.DELUXE));

        // Floor 3: Suites ($500)
        roomList.add(new Room(301, Room.RoomType.SUITE));
        roomList.add(new Room(302, Room.RoomType.SUITE));
        roomList.add(new Room(303, Room.RoomType.SUITE));
        roomList.add(new Room(304, Room.RoomType.SUITE));
        roomList.add(new Room(305, Room.RoomType.SUITE));

        // Floor 4: Penthouses ($1200)
        roomList.add(new Room(401, Room.RoomType.PENTHOUSE));
        roomList.add(new Room(402, Room.RoomType.PENTHOUSE));
        roomList.add(new Room(403, Room.RoomType.PENTHOUSE));
        roomList.add(new Room(404, Room.RoomType.PENTHOUSE));
        roomList.add(new Room(405, Room.RoomType.PENTHOUSE));

        // Mark some rooms dirty for initial cleaning practice
        roomList.getEntry(2).setStatus(Room.HousekeepingStatus.DIRTY);
        roomList.getEntry(7).setStatus(Room.HousekeepingStatus.DIRTY);
        roomList.getEntry(12).setStatus(Room.HousekeepingStatus.DIRTY);
        roomList.getEntry(17).setStatus(Room.HousekeepingStatus.DIRTY);

        // --- Populate Guests / Loyalty Members ---
        Guest g1 = new Guest("Alice Tan", "0123456789", 6000, Guest.LoyaltyTier.DIAMOND);
        Guest g2 = new Guest("Bob Lim", "0112233445", 400, Guest.LoyaltyTier.SILVER);
        Guest g3 = new Guest("Charlie Ng", "0178899221", 1200, Guest.LoyaltyTier.GOLD);
        Guest g4 = new Guest("David Loo", "0199988776", 12000, Guest.LoyaltyTier.ELITE);
        Guest g5 = new Guest("Eva Green", "0185566778", 50, Guest.LoyaltyTier.STANDARD);
        Guest g6 = new Guest("Frank Wright", "0133445566", 3200, Guest.LoyaltyTier.PLATINUM);

        guestList.add(g1);
        guestList.add(g2);
        guestList.add(g3);
        guestList.add(g4);
        guestList.add(g5);
        guestList.add(g6);

        guestLookupTree.insert(g1.getContactNumber(), g1);
        guestLookupTree.insert(g2.getContactNumber(), g2);
        guestLookupTree.insert(g3.getContactNumber(), g3);
        guestLookupTree.insert(g4.getContactNumber(), g4);
        guestLookupTree.insert(g5.getContactNumber(), g5);
        guestLookupTree.insert(g6.getContactNumber(), g6);

        // --- Seed Housekeeping Stack Logs ---
        housekeepingHistoryStack.push(new HousekeepingLog(102, Room.HousekeepingStatus.DIRTY, Room.HousekeepingStatus.CLEANING_IN_PROGRESS, "Muthu", "08:15:00"));
        housekeepingHistoryStack.push(new HousekeepingLog(102, Room.HousekeepingStatus.CLEANING_IN_PROGRESS, Room.HousekeepingStatus.INSPECTED, "Siti", "09:00:00"));
        housekeepingHistoryStack.push(new HousekeepingLog(202, Room.HousekeepingStatus.DIRTY, Room.HousekeepingStatus.CLEANING_IN_PROGRESS, "John", "10:30:00"));

        // --- Seed Standard Bookings (Chronological Queue) ---
        Booking sb1 = new Booking("50284711", g2, Room.RoomType.STANDARD, false, 1, "2026-07-04");
        Booking sb2 = new Booking("50284712", g3, Room.RoomType.DELUXE, false, 2, "2026-07-04");
        Booking sb3 = new Booking("50284713", g5, Room.RoomType.SUITE, false, 3, "2026-07-04");

        standardBookingQueue.enqueue(sb1);
        standardBookingQueue.enqueue(sb2);
        standardBookingQueue.enqueue(sb3);

        bookingLookupTree.insert(sb1.getConfirmationNumber(), sb1);
        bookingLookupTree.insert(sb2.getConfirmationNumber(), sb2);
        bookingLookupTree.insert(sb3.getConfirmationNumber(), sb3);

        // --- Seed VIP Bookings (Heap Priority Queue) ---
        // Tier Order: David (ELITE, rank 5) > Alice (DIAMOND, rank 4) > Frank (PLATINUM, rank 3)
        Booking vb1 = new Booking("VIP00001", g1, Room.RoomType.SUITE, true, 10, "2026-07-04");
        Booking vb2 = new Booking("VIP00002", g4, Room.RoomType.PENTHOUSE, true, 11, "2026-07-04");
        Booking vb3 = new Booking("VIP00003", g6, Room.RoomType.DELUXE, true, 12, "2026-07-04");

        vipBookingQueue.enqueue(vb1);
        vipBookingQueue.enqueue(vb2);
        vipBookingQueue.enqueue(vb3);

        bookingLookupTree.insert(vb1.getConfirmationNumber(), vb1);
        bookingLookupTree.insert(vb2.getConfirmationNumber(), vb2);
        bookingLookupTree.insert(vb3.getConfirmationNumber(), vb3);
        
        // --- Seed Allocated/Active Bookings ---
        Room r301 = roomList.getEntry(11); // Suite 301
        Booking activeBooking = new Booking("22947581", g4, Room.RoomType.SUITE, true, 5, "2026-07-03");
        r301.setCurrentGuestConfirmation(activeBooking.getConfirmationNumber());
        activeBooking.setAllocatedRoom(r301);
        activeBooking.setStatus(Booking.BookingStatus.CHECKED_IN);
        bookingLookupTree.insert(activeBooking.getConfirmationNumber(), activeBooking);
    }
}
