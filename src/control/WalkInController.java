package control;

import adt.QueueInterface;
import adt.StandardQueue;
import boundary.WalkInUI;
import dao.RoomData;
import dao.SeedCustomerData;
import entity.Customer;
import entity.CustomerType;
import entity.Room;
import entity.RoomType;

/**
 * Author: <Your Name Here>
 *
 * WalkInController is Module 1's control class - the integration point
 * that drives both modules. It owns the standard queue directly, and
 * reaches into Module 2's VIP queue only through VipAllocationController
 * (never touching VipQueue itself), per the confirmed module split.
 *
 * At construction, both queues are pre-populated directly from hardcoded
 * seed data (SeedCustomerData) - this replaces the old design where
 * Walk-In drew from that data one click at a time. From here on,
 * checkIn() takes real manually-collected input.
 *
 * It also owns main(), matching the ECBDemo reference convention
 * adopted for this project.
 *
 * No I/O here at all, per ECB rules for control classes - checkIn() and
 * assignRoom() return result objects; WalkInUI (boundary) is the one
 * that actually prints anything or reads Scanner input.
 */
public class WalkInController {

    private final QueueInterface<Customer> standardQueue;
    private final VipAllocationController vipController;
    private final Room[] rooms;

    private int nextIdNumber;

    public WalkInController() {
        this.standardQueue = new StandardQueue<>();
        this.vipController = new VipAllocationController();
        this.rooms = RoomData.createRooms();

        Customer[] seedCustomers = SeedCustomerData.createSeedCustomers();
        for (Customer seedCustomer : seedCustomers) {
            if (seedCustomer.getCustomerType() == CustomerType.VIP) {
                vipController.registerVip(seedCustomer);
            } else {
                standardQueue.enqueue(seedCustomer);
            }
        }
        this.nextIdNumber = seedCustomers.length + 1;
    }

    /**
     * Manual check-in. Takes input already collected by the boundary
     * (per ECB rules, this class does no Scanner reading itself).
     *
     * VIP-code logic (confirmed design):
     *   - blank/empty code            -> STANDARD_NO_CODE (Standard queue)
     *   - code entered, valid         -> VIP_REGISTERED (VIP queue)
     *   - code entered, doesn't match -> STANDARD_INVALID_CODE (Standard queue -
     *     confirmed fallback, not an abort)
     *
     * @param name              customer's name as typed by staff
     * @param requestedRoomType room type the customer wants
     * @param vipCodeInput      raw code input - may be null or blank
     */
    public CheckInResult checkIn(String name, RoomType requestedRoomType, String vipCodeInput) {
        String customerId = generateNextCustomerId();
        String trimmedCode = (vipCodeInput == null) ? "" : vipCodeInput.trim();

        if (trimmedCode.isEmpty()) {
            Customer customer = new Customer(customerId, name, CustomerType.STANDARD, requestedRoomType);
            standardQueue.enqueue(customer);
            return CheckInResult.standardNoCode(customer);
        }

        if (vipController.isValidVipCode(trimmedCode)) {
            Customer customer = new Customer(customerId, name, CustomerType.VIP, requestedRoomType);
            vipController.registerVip(customer);
            return CheckInResult.vipRegistered(customer);
        }

        Customer customer = new Customer(customerId, name, CustomerType.STANDARD, requestedRoomType);
        standardQueue.enqueue(customer);
        return CheckInResult.standardInvalidCode(customer);
    }

    /**
     * Attempts to assign a room to exactly one waiting customer.
     * VIP queue is checked first (if non-empty); the Standard queue is
     * only checked once the VIP queue is empty.
     *
     * IMPORTANT ordering: the front customer is PEEKED first, and only
     * actually dequeued once a matching room has been confirmed
     * available. Dequeuing first would lose the customer from the
     * queue entirely if no matching room existed.
     */
    public RoomAssignmentResult assignRoom() {
        Customer target;
        boolean fromVipQueue;

        if (vipController.hasWaitingVip()) {
            target = vipController.peekNextVip();
            fromVipQueue = true;
        } else if (!standardQueue.isEmpty()) {
            target = standardQueue.peekFront();
            fromVipQueue = false;
        } else {
            return RoomAssignmentResult.noCustomersWaiting();
        }

        Room matchedRoom = findAvailableRoom(target.getRequestedRoomType());
        if (matchedRoom == null) {
            // Deliberate simplification: the front customer blocks the
            // rest of their queue for their room type - no skip-ahead
            // to a later customer who might be servable right now.
            return RoomAssignmentResult.noRoomAvailable(target);
        }

        // Only now do we actually remove the customer from their queue.
        if (fromVipQueue) {
            vipController.getNextVip();
        } else {
            standardQueue.dequeue();
        }
        matchedRoom.setAvailable(false);

        return RoomAssignmentResult.success(target, matchedRoom);
    }

    private Room findAvailableRoom(RoomType requestedType) {
        for (Room room : rooms) {
            if (room.isAvailable() && room.getRoomType() == requestedType) {
                return room;
            }
        }
        return null;
    }

    private String generateNextCustomerId() {
        String id = String.format("C%03d", nextIdNumber);
        nextIdNumber++;
        return id;
    }

    public static void main(String[] args) {
        WalkInController controller = new WalkInController();
        WalkInUI ui = new WalkInUI(controller);
        ui.start();
    }
}
