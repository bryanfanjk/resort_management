/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import java.util.Scanner;
import boundary.HotelCheckInUI;
import util.IntegerReader;

/**
 *
 * @author NYO
 */
public class MainMenu {
    
    private final Scanner scanner = new Scanner(System.in);
    
    public void menu() {
        int choice;
        do {
            System.out.println("\nTARUMT  Resorts, a luxury hospitality chain.");
            System.out.println("==============================================");
            System.out.println("1. Module 1 Walk-In Registrations & Standard Booking Procedure");
            System.out.println("2. Module 2 VIP & Loyalty Tier Priority Room Allocation");
            System.out.println("3. Module 3 Housekeeping and Task Log");
            System.out.println("4. Module 4 ???");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = IntegerReader.readInteger();


            switch (choice) {
                case 1:
                    new HotelCheckInUI().start();
                    break;
                case 2:
                    System.out.println("integrated in module 1");
                    break;
                case 3:
                    System.out.println("not done");
                    break;
                case 4:
                    System.out.println("Not Decicded");
                case 0:
                    System.out.println("\nSystem closed.");
                    break;
                default:
                    System.out.println("\nInvalid choice.");
            }
        } while (choice != 0);
    }

}
