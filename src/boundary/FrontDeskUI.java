/*
 * Boundary class interacting with the user/actor (Front-Desk Agent).
 * Interacts only with Actor (via Scanner/Console) and Control object (FrontDeskControl).
 */
package boundary;

import java.util.Scanner;
import control.FrontDeskControl;
import dao.RoomData;
import entity.CustomerType;
import entity.GuestBillingInfo;
import entity.Room;

public class FrontDeskUI {

    private FrontDeskControl control;
    private Scanner scanner;
    private Room[] rooms;

    public FrontDeskUI() {
        this(new FrontDeskControl(), RoomData.createRooms());
    }

    public FrontDeskUI(FrontDeskControl control) {
        this(control, RoomData.createRooms());
    }

    public FrontDeskUI(FrontDeskControl control, Room[] rooms) {
        this.control = control != null ? control : new FrontDeskControl();
        this.scanner = new Scanner(System.in);
        this.rooms = rooms != null ? rooms : RoomData.createRooms();
    }

    public void displayFrontDeskMenu() {
        displayFrontDeskMenu(this.rooms != null ? this.rooms : RoomData.createRooms());
    }

    public void start() {
        displayFrontDeskMenu();
    }

    public void displayFrontDeskMenu(Room[] rooms) {
        if (rooms != null) {
            this.rooms = rooms;
        }
        int choice;

        do {
            System.out.println("\n=================================");
            System.out.println("     FRONT-DESK SERVICE MENU     ");
            System.out.println("=================================");
            System.out.println("1. Search Guest Information (8-digit Confirmation No., e.g. CONF0001)");
            System.out.println("2. Query Room Availability");
            System.out.println("3. View Billing Details");
            System.out.println("4. Guest Information Retrieval Report (All / Filtered)");
            System.out.println("5. Financial Report (Daily Revenue)");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    handleGuestSearch();
                    break;
                case 2:
                    handleRoomAvailabilityQuery(this.rooms);
                    break;
                case 3:
                    handleBillingQuery();
                    break;
                case 4:
                    handleGuestInformationReport();
                    break;
                case 5:
                    handleFinancialReport(this.rooms);
                    break;
                case 6:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-6.");
            }
        } while (choice != 6);
    }

    private void handleGuestSearch() {
        System.out.println("\n--- Search Guest Information ---");
        System.out.print("Enter 8-digit Confirmation Number (e.g. CONF0001): ");
        String confNum = scanner.nextLine().trim().toUpperCase();

        if (confNum.length() != 8 || !confNum.matches("[A-Z0-9]{8}")) {
            System.out.println("Invalid format. Confirmation number must be 8 characters (e.g. CONF0001).");
            return;
        }

        GuestBillingInfo guestInfo = control.searchGuestByConfirmation(confNum);
        if (guestInfo != null) {
            System.out.println("\nGuest Information Found:");
            System.out.println("---------------------------------");
            System.out.println(guestInfo.toString());
            System.out.println("---------------------------------");
        } else {
            System.out.println("No guest record found for confirmation number: " + confNum);
        }
    }

    private void handleRoomAvailabilityQuery(Room[] rooms) {
        System.out.println("\n--- Room Availability Query ---");
        String summary = control.getRoomAvailabilitySummary(rooms);
        System.out.println(summary);
    }

    private void handleBillingQuery() {
        System.out.println("\n--- Guest Billing Inquiry ---");
        System.out.print("Enter 8-digit Confirmation Number (e.g. CONF0001): ");
        String confNum = scanner.nextLine().trim().toUpperCase();

        if (confNum.length() != 8 || !confNum.matches("[A-Z0-9]{8}")) {
            System.out.println("Invalid format. Confirmation number must be 8 characters (e.g. CONF0001).");
            return;
        }

        GuestBillingInfo guestInfo = control.searchGuestByConfirmation(confNum);
        if (guestInfo != null) {
            System.out.println("\nBilling Details:");
            System.out.println("---------------------------------");
            System.out.println("Confirmation Number: " + guestInfo.getConfirmationNumber());
            System.out.println("Customer Name      : " + (guestInfo.getCustomer() != null ? guestInfo.getCustomer().getCustomerName() : "N/A"));
            System.out.println("Customer Tier      : " + (guestInfo.getCustomer() != null && guestInfo.getCustomer().getCustomerType() == CustomerType.VIP ? "VIP" : "Standard"));
            System.out.println("Nights Stayed      : " + (guestInfo.getCustomer() != null ? guestInfo.getCustomer().getNightsStayed() : 0));
            System.out.println("Daily Room Rate    : $" + String.format("%.2f", guestInfo.getDailyRoomRate()));
            System.out.println("Total Amount Due   : $" + String.format("%.2f", guestInfo.getTotalBillAmount()));
            System.out.println("---------------------------------");
        } else {
            System.out.println("No billing record found for confirmation number: " + confNum);
        }
    }

    private void handleGuestInformationReport() {
        System.out.println("\n--- Guest Information Retrieval Report ---");
        System.out.println("1. Show ALL Guests");
        System.out.println("2. Filter Guests by Check-in Date");
        System.out.println("3. Filter VIP Guests");
        System.out.println("4. Back to Front-Desk Service Menu");
        System.out.print("Enter choice (1-4): ");
        String subChoice = scanner.nextLine().trim();

        GuestBillingInfo[] guests;

        if ("1".equals(subChoice)) {
            guests = control.getAllGuests();
            System.out.println("\n[ REPORT: All Registered Guests ]");
        } else if ("2".equals(subChoice)) {
            System.out.print("Enter Check-in Date (DD/MM/YYYY): ");
            String dateInput = scanner.nextLine().trim();
            guests = control.getFilteredGuestsByCheckIn(dateInput);
            System.out.println("\n[ REPORT: Guests Checking In On " + dateInput + " ]");
        } else if ("3".equals(subChoice)) {
            guests = control.getFilteredVipGuests();
            System.out.println("\n[ REPORT: VIP Guests Only ]");
        } else if ("4".equals(subChoice)) {
            return;
        } else {
            System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
            return;
        }

        if (guests.length == 0) {
            System.out.println("No matching guest records found.");
            return;
        }

        System.out.println("=========================================================================================================");
        System.out.printf("%-12s %-18s %-10s %-5s %-12s %-12s %-10s %-12s %-12s%n",
                "Conf No.", "Guest Name", "Tier", "Pax", "Check-In", "Check-Out", "Room No.", "Daily Rate", "Total Bill");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        for (GuestBillingInfo g : guests) {
            String roomStr = (g.getRoom() != null) ? String.valueOf(g.getRoom().getRoomNumber()) : "Waiting";
            String name = (g.getCustomer() != null) ? g.getCustomer().getCustomerName() : "N/A";
            String tier = (g.getCustomer() != null && g.getCustomer().getCustomerType() == CustomerType.VIP) ? "VIP" : "Standard";
            int pax = (g.getCustomer() != null) ? g.getCustomer().getPax() : 0;
            String checkIn = (g.getCustomer() != null) ? g.getCustomer().getCheckInDate() : "N/A";
            String checkOut = (g.getCustomer() != null && g.getCustomer().getCheckOutDate() != null) ? g.getCustomer().getCheckOutDate() : "-";

            System.out.printf("%-12s %-18s %-10s %-5d %-12s %-12s %-10s $%-11.2f $%-11.2f%n",
                    g.getConfirmationNumber(),
                    name,
                    tier,
                    pax,
                    checkIn,
                    checkOut,
                    roomStr,
                    g.getDailyRoomRate(),
                    g.getTotalBillAmount());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("Total Records: " + guests.length);
        System.out.println("=========================================================================================================");
    }

    private void handleFinancialReport(Room[] rooms) {
        String report = control.generateFinancialReport(rooms);
        System.out.println(report);
    }
}
