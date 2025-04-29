import java.util.Random;
import java.util.Scanner;

public class Slots {
    public void play(Player player) {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("\nCurrent Balance: $" + player.getBalance());
            System.out.println("""
            Choose your bet amount:
              1. $20
              2. $50
              3. $100
              Type "exit" to leave.
            """);
            System.out.print("Enter choice: ");
            String input = in.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Leaving slots.");
                return;
            }

            int bet = 0;
            switch (input) {
                case "1" -> bet = 20;
                case "2" -> bet = 50;
                case "3" -> bet = 100;
                default -> {
                    System.out.println("Invalid choice.");
                    continue;
                }
            }

            if (player.getBalance() < bet) {
                System.out.println("Insufficient balance.");
                continue;
            }

            System.out.println("SPINNING...");
            Random rand = new Random();
            int a = rand.nextInt(5), b = rand.nextInt(5), c = rand.nextInt(5);
            System.out.printf("[%d] [%d] [%d]%n", a, b, c);

            if (a == b && b == c) {
                System.out.println("Jackpot! You win $" + (bet * 10) + "!");
                player.updateBalance((bet * 10) - bet);
            } else if (a == b || b == c || a == c) {
                System.out.println("Nice! You win $" + (bet * 2) + "!");
                player.updateBalance((bet * 2) - bet);
            } else {
                System.out.println("You lost $" + bet + ".");
                player.updateBalance(-bet);
            }

            player.showOffEarnings();
        }
    }
}
