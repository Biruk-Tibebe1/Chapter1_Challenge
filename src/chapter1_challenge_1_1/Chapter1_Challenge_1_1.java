package chapter1_challenge_1_1;

import java.util.Scanner;

public class Chapter1_Challenge_1_1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the cryptic message (positive integer): ");
        int input = scanner.nextInt();
        scanner.close();
        
        // Calculate number of digits
        int digitCount = (int) (Math.log10(input) + 1);
        
        // Extract first digit
        int firstDigit = input / (int) Math.pow(10, digitCount - 1);
        
        // Extract last digit
        int lastDigit = input % 10;
        
        // Product of first and last
        int product = firstDigit * lastDigit;
        
        // Extract second digit (assumes 5 digits as in example: /1000 %10 for second from left)
        int secondDigit = (input / 1000) % 10;
        
        // Extract second-last digit (/10 %10)
        int secondLastDigit = (input / 10) % 10;
        
        // Sum of second and second-last
        int sumValue = secondDigit + secondLastDigit;
        
        // Concatenate as strings
        String finalCode = String.valueOf(product) + String.valueOf(sumValue);
        
        System.out.println("The decrypted code is: " + finalCode);
    }
}