package boundary;

import control.HotelController;
import entity.RoomType;
import util.InputUtil;

//author: Ng Yung Onn

public class VipAllocationUI {
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
            System.out.println("2. VIP Demand vs. Room Availability Summary");
            System.out.println("3. VIP Reservation History Report");
            System.out.println("4. Back to Main Menu");
            choice = InputUtil.readInt("Enter your choice: ", 1, 4);

            switch (choice) {
                case 1:
                    viewVipCustomerReport();
                    break;

                case 2:
                    reportUI.displayVipDemandAvailabilityReport();
                    break;
 
                case 3:
                    viewVipReservationHistoryReport();
                    break;
                   
                case 4:
                    break;

                default:
                    System.out.println("\nInvalid choice.");
            }

        } while (choice != 4);
    }
    
    private RoomType readReservationRoomTypeFilter() {
        filterExitSelected = false;

        while (true) {
            System.out.println(
                    "\nFilter VIP reservations by room type:");

            System.out.println("1. Deluxe");
            System.out.println("2. Premium");
            System.out.println("3. Platinum");
            System.out.println("4. All Room Types");
            System.out.println("5. Back");
            int choice = InputUtil.readInt("Enter your choice: ", 1, 5);

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
    
    private Boolean readReservationStatusFilter() {
    filterExitSelected = false;

    while (true) {
        System.out.println(
                "\nFilter VIP reservations by customer status:");

        System.out.println("1. All VIP Customers");
        System.out.println("2. Active VIP Customers");
        System.out.println("3. Checked-out VIP Customers");
        System.out.println("4. Back");
        int choice = InputUtil.readInt("Enter your choice: ", 1, 4);
        switch (choice) {
            case 1:
                return null;

            case 2:
                return false;

            case 3:
                return true;

            case 4:
                filterExitSelected = true;
                return null;

            default:
                System.out.println(
                        "Please choose 1, 2, 3, or 4.");
            }
        }
    }
    
    private void viewVipReservationHistoryReport() {
    RoomType roomTypeFilter =
            readReservationRoomTypeFilter();

    if (filterExitSelected) {
        return;
    }

    Boolean checkedOutFilter =
            readReservationStatusFilter();

    if (checkedOutFilter == null
            && filterExitSelected) {
        return;
    }

    reportUI.displayVipReservationHistoryReport(
            roomTypeFilter,
            checkedOutFilter);
    }

    private void viewVipCustomerReport() {
        RoomType roomTypeFilter =
                readRoomTypeFilter();

        if (filterExitSelected) {
            return;
        }

        String nameFilter = InputUtil.readStringWithSkip("Search customer name, or press Enter for all: ");

        int sortChoice = readSortChoice();

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
            int choice = InputUtil.readInt("Enter your choice: ", 1, 5);
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
        System.out.println("\nSort VIP report by:");
        System.out.println("1. Waiting Position");
        System.out.println("2. Customer Name");
        System.out.println("3. Number of Guests");
        System.out.println("4. Nights Stayed");
        return InputUtil.readInt("Enter your choice: ", 1, 4);
    }
}
