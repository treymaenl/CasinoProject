import java.util.Random;
import java.util.Scanner;

public class Roulette {
    public void play(Player player) {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("\nCurrent Balance: $" + player.getBalance());
            System.out.print("Enter bet amount (max $5000) or type \"exit\" to leave: ");
            String input = in.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Leaving roulette.");
                return;
            }

            int bet;
            try {
                bet = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (bet < 1 || bet > 5000 || bet > player.getBalance()) {
                System.out.println("Invalid bet or insufficient funds.");
                continue;
            }

            System.out.println("SPINNING...");
            Random rand = new Random();
            int number = rand.nextInt(37);
            String color = (number == 0) ? "Green" : (number % 2 == 0 ? "Black" : "Red");

            System.out.printf("Landed on %s %d%n", color, number);

            if (number == 7) {
                System.out.println("Lucky 7! You win $" + (bet * 3) + "!");
                player.updateBalance((bet * 3) - bet);
            } else {
                System.out.println("You lost $" + bet + ".");
                player.updateBalance(-bet);
            }

            player.showOffEarnings();
        }
    }
}
