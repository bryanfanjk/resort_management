package tarumtresort.boundary;

import tarumtresort.util.InputUtil;

/**
 * Boundary class representing the primary menu entry point.
 * Directs navigation to distinct application modules.
 * Adheres strictly to ECB: contains no business logic.
 * 
 * @author Admin
 */
public class MainMenuUI {

    private final BookingUI bookingUI;
    private final VIPAllocationUI vipUI;
    private final HousekeepingUI housekeepingUI;
    private final FrontDeskUI frontDeskUI;
    private final LoyaltyUI loyaltyUI;
    private final ReportUI reportUI;

    public MainMenuUI(BookingUI bookingUI, 
                      VIPAllocationUI vipUI, 
                      HousekeepingUI housekeepingUI, 
                      FrontDeskUI frontDeskUI, 
                      LoyaltyUI loyaltyUI, 
                      ReportUI reportUI) {
        this.bookingUI = bookingUI;
        this.vipUI = vipUI;
        this.housekeepingUI = housekeepingUI;
        this.frontDeskUI = frontDeskUI;
        this.loyaltyUI = loyaltyUI;
        this.reportUI = reportUI;
    }

    public void startMenuLoop() {
        while (true) {
            System.out.println("\n\u001B[36m=========================================================\u001B[0m");
            System.out.println("\u001B[36m||          WELCOME TO TARUMT RESORTS SYSTEM           ||\u001B[0m");
            System.out.println("\u001B[36m||         Room Reservation & Optimization App         ||\u001B[0m");
            System.out.println("\u001B[36m=========================================================\u001B[0m");
            System.out.println("1. Walk-In & Standard Booking Queue (FIFO Queue)");
            System.out.println("2. VIP & Loyalty Tier Room Allocation (Max-Heap Priority Queue)");
            System.out.println("3. Housekeeping Status & Task Logs (LIFO Stack History)");
            System.out.println("4. Front-Desk Customer Search Service (BST Lookup & Range)");
            System.out.println("5. Loyalty Rewards & Points Redemption (Members Registry)");
            System.out.println("6. Resort Analytics & Report Generation (Sorting/Filtering)");
            System.out.println("7. Exit System");
            System.out.println("\u001B[36m=========================================================\u001B[0m");

            int choice = InputUtil.readInt("Enter module choice (1-7): ", 1, 7);
            switch (choice) {
                case 1:
                    bookingUI.start();
                    break;
                case 2:
                    vipUI.start();
                    break;
                case 3:
                    housekeepingUI.start();
                    break;
                case 4:
                    frontDeskUI.start();
                    break;
                case 5:
                    loyaltyUI.start();
                    break;
                case 6:
                    reportUI.start();
                    break;
                case 7:
                    System.out.println("\nThank you for using TARUMT Resorts Management System. Goodbye!");
                    return;
            }
        }
    }
}
