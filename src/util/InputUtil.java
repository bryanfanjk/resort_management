package util;

import java.util.Scanner;

/* author: Ho Jia Ming */
public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);

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

    public static String readStringWithSkip(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input;
    }

    public static String readContactNumber(String prompt) {
        while (true) {
            String contact = readString(prompt);
            if (contact.matches("\\d{7,15}")) {
                return contact;
            }
            System.out.println("ERROR: Contact number must be digits only, between 7 and 15 digits.");
        }
    }

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

    public static String readConfirmationNumber(String prompt) {
        while (true) {
            String input = readString(prompt).toUpperCase();
            if (input.matches("\\d{8}") || input.matches("VIP\\d{5}")) {
                return input;
            }
            System.out.println("ERROR: Code must be an 8-digit code (Standard) or 'VIP' followed by 5 digits.");
        }
    }

    public static String readDate(String prompt) {
        while (true) {
            String date = readString(prompt);
            if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return date;
            }
            System.out.println("ERROR: Date must follow format YYYY-MM-DD.");
        }
    }

    public static void displayHeader(String title) {
        int length = title.length() + 8;
        String line = "=".repeat(length);
        System.out.println("\n" + line);
        System.out.println("||  " + title.toUpperCase() + "  ||");
        System.out.println(line);
    }

    public static void pressEnterToContinue() {
        System.out.println("\nPress Enter to return to menu...");
        scanner.nextLine();
    }
}
