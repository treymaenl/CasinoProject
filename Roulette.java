import java.util.Random;
import java.util.Scanner;

/**
 * A simple text-based Roulette game where players can place bets on colors or numbers.
 * 
 * The player can bet on:
 * <ul>
 *   <li>A specific color: "red", "black", or "green"</li>
 *   <li>A specific number from 0 to 36</li>
 * </ul>
 * Payouts:
 * <ul>
 *   <li>2x for correct color</li>
 *   <li>35x for exact number match</li>
 * </ul>
 */
public class Roulette {

    /**
     * Begins the Roulette game loop for the provided player.
     * Continues until the player exits or chooses to stop playing.
     *
     * @param player the Player participating in the game
     */
    public void play(Player player) {
        Scanner in = new Scanner(System.in);

        while (true) {
            int bet = getBet(player, in);
            if (bet == -1) return;

            do {
                System.out.println("\nRoulette Board:");
                System.out.println("""
                  GREEN: 0
                  RED: 1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36
                  BLACK: 2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35
                """);

                System.out.print("Bet on color (red/black/green) or a number (0-36): ");
                String choice = in.nextLine().trim().toLowerCase();

                if (player.getBalance() < bet) {
                    System.out.println("Insufficient balance.");
                    break;
                }

                System.out.print("SPINNING");
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(100);
                        System.out.print(".");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println();

                Random rand = new Random();
                int result = rand.nextInt(37); // Spin result between 0 and 36
                String color = getColor(result);
                System.out.printf("Ball landed on %s %d%n", color.toUpperCase(), result);

                boolean win = false;

                // Color bet logic
                if (choice.equals("red") || choice.equals("black") || choice.equals("green")) {
                    win = choice.equals(color);
                    if (win) {
                        int mult = 2;
                        if (color.equals("green")) mult = 100;
                        System.out.println("You win $" + (bet * mult));
                        player.updateBalance(bet * mult - bet);
                    } else {
                        System.out.println("You lost $" + bet);
                        player.updateBalance(-bet);
                    }

                // Number bet logic
                } else {
                    try {
                        int guessed = Integer.parseInt(choice);
                        if (guessed < 0 || guessed > 36) throw new NumberFormatException();
                        if (guessed == result) {
                            int mult = 25;
                            if (guessed == 0) mult = 100;
                            System.out.println("Exact match! You win $" + (bet * mult));
                            player.updateBalance((mult * bet) - bet);
                        } else {
                            System.out.println("Wrong number. You lost $" + bet);
                            player.updateBalance(-bet);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid bet choice.");
                        continue;
                    }
                }

                player.showOffEarnings();
            } while (!playAgainPrompt(in));
        }
    }

    /**
     * Determines the color of the given roulette number.
     *
     * @param number the roulette number (0–36)
     * @return a string representing the color: "red", "black", or "green"
     */
    private String getColor(int number) {
        if (number == 0) return "green";
        int[] redNums = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int r : redNums) {
            if (r == number) return "red";
        }
        return "black";
    }

    /**
     * Prompts the player to enter a bet amount or exit the game.
     *
     * @param player the Player placing the bet
     * @param in the Scanner for reading user input
     * @return the chosen bet amount, or -1 if the player chooses to exit
     */
    private int getBet(Player player, Scanner in) {
        while (true) {
            System.out.println("\nCurrent Balance: $" + player.getBalance());
            System.out.print("Enter bet amount (max $5000) or \"exit\": ");
            String input = in.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) return -1;
            try {
                int bet = Integer.parseInt(input);
                if (bet < 1 || bet > 5000 || bet > player.getBalance()) {
                    System.out.println("Invalid or insufficient balance.");
                } else {
                    return bet;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    /**
     * Prompts the player to decide whether to play another round.
     *
     * @param in the Scanner for reading user input
     * @return true if the player chooses to play again; false otherwise
     */
    private boolean playAgainPrompt(Scanner in) {
        System.out.print("Play again? (y/n): ");
        return in.nextLine().trim().equalsIgnoreCase("n");
    }
}
