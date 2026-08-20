package control;

import adt.StandardList;
import adt.VipList;
import boundary.WalkInUI;
import dao.RoomData;
import dao.SeedCustomerData;
import entity.Customer;
import entity.CustomerType;
import entity.Room;
import entity.RoomType;

public class WalkInController {

    private final StandardList<Customer> standardList;
    private final VipAllocationController vipController;
    private final Room[] rooms;

    private int nextIdNumber;

    public WalkInController() {
        this.standardList = new StandardList<>();
        this.vipController = new VipAllocationController();
        this.rooms = RoomData.createRooms();

        Customer[] seedCustomers = SeedCustomerData.createSeedCustomers();

        for (Customer customer : seedCustomers) {
            if (customer.getCustomerType() == CustomerType.VIP) {
                vipController.registerVip(customer);
            } else {
                standardList.add(customer);
            }
        }

        this.nextIdNumber = seedCustomers.length + 1;
    }

    public CheckInResult checkIn(String name, RoomType requestedRoomType, String vipCodeInput) {
        String customerId = generateNextCustomerId();
        String trimmedCode = (vipCodeInput == null) ? "" : vipCodeInput.trim();

        if (trimmedCode.isEmpty()) {
            Customer customer = new Customer(customerId, name, CustomerType.STANDARD, requestedRoomType);
            standardList.add(customer);
            return CheckInResult.standardNoCode(customer);
        }

        if (vipController.isValidVipCode(trimmedCode)) {
            Customer customer = new Customer(customerId, name, CustomerType.VIP, requestedRoomType);
            vipController.registerVip(customer);
            return CheckInResult.vipRegistered(customer);
        }

        Customer customer = new Customer(customerId, name, CustomerType.STANDARD, requestedRoomType);
        standardList.add(customer);
        return CheckInResult.standardInvalidCode(customer);
    }

    public RoomAssignmentResult assignRoom() {
        // 1. VIP list takes priority
        if (vipController.hasWaitingVip()) {
            VipList<Customer> vipList = vipController.getVipList();

            for (int i = 0; i < vipList.size(); i++) {
                Customer customer = vipList.get(i);
                Room room = findAvailableRoom(customer.getRequestedRoomType());

                if (room != null) {
                    vipList.remove(i);
                    room.setAvailable(false);
                    return RoomAssignmentResult.success(customer, room);
                }
            }
        }

        // 2. If no VIP customer can be assigned, try Standard list
        if (!standardList.isEmpty()) {
            for (int i = 0; i < standardList.size(); i++) {
                Customer customer = standardList.get(i);
                Room room = findAvailableRoom(customer.getRequestedRoomType());

                if (room != null) {
                    standardList.remove(i);
                    room.setAvailable(false);
                    return RoomAssignmentResult.success(customer, room);
                }
            }
        }

        // 3. Only fail if there are waiting customers but all rooms are unavailable
        if (vipController.hasWaitingVip()) {
            return RoomAssignmentResult.noRoomAvailable(vipController.peekNextVip());
        }

        if (!standardList.isEmpty()) {
            return RoomAssignmentResult.noRoomAvailable(standardList.get(0));
        }

        return RoomAssignmentResult.noCustomersWaiting();
    }

    public int getStandardListSize() {
        return standardList.size();
    }

    public int getVipListSize() {
        return vipController.waitingVipCount();
    }

    public StandardList<Customer> getStandardList() {
        return standardList;
    }

    public VipList<Customer> getVipList() {
        return vipController.getVipList();
    }

    public Customer peekNextStandardCustomer() {
        if (standardList.isEmpty()) {
            return null;
        }
        return standardList.get(0);
    }

    public Customer peekNextVipCustomer() {
        return vipController.peekNextVip();
    }

    public String getWaitingListSummary() {
        return "VIP waiting: " + getVipListSize()
                + " | Standard waiting: " + getStandardListSize();
    }

    private Room findAvailableRoom(RoomType requestedType) {
        for (Room room : rooms) {
            if (room.isAvailable() && room.getRoomType() == requestedType) {
                return room;
            }
        }
        return null;
    }

    private String generateNextCustomerId() {
        String id = String.format("C%03d", nextIdNumber);
        nextIdNumber++;
        return id;
    }

    public static void main(String[] args) {
        WalkInController controller = new WalkInController();
        WalkInUI ui = new WalkInUI(controller);
        ui.start();
    }
}