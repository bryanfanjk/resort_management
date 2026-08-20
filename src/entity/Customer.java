package entity;

/**
 * Author: <Your Name Here>
 *
 * Customer is a plain data object (POJO) - no Scanner input, no
 * System.out output, per ECB rules for entity classes.
 *
 * Note: no VipTier field here yet - that's a Phase B addition, and per
 * the open fork discussed, whether it becomes a nullable field on this
 * class or a separate VIP-specific subtype is still to be decided when
 * Phase B actually starts.
 */
public class Customer {

    private final String customerId;
    private String name;
    private CustomerType customerType;
    private RoomType requestedRoomType;

    public Customer(String customerId, String name, CustomerType customerType, RoomType requestedRoomType) {
        this.customerId = customerId;
        this.name = name;
        this.customerType = customerType;
        this.requestedRoomType = requestedRoomType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public RoomType getRequestedRoomType() {
        return requestedRoomType;
    }

    public void setRequestedRoomType(RoomType requestedRoomType) {
        this.requestedRoomType = requestedRoomType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) obj;
        return this.customerId.equals(other.customerId);
    }

    @Override
    public int hashCode() {
        return customerId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("[%s] %-16s | %-8s | Requested: %s",
                customerId, name, customerType, requestedRoomType);
    }
}
