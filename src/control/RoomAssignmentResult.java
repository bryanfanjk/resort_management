package control;

import entity.Customer;
import entity.Room;

/**
 * Author: <Your Name Here>
 *
 * Immutable result of a single WalkInController.assignRoom() attempt.
 * Used instead of an exception (matching the adopted no-exception,
 * ECBDemo-style convention) so the boundary can print the right message
 * for each of the three possible outcomes without any try/catch.
 */
public class RoomAssignmentResult {

    public enum Status {
        SUCCESS,
        NO_ROOM_AVAILABLE,
        NO_CUSTOMERS_WAITING
    }

    private final Status status;
    private final Customer customer;
    private final Room room;

    private RoomAssignmentResult(Status status, Customer customer, Room room) {
        this.status = status;
        this.customer = customer;
        this.room = room;
    }

    public static RoomAssignmentResult success(Customer customer, Room room) {
        return new RoomAssignmentResult(Status.SUCCESS, customer, room);
    }

    public static RoomAssignmentResult noRoomAvailable(Customer customer) {
        return new RoomAssignmentResult(Status.NO_ROOM_AVAILABLE, customer, null);
    }

    public static RoomAssignmentResult noCustomersWaiting() {
        return new RoomAssignmentResult(Status.NO_CUSTOMERS_WAITING, null, null);
    }

    public Status getStatus() {
        return status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Room getRoom() {
        return room;
    }
}
