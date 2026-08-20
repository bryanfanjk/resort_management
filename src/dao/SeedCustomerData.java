package dao;

import entity.Customer;
import entity.CustomerType;
import entity.RoomType;

/**
 * Author: <Your Name Here>
 *
 * Hardcoded seed data used ONCE, at startup, to directly pre-populate
 * the VIP and Standard queues. This replaces the old CustomerData
 * class's role - previously "Walk In" drew from a list like this one
 * click at a time; now Walk-In takes real manual input, and this data
 * only exists to give the system something to work with (and to demo
 * against) the instant it launches.
 *
 * Same mix as before - 4 STANDARD, 4 VIP, spread across all 3 room
 * types - preserving the demo scenario already verified to work: VIP
 * priority, a Platinum room-type collision between a VIP and a
 * STANDARD guest, and a STANDARD guest who ends up blocking the rest
 * of their queue once Deluxe rooms run out.
 */
public final class SeedCustomerData {

    private SeedCustomerData() {
        // static utility class - never instantiated
    }

    public static Customer[] createSeedCustomers() {
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
