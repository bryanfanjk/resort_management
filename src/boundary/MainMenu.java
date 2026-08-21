package boundary;

import adt.LinkedStack;
import adt.List;
import adt.ListInterface;
import control.AuthController;
import control.HotelController;
import control.HousekeepingController;
import entity.HousekeepingLog;
import entity.Room;
import java.util.Scanner;
import control.HotelController;
import util.IntegerReader;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final HotelController hotelController = new HotelController();
    private final LinkedStack<HousekeepingLog> housekeepingStack = new LinkedStack<>();
    private final AuthController authController = new AuthController();
    private final HousekeepingController housekeepingController;
    private final HotelCheckInUI checkInUI;
    private final HousekeepingUI housekeepingUI;

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
                authController);
    }

    public void menu() {
        int choice;
        do {
            System.out.println("\nTARUMT  Resorts, a luxury hospitality chain.");
            System.out.println("==============================================");
            System.out.println("1. Module 1 Walk-In Registrations & Standard Booking Procedure");
            System.out.println("2. Module 2 VIP & Loyalty Tier Priority Room Allocation");
            System.out.println("3. Module 3 Housekeeping and Task Log");
            System.out.println("4. Module 4 Front-Desk Service & Billing");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = IntegerReader.readInteger();

            switch (choice) {
                case 1:
                    new HotelCheckInUI(hotelController).start();
                    break;
                case 2:
                    System.out.println("\nintegrated in module 1");
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
