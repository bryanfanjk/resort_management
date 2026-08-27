package boundary;

import adt.LinkedStack;
import adt.List;
import adt.ListInterface;
import control.AuthController;
import control.HousekeepingController;
import entity.HousekeepingLog;
import entity.Room;
import control.HotelController;
import util.InputUtil;

public class MainMenu {

    private final HotelController hotelController = new HotelController();
    private final LinkedStack<HousekeepingLog> housekeepingStack = new LinkedStack<>();
    private final AuthController authController = new AuthController();
    private final HousekeepingController housekeepingController;
    private final HotelCheckInUI checkInUI;
    private final HousekeepingUI housekeepingUI;
    private final ReportsUI vipAllocationUI =
        new ReportsUI(hotelController);

    public MainMenu() {
        Room[] roomsArray = hotelController.getRooms();
        ListInterface<Room> roomsList = new List<>(roomsArray.length);
        for (Room r : roomsArray) {
            roomsList.add(r);
        }
        this.housekeepingController = new HousekeepingController(housekeepingStack,
                roomsList,
                authController);
        this.checkInUI = new HotelCheckInUI(hotelController);
        this.housekeepingUI = new HousekeepingUI(housekeepingController,
                housekeepingStack,
                roomsList,
                authController,
                hotelController);
    }

    public void menu() {
        int choice;
        do {
            System.out.println("\nTARUMT  Resorts, a luxury hospitality chain.");
            System.out.println("==============================================");
            System.out.println("1. Manage Walk-In Reservations for Standard and VIP Guests");
            System.out.println("2. View Reports (Standard and VIP Guests)");
            System.out.println("3. Housekeeping and Task Log");
            System.out.println("4. Front-Desk Service & Billing");
            System.out.println("0. Exit");
            choice = InputUtil.readInt("Enter your choice: ", 0, 4);
            switch (choice) {
                case 1:
                    checkInUI.start();
                    break;
                case 2:
                    vipAllocationUI.start();
                    break;
                case 3:
                    housekeepingUI.start();
                    break;
                case 4:
                    new FrontDeskUI(hotelController.getFrontDeskControl(), hotelController.getRooms()).displayFrontDeskMenu(hotelController.getRooms());
                    break;
                case 0:
                    System.out.println("\nSystem closed.");
                    break;
                default:
                    System.out.println("\nInvalid choice.");
            }
        } while (choice != 0);
    }
}
