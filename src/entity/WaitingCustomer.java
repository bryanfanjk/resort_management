package entity;

/** A customer waiting for a room that satisfies the recorded requirements. */
public class WaitingCustomer extends Customer {

    private int waitingPosition;
    private final RoomType requestedRoomType;
    

    public WaitingCustomer(Customer customer, RoomType requestedRoomType,
                           int waitingPosition) {
        super(customer.getCustomerName(), customer.getPax(),
                customer.getCheckInDate(), customer.getNightsStayed(), customer.getCustomerType());
        this.requestedRoomType = requestedRoomType;
        this.waitingPosition = waitingPosition;
    }

    public int getWaitingPosition() {
        return waitingPosition;
    }

    public void setWaitingPosition(int waitingPosition) {
        this.waitingPosition = waitingPosition;
    }

    public RoomType getRequestedRoomType() {
        return requestedRoomType;
    }
}
