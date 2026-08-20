package dao;

import entity.Room;
import entity.RoomType;

/**
 * Author: <Your Name Here>
 *
 * Hardcoded room data. No file or database used - this project made
 * that a deliberate design choice, not something the assignment
 * requires (the assignment explicitly permits either hardcoding or
 * file-based population). Two rooms are pre-marked unavailable so the
 * "no matching room" path in WalkInController.assignRoom() is
 * exercisable from the very first run, not only once rooms run out
 * during a demo.
 */
public final class RoomData {

    private RoomData() {
        // static utility class - never instantiated
    }

    public static Room[] createRooms() {
        return new Room[]{
            new Room(101, 1, RoomType.DELUXE, true),
            new Room(102, 1, RoomType.DELUXE, false),   // pre-occupied
            new Room(103, 1, RoomType.DELUXE, true),
            new Room(201, 2, RoomType.PREMIUM, true),
            new Room(202, 2, RoomType.PREMIUM, true),
            new Room(301, 3, RoomType.PLATINUM, true),
            new Room(302, 3, RoomType.PLATINUM, false)  // pre-occupied
        };
    }
}
