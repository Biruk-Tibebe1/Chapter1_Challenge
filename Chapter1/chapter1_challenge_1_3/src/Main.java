
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int health = 100;
        Random rand = new Random();
        Scanner sc = new Scanner(System.in);
        boolean defeated = false;
        
        for (int room = 1; room <= 5; room++) {
            System.out.println("Entering room " + room + "...");
            int event = rand.nextInt(3) + 1;
            
            switch (event) {
                case 1:
                    health -= 20;
                    System.out.println("A trap sprung! Health is now " + health + ".");
                    break;
                case 2:
                    health += 15;
                    if (health > 100) health = 100;
                    System.out.println("You found a healing potion! Health is now " + health + 
                                       (health == 100 ? " (capped to 100)." : "."));
                    break;
                case 3:
                    int monsterNum = rand.nextInt(5) + 1;
                    System.out.println("A monster appears! Guess a number (1-5) to defeat it:");
                    int guess;
                    do {
                        System.out.print("Guess: ");
                        guess = sc.nextInt();
                        if (guess != monsterNum) {
                            System.out.println("Wrong! Try again:");
                        }
                    } while (guess != monsterNum);
                    System.out.println("You defeated the monster!");
                    break;
            }
            
            if (health <= 0) {
                System.out.println("You have been defeated in room " + room + ".");
                defeated = true;
                break;
            }
        }
        
        if (!defeated) {
            System.out.println("You cleared the dungeon! Victorious with " + health + " health!");
        }
        
        sc.close();
    }
}