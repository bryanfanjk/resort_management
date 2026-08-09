package tarumtresort.boundary;

import tarumtresort.adt.HeapPriorityQueue;
import tarumtresort.adt.ListInterface;
import tarumtresort.control.VIPAllocationController;
import tarumtresort.entity.Booking;
import tarumtresort.entity.Guest;
import tarumtresort.entity.Room;
import tarumtresort.util.InputUtil;

/**
 * Boundary class handling VIP & Loyalty priority queues.
 * Shows binary heap tree updates and sorting lists.
 * 
 * @author Admin
 */
public class VIPAllocationUI {

    private final VIPAllocationController controller;
    private final HeapPriorityQueue<Booking> queue;

    public VIPAllocationUI(VIPAllocationController controller, HeapPriorityQueue<Booking> queue) {
        this.controller = controller;
        this.queue = queue;
    }

    public void start() {
        while (true) {
            InputUtil.displayHeader("VIP & Loyalty Priority Room Allocation");
            System.out.println("1. Register VIP & Elite Member Booking");
            System.out.println("2. Allocate Room to Highest Priority Guest (Heap Max)");
            System.out.println("3. Upgrade Member Tier & Re-heap Queue");
            System.out.println("4. View VIP Priority Queue (Tree Level-Order / Sorted)");
            System.out.println("5. Back to Main Menu");

            int choice = InputUtil.readInt("Enter choice (1-5): ", 1, 5);
            switch (choice) {
                case 1:
                    registerVIP();
                    break;
                case 2:
                    allocateVIP();
                    break;
                case 3:
                    upgradePriority();
                    break;
                case 4:
                    viewVIPQueue();
                    break;
                case 5:
                    return;
            }
        }
    }

    private void registerVIP() {
        System.out.println("\n--- Register VIP & Elite Member ---");
        String name = InputUtil.readString("Enter Member Name: ");
        String contact = InputUtil.readContactNumber("Enter Contact Number: ");
        
        System.out.println("Select Member Loyalty Tier:");
        System.out.println("1. Standard (VIP Request)");
        System.out.println("2. Silver");
        System.out.println("3. Gold");
        System.out.println("4. Platinum");
        System.out.println("5. Diamond");
        System.out.println("6. Elite");
        int tierChoice = InputUtil.readInt("Select choice (1-6): ", 1, 6);
        Guest.LoyaltyTier tier = Guest.LoyaltyTier.values()[tierChoice - 1];

        System.out.println("Select Requested Room Type:");
        System.out.println("1. Standard");
        System.out.println("2. Deluxe");
        System.out.println("3. Suite");
        System.out.println("4. Penthouse");
        int roomChoice = InputUtil.readInt("Select choice (1-4): ", 1, 4);
        Room.RoomType requestedRoomType = Room.RoomType.values()[roomChoice - 1];

        String date = InputUtil.readDate("Enter Booking Date (YYYY-MM-DD): ");

        // Create guest with points that match their tier minimum
        int startingPoints = 0;
        switch (tier) {
            case SILVER: startingPoints = 300; break;
            case GOLD: startingPoints = 1000; break;
            case PLATINUM: startingPoints = 2500; break;
            case DIAMOND: startingPoints = 5000; break;
            case ELITE: startingPoints = 10000; break;
        }
        Guest guest = new Guest(name, contact, startingPoints, tier);

        Booking booking = controller.registerVIPBooking(guest, requestedRoomType, date);
        System.out.println("\nSUCCESS: VIP Booking Registered!");
        System.out.printf("Confirmation Number: %s\n", booking.getConfirmationNumber());
        System.out.printf("Current Priority Queue Size: %d\n", queue.getSize());
        InputUtil.pressEnterToContinue();
    }

    private void allocateVIP() {
        System.out.println("\n--- Allocate VIP Room (Max-Heap Priority) ---");
        if (queue.isEmpty()) {
            System.out.println("VIP Queue is empty.");
            InputUtil.pressEnterToContinue();
            return;
        }

        Booking rootBooking = queue.peek();
        System.out.printf("Highest priority member in heap: %s (Tier: %s, Conf: %s)\n",
                rootBooking.getGuest().getName(), rootBooking.getGuest().getTier().getLabel(), rootBooking.getConfirmationNumber());

        Booking allocated = controller.allocateNextVIPRoom();
        if (allocated != null) {
            System.out.printf("SUCCESS: Allocated Room %d to VIP Guest %s!\n",
                    allocated.getAllocatedRoom().getRoomNumber(), allocated.getGuest().getName());
        } else {
            System.out.println("ALLOCATION FAILED: No vacant, clean rooms of the requested type available.");
            System.out.println("Please clean a room or adjust request.");
        }
        InputUtil.pressEnterToContinue();
    }

    private void upgradePriority() {
        System.out.println("\n--- Upgrade Queue Member Priority ---");
        if (queue.isEmpty()) {
            System.out.println("Priority Queue is empty.");
            InputUtil.pressEnterToContinue();
            return;
        }

        String confCode = InputUtil.readConfirmationNumber("Enter VIP booking confirmation number (e.g. VIP12345): ");
        
        System.out.println("Select New Loyalty Tier to Upgrade to:");
        System.out.println("1. Gold");
        System.out.println("2. Platinum");
        System.out.println("3. Diamond");
        System.out.println("4. Elite");
        int tierChoice = InputUtil.readInt("Select choice (1-4): ", 1, 4);
        Guest.LoyaltyTier newTier = null;
        switch (tierChoice) {
            case 1: newTier = Guest.LoyaltyTier.GOLD; break;
            case 2: newTier = Guest.LoyaltyTier.PLATINUM; break;
            case 3: newTier = Guest.LoyaltyTier.DIAMOND; break;
            case 4: newTier = Guest.LoyaltyTier.ELITE; break;
        }

        boolean success = controller.upgradeVIPQueuePriority(confCode, newTier);
        if (success) {
            System.out.println("SUCCESS: Member priority upgraded! The Max-Heap has automatically reorganized itself.");
        } else {
            System.out.println("ERROR: Booking not found in pending VIP priority queue.");
        }
        InputUtil.pressEnterToContinue();
    }

    private void viewVIPQueue() {
        System.out.println("\n--- VIP Priority Queue representation ---");
        if (queue.isEmpty()) {
            System.out.println("[VIP Queue is empty]");
            InputUtil.pressEnterToContinue();
            return;
        }

        System.out.println("Select Display Format:");
        System.out.println("1. Priority Sorted Order (Priority Descending)");
        System.out.println("2. Raw Heap Array Order (Level-Order Binary Tree Representation)");
        int format = InputUtil.readInt("Enter choice (1-2): ", 1, 2);

        ListInterface<Booking> list = (format == 1) ? queue.getSortedList() : queue.toList();

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-4s | %-12s | %-16s | %-12s | %-12s | %-15s\n", 
                "HeapIdx", "Conf Code", "Guest Name", "Loyalty Tier", "Room Type", "Status");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (int i = 1; i <= list.getLength(); i++) {
            Booking b = list.getEntry(i);
            System.out.printf("%-7d | %-12s | %-16s | %-12s | %-12s | %-15s\n",
                    i, b.getConfirmationNumber(), b.getGuest().getName(), b.getGuest().getTier().getLabel(), b.getRequestedRoomType().getLabel(), b.getStatus().getLabel());
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("Total VIPs in Priority Queue: %d\n", queue.getSize());
        InputUtil.pressEnterToContinue();
    }
}
