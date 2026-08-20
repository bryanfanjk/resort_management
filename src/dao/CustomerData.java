package dao;

import entity.Customer;
import entity.CustomerType;
import entity.RoomType;

/**
 * Author: <Your Name Here>
 *
 * Hardcoded customer data (walk-ins), no file/database. Deliberately
 * mixed - 4 STANDARD, 4 VIP, spread across all 3 room types - so that
 * running through the full menu produces a meaningful demo: VIP
 * priority, a room-type collision between a VIP and a STANDARD guest
 * both wanting Platinum, and at least one STANDARD guest who never
 * gets served because the Deluxe rooms run out ahead of them in queue
 * (the deliberate no-skip-ahead simplification documented in the
 * implementation plan).
 */
public final class CustomerData {

    private CustomerData() {
        // static utility class - never instantiated
    }

    public static Customer[] createCustomers() {
        return new Customer[]{
            new Customer("C001", "Ahmad Fauzi", CustomerType.STANDARD, RoomType.DELUXE),
            new Customer("C002", "Grace Lim", CustomerType.VIP, RoomType.PLATINUM),
            new Customer("C003", "Ravi Kumar", CustomerType.STANDARD, RoomType.PREMIUM),
            new Customer("C004", "Michelle Tan", CustomerType.VIP, RoomType.DELUXE),
            new Customer("C005", "Farid Rahman", CustomerType.STANDARD, RoomType.PLATINUM),
            new Customer("C006", "Priya Devi", CustomerType.VIP, RoomType.PREMIUM),
            new Customer("C007", "Wong Kah Weng", CustomerType.STANDARD, RoomType.DELUXE),
            new Customer("C008", "Aisha Zainal", CustomerType.VIP, RoomType.DELUXE)
        };
    }
}
