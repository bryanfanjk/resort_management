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
            new Room(105, 3, RoomType.DELUXE),
            new Room(106, 3, RoomType.DELUXE),
            new Room(107, 4, RoomType.DELUXE),
            new Room(108, 4, RoomType.DELUXE),
            new Room(201, 1, RoomType.PREMIUM),
            new Room(202, 1, RoomType.PREMIUM),
            new Room(203, 2, RoomType.PREMIUM),
            new Room(204, 2, RoomType.PREMIUM),
            new Room(205, 3, RoomType.PREMIUM),
            new Room(206, 3, RoomType.PREMIUM),
            new Room(207, 4, RoomType.PREMIUM),
            new Room(208, 4, RoomType.PREMIUM),
            new Room(301, 1, RoomType.PLATINUM),
            new Room(302, 1, RoomType.PLATINUM),
            new Room(303, 2, RoomType.PLATINUM),
            new Room(304, 2, RoomType.PLATINUM),
            new Room(305, 3, RoomType.PLATINUM),
            new Room(306, 3, RoomType.PLATINUM),
            new Room(307, 4, RoomType.PLATINUM),
            new Room(308, 4, RoomType.PLATINUM),
               
        };
    }
}
