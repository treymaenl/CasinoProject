import java.util.Random;
import java.util.Scanner;

/**
 * Represents a Slots game where a player can place bets and spin for winnings.
 * Winnings are calculated based on matching numbers in the slot spin result.
 */
public class Slots {

    /**
     * Starts the Slots game loop for the specified player.
     * The player chooses a bet amount and spins. The game continues
     * until the player chooses to exit or runs out of balance.
     *
     * @param player the Player participating in the game
     */
    public void play(Player player) {
        Scanner in = new Scanner(System.in);
        while (true) {
            int bet = getBet(player, in);
            if (bet == -1) return;

            do {
                if (player.getBalance() < bet) {
                    System.out.println("Insufficient balance.");
                    break;
                }

                System.out.print("\nSPINNING");
                for (int i = 0; i < 3; i++) {
                    try {
                        Thread.sleep(100);
                        System.out.print("...");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println();

                Random rand = new Random();
                int odds = 6;
                int rows = (player.isVIP()) ? 3 : 1;
                for (int i = 0; i < rows; i++) {
                    int a = rand.nextInt(odds), b = rand.nextInt(odds), c = rand.nextInt(odds), d = rand.nextInt(odds);
                    System.out.printf("[%d] [%d] [%d] [%d]%n", a, b, c, d);

                    if (a == b && b == c && c == d) {
                        System.out.println("Jackpot! You win $" + (bet * 10));
                        player.updateBalance((bet * 10) - bet);
                    } else if ((a == b && b == c) || (b == c && c == d)) {
                        System.out.println("3 in a row! You win $" + (bet * 3));
                        player.updateBalance((bet * 3) - bet);
                    } else if (a == b || b == c || c == d) {
                        System.out.println("Nice! You win $" + (bet * 2));
                        player.updateBalance((bet * 2) - bet);
                    } else {
                        System.out.println("You lost $" + bet + ".");
                        player.updateBalance(-bet);
                    }
                }

                player.showOffEarnings();
                MainGame.saveGame(false);
            } while (!playAgainPrompt(in));
        }
    }

    /**
     * Prompts the player to select a bet amount.
     * Options are $20, $50, or $100. Typing "exit" returns -1 to quit.
     *
     * @param player the Player placing the bet
     * @param in the Scanner for user input
     * @return the selected bet amount, or -1 if the player exits
     */
    private int getBet(Player player, Scanner in) {
        while (true) {
            int bet1, bet2, bet3;
            if (!player.isVIP()) {
                bet1 = 20;
                bet2 = 50;
                bet3 = 100;
            } else {
                bet1 = 100;
                bet2 = 500;
                bet3 = 1000;
            }
            System.out.println("\nCurrent Balance: $" + player.getBalance());
            if (player.isVIP()) {
                System.out.println("""
                    Choose your bet amount:
                      1. $100
                      2. $500
                      3. $1000
                      Type "exit" to leave slots.
                    """);
            } else {
                System.out.println("""
                    Choose your bet amount:
                      1. $20
                      2. $50
                      3. $100
                      Type "exit" to leave slots.
                    """);
            }
            System.out.print("Enter bet option: ");
            String input = in.nextLine().trim();
            return switch (input) {
                case "1" -> bet1;
                case "2" -> bet2;
                case "3" -> bet3;
                case "exit" -> -1;
                default -> {
                    System.out.println("Invalid choice.");
                    yield getBet(player, in);
                }
            };
        }
    }

    /**
     * Asks the player whether they want to spin again.
     *
     * @param in the Scanner for user input
     * @return true if the player chooses to continue, false otherwise
     */
    private boolean playAgainPrompt(Scanner in) {
        System.out.print("Spin again? (y/n): ");
        String response = in.nextLine().trim();
        System.out.println();
        return response.equalsIgnoreCase("n");
    }
}
