package tarumtresort.util;

import java.util.Scanner;

/**
 * Utility class for capturing, parsing, and validating console inputs.
 * Ensures the boundary classes can retrieve clean data without polluting
 * business controllers.
 * 
 * @author Admin
 */
public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Reads a validated integer from the console within an inclusive range.
     * 
     * @param prompt User instruction.
     * @param min Minimum allowable value.
     * @param max Maximum allowable value.
     * @return Validated integer.
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("ERROR: Please enter a number between %d and %d.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid number format. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a validated integer from the console within an inclusive range, or allows -1 to exit/cancel.
     * 
     * @param prompt User instruction.
     * @param min Minimum allowable value.
     * @param max Maximum allowable value.
     * @return Validated integer, or -1 to cancel.
     */
    public static int readIntWithExit(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value == -1 || (value >= min && value <= max)) {
                    return value;
                }
                System.out.printf("ERROR: Please enter a number between %d and %d, or -1 to cancel.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid number format. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a non-empty string.
     * 
     * @param prompt User instruction.
     * @return Validated string.
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("ERROR: Input cannot be empty. Please try again.");
        }
    }

    /**
     * Reads a phone/contact number consisting only of numbers.
     * 
     * @param prompt User instruction.
     * @return Validated contact string.
     */
    public static String readContactNumber(String prompt) {
        while (true) {
            String contact = readString(prompt);
            if (contact.matches("\\d{7,15}")) {
                return contact;
            }
            System.out.println("ERROR: Contact number must be digits only, between 7 and 15 digits.");
        }
    }

    /**
     * Reads a double value within a minimum.
     * 
     * @param prompt User instruction.
     * @param min Minimum value.
     * @return Validated double value.
     */
    public static double readDouble(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min) {
                    return value;
                }
                System.out.printf("ERROR: Please enter a value >= %.2f.\n", min);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid numeric format. Please enter a valid decimal number.");
            }
        }
    }

    /**
     * Reads a booking confirmation code. Standard is 8 digits. VIP contains "VIP".
     * 
     * @param prompt User instruction.
     * @return Validated confirmation code.
     */
    public static String readConfirmationNumber(String prompt) {
        while (true) {
            String input = readString(prompt).toUpperCase();
            if (input.matches("\\d{8}") || input.matches("VIP\\d{5}")) {
                return input;
            }
            System.out.println("ERROR: Code must be an 8-digit code (Standard) or 'VIP' followed by 5 digits.");
        }
    }

    /**
     * Reads a date string in YYYY-MM-DD format.
     * 
     * @param prompt User instruction.
     * @return Validated date string.
     */
    public static String readDate(String prompt) {
        while (true) {
            String date = readString(prompt);
            if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return date;
            }
            System.out.println("ERROR: Date must follow format YYYY-MM-DD.");
        }
    }

    /**
     * Displays a clean console header block.
     * 
     * @param title Title text.
     */
    public static void displayHeader(String title) {
        int length = title.length() + 8;
        String line = "=".repeat(length);
        System.out.println("\n" + line);
        System.out.println("||  " + title.toUpperCase() + "  ||");
        System.out.println(line);
    }

    /**
     * Prompts the user to press Enter to continue.
     */
    public static void pressEnterToContinue() {
        System.out.println("\nPress Enter to return to menu...");
        scanner.nextLine();
    }
}
