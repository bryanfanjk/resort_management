package dao;

import entity.Room;
import entity.RoomType;

/** Hardcoded room data*/
public final class RoomData {

    private RoomData() {
    }

    public static Room[] createRooms() {
        return new Room[]{
            new Room(101, 1, RoomType.DELUXE),
            new Room(102, 1, RoomType.DELUXE),
            new Room(103, 2, RoomType.DELUXE),
            new Room(104, 2, RoomType.DELUXE),
            new Room(201, 2, RoomType.PREMIUM),
            new Room(202, 2, RoomType.PREMIUM),
            new Room(203, 3, RoomType.PREMIUM),
            new Room(204, 3, RoomType.PREMIUM),
            new Room(301, 3, RoomType.PLATINUM),
            new Room(302, 3, RoomType.PLATINUM),
            new Room(303, 4, RoomType.PLATINUM),
            new Room(304, 4, RoomType.PLATINUM)
        };
    }
}
