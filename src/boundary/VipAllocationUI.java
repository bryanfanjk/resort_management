package boundary;

import control.HotelController;
import entity.RoomType;
import java.util.Scanner;
import util.IntegerReader;

//author: Ng Yung Onn

public class VipAllocationUI {

    private final Scanner scanner = new Scanner(System.in);
    private final GenReportVipUI reportUI;

    private boolean filterExitSelected;

    public VipAllocationUI(HotelController controller) {
        this.reportUI = new GenReportVipUI(controller);
    }

    public void start() {
        int choice;

        do {
            System.out.println("\n=======================================");
            System.out.println("Module 2: VIP Priority Room Allocation");
            System.out.println("=======================================");
            System.out.println("1. VIP Customer Summary Report");
            System.out.println(
                    "2. VIP Demand vs. Room Availability Summary");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");

            choice = IntegerReader.readInteger();

            switch (choice) {
                case 1:
                    viewVipCustomerReport();
                    break;

                case 2:
                    reportUI.displayVipDemandAvailabilityReport();
                    break;

                case 3:
                    break;

                default:
                    System.out.println("\nInvalid choice.");
            }

        } while (choice != 3);
    }

    private void viewVipCustomerReport() {
        RoomType roomTypeFilter =
                readRoomTypeFilter();

        if (filterExitSelected) {
            return;
        }

        System.out.print(
                "Search customer name, or press Enter for all: ");

        String nameFilter =
                scanner.nextLine().trim();

        int sortChoice =
                readSortChoice();

        reportUI.displayVipCustomerReport(
                roomTypeFilter,
                nameFilter,
                sortChoice);
    }

    private RoomType readRoomTypeFilter() {
        filterExitSelected = false;

        while (true) {
            System.out.println(
                    "\nFilter VIP customers by room type:");

            System.out.println("1. Deluxe");
            System.out.println("2. Premium");
            System.out.println("3. Platinum");
            System.out.println("4. All Room Types");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");

            int choice =
                    IntegerReader.readInteger();

            switch (choice) {
                case 1:
                    return RoomType.DELUXE;

                case 2:
                    return RoomType.PREMIUM;

                case 3:
                    return RoomType.PLATINUM;

                case 4:
                    return null;

                case 5:
                    filterExitSelected = true;
                    return null;

                default:
                    System.out.println(
                            "Please choose 1, 2, 3, 4, or 5.");
            }
        }
    }

    private int readSortChoice() {
        while (true) {
            System.out.println("\nSort VIP report by:");
            System.out.println("1. Waiting Position");
            System.out.println("2. Customer Name");
            System.out.println("3. Number of Guests");
            System.out.println("4. Nights Stayed");
            System.out.print("Enter your choice: ");

            int choice =
                    IntegerReader.readInteger();

            if (choice >= 1 && choice <= 4) {
                return choice;
            }

            System.out.println(
                    "Please choose 1, 2, 3, or 4.");
        }
    }
}
