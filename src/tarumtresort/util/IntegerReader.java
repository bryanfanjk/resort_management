/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tarumtresort.util;
import java.util.Scanner;

/**
 *
 * @author NYO
 */
public class IntegerReader {
    private static Scanner scanner = new Scanner(System.in);
    public static int readInteger() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}
