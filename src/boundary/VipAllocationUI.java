package boundary;

import control.HotelController;
import entity.RoomType;
import util.InputUtil;

//author: Ng Yung Onn

public class VipAllocationUI {

    private final GenReportVipUI reportUI;
    private final GenerateReportUI generalReportUI;

    private boolean filterExitSelected;

    public VipAllocationUI(HotelController controller) {
        this.reportUI = new GenReportVipUI(controller);
        this.generalReportUI = new GenerateReportUI(controller);
    }

    public void start() {
        int choice;

        do {
            System.out.println("\n=======================================");
            System.out.println("View Reports");
            System.out.println("=======================================");
            System.out.println("1. All Reservations Report (Module 1)");
            System.out.println("2. Standard Customers Waiting List Report (Module 1)");
            System.out.println("3. VIP Customer Summary Report (Module 2)");
            System.out.println("4. VIP Demand vs. Room Availability Summary (Module 2)");
            System.out.println("5. VIP Reservation History Report (Module 2)");
            System.out.println("6. Back to Main Menu");
            choice = InputUtil.readInt("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1:
                    viewFilteredReservationReport();
                    break;

                case 2:
                    viewFilteredWaitingReport();
                    break;
 
                case 3:
                    viewVipCustomerReport();
                    break;
                case 4:
                    reportUI.displayVipDemandAvailabilityReport();
                    break;
                case 5:
                    viewVipReservationHistoryReport();
                    break;
                case 6:
                    break;

                default:
                    System.out.println("\nInvalid choice.");
            }

        } while (choice != 6);
    }

    private void viewFilteredReservationReport() {
        generalReportUI.displayReservationReport(null, null);
        while (true) {
            int filterChoice = readGeneralReservationFilter();
            if (filterChoice == 7) {
                return;
            }
            switch (filterChoice) {
                case 1: generalReportUI.displayReservationReport(RoomType.DELUXE, null); break;
                case 2: generalReportUI.displayReservationReport(RoomType.PREMIUM, null); break;
                case 3: generalReportUI.displayReservationReport(RoomType.PLATINUM, null); break;
                case 4: generalReportUI.displayReservationReport(null, null); break;
                case 5: generalReportUI.displayReservationReport(null, true); break;
                case 6: generalReportUI.displayReservationReport(null, false); break;
                default: break;
            }
        }
    }

private int readGeneralReservationFilter() {
        System.out.println("\nFilter all reservations by:");
        System.out.println("1. Deluxe");
        System.out.println("2. Premium");
        System.out.println("3. Platinum");
        System.out.println("4. All Room Types");
        System.out.println("5. Checked Out Customers");
        System.out.println("6. Active Customers");
        System.out.println("7. Back");
        return InputUtil.readInt("Enter your choice: ", 1, 7);
}

    private void viewFilteredWaitingReport() {
        generalReportUI.displayWaitingReport(null);
        while (true) {
            RoomType filter = readGeneralWaitingFilter();
            if (filter == null && filterExitSelected) {
                return;
            }
            generalReportUI.displayWaitingReport(filter);
        }
    }

    private RoomType readGeneralWaitingFilter() {
        filterExitSelected = false;
        System.out.println("\nFilter standard waiting customers by room type:");
        System.out.println("1. Deluxe");
        System.out.println("2. Premium");
        System.out.println("3. Platinum");
        System.out.println("4. All Room Types");
        System.out.println("5. Back");
        int choice = InputUtil.readInt("Enter your choice: ", 1, 5);
        switch (choice) {
            case 1: return RoomType.DELUXE;
            case 2: return RoomType.PREMIUM;
            case 3: return RoomType.PLATINUM;
            case 5:
                filterExitSelected = true;
                return null;
            default: return null; // choice == 4
        }
    }
    
    private RoomType readReservationRoomTypeFilter() {
        filterExitSelected = false;

            System.out.println("\nFilter VIP reservations by room type:");

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

                case 5:
                    filterExitSelected = true;
                    return null;

                default:
                    return null; // choice == 4
            }
    }
    
    private Boolean readReservationStatusFilter() {
    filterExitSelected = false;
    
        System.out.println("\nFilter VIP reservations by customer status:");

        System.out.println("1. All VIP Customers");
        System.out.println("2. Active VIP Customers");
        System.out.println("3. Checked-out VIP Customers");
        System.out.println("4. Back");
        int choice = InputUtil.readInt("Enter your choice: ", 1, 4);

        switch (choice) {
            case 2:
                return false;

            case 3:
                return true;

            case 4:
                filterExitSelected = true;
                return null;

            default:
                return null; // choice == 1
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

        int sortChoice =
                readSortChoice();

        reportUI.displayVipCustomerReport(
                roomTypeFilter,
                nameFilter,
                sortChoice);
    }

    private RoomType readRoomTypeFilter() {
        filterExitSelected = false;

            System.out.println("\nFilter VIP customers by room type:");

            System.out.println("1. Deluxe");
            System.out.println("2. Premium");
            System.out.println("3. Platinum");
            System.out.println("4. All Room Types");
            System.out.println("5. Back");
            int choice =
                    InputUtil.readInt("Enter your choice: ", 1, 5);

            switch (choice) {
                case 1:
                    return RoomType.DELUXE;

                case 2:
                    return RoomType.PREMIUM;

                case 3:
                    return RoomType.PLATINUM;

                case 5:
                    filterExitSelected = true;
                    return null;

                default:
                    return null; // choice == 4
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
