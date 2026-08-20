package control;

import adt.QueueInterface;
import adt.StandardQueue;
import boundary.WalkInUI;
import dao.CustomerData;
import dao.RoomData;
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
 * It also owns main(), matching the ECBDemo reference convention
 * adopted for this project (ProductMaintenance.main() in that project),
 * rather than a separate main-package class.
 *
 * No I/O here at all, per ECB rules for control classes - checkIn() and
 * assignRoom() return result objects; WalkInUI (boundary) is the one
 * that actually prints anything.
 */
public class WalkInController {

    private final Customer[] hardcodedCustomers;
    private int nextCustomerIndex;

    private final QueueInterface<Customer> standardQueue;
    private final VipAllocationController vipController;

    private final Room[] rooms;

    public WalkInController() {
        this.hardcodedCustomers = CustomerData.createCustomers();
        this.nextCustomerIndex = 0;
        this.standardQueue = new StandardQueue<>();
        this.vipController = new VipAllocationController();
        this.rooms = RoomData.createRooms();
    }

    /**
     * Processes exactly ONE hardcoded customer per call (mirrors
     * assignRoom()'s one-per-click pattern - confirmed design, and
     * chosen deliberately so the eventual switch to manual input later
     * doesn't require changing this interaction model at all).
     *
     * @return the Customer just processed, or null if the hardcoded
     * list has been fully processed already.
     */
    public Customer checkIn() {
        if (nextCustomerIndex >= hardcodedCustomers.length) {
            return null;
        }
        Customer customer = hardcodedCustomers[nextCustomerIndex];
        nextCustomerIndex++;

        if (customer.getCustomerType() == CustomerType.VIP) {
            vipController.registerVip(customer);
        } else {
            standardQueue.enqueue(customer);
        }
        return customer;
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

    public static void main(String[] args) {
        WalkInController controller = new WalkInController();
        WalkInUI ui = new WalkInUI(controller);
        ui.start();
    }
}
