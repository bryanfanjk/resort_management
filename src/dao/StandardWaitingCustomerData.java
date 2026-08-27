package dao;

import entity.Customer;
import entity.CustomerType;
import entity.RoomType;
import entity.WaitingCustomer;

public final class StandardWaitingCustomerData {

    private StandardWaitingCustomerData() {
    }

    public static WaitingCustomer[] createNew() {
        return new WaitingCustomer[]{
            createWaitingCustomer("Daniel Kumar", 7, "14/08/2026", 2,
                    RoomType.DELUXE),
            createWaitingCustomer("Evelyn Tan", 3, "14/08/2026", 3,
                    RoomType.PREMIUM),
            createWaitingCustomer("Farid Ismail", 1, "15/08/2026", 1,
                    RoomType.DELUXE),
            createWaitingCustomer("Fas Isma", 5, "16/08/2026", 1,
                    RoomType.DELUXE),
            createWaitingCustomer("Farid Ismail", 2, "15/08/2026", 1,
                    RoomType.DELUXE),
            createWaitingCustomer("Farid Ismail", 3, "19/08/2026", 5,
                    RoomType.PREMIUM)
        };
    }

    private static WaitingCustomer createWaitingCustomer(
            String name, int pax, String checkInDate, int nightsStayed,
            RoomType roomType) {

        Customer customer = new Customer(name, pax, checkInDate,
                nightsStayed, CustomerType.STANDARD);
        return new WaitingCustomer(customer, roomType, 0);
    }
}
