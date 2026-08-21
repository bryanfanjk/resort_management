package dao;

import entity.Customer;
import entity.CustomerType;
import entity.RoomType;
import entity.WaitingCustomer;

public final class VipWaitingCustomerData {

    private VipWaitingCustomerData() {
    }

    public static WaitingCustomer[] createNew() {
        return new WaitingCustomer[]{
            createWaitingCustomer("Victor Lim", 2, "14/08/2026", 3,
                    RoomType.PREMIUM),
            createWaitingCustomer("Sophia Tan", 4, "15/08/2026", 2,
                    RoomType.PLATINUM),
            createWaitingCustomer("Marcus Wong", 1, "16/08/2026", 1,
                    RoomType.DELUXE)
        };
    }

    private static WaitingCustomer createWaitingCustomer(
            String name, int pax, String checkInDate, int nightsStayed,
            RoomType roomType) {

        Customer customer = new Customer(name, pax, checkInDate,
                nightsStayed, CustomerType.VIP);
        return new WaitingCustomer(customer, roomType, 0);
    }
}
