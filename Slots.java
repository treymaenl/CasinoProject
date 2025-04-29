import java.util.Random;
import java.util.Scanner;

public class Slots {
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

                System.out.print("SPINNING");
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
                int a = rand.nextInt(5), b = rand.nextInt(5), c = rand.nextInt(5);
                System.out.printf("[%d] [%d] [%d]%n", a, b, c);

                if (a == b && b == c) {
                    System.out.println("Jackpot! You win $" + (bet * 10));
                    player.updateBalance((bet * 10) - bet);
                } else if (a == b || b == c || a == c) {
                    System.out.println("Nice! You win $" + (bet * 2));
                    player.updateBalance((bet * 2) - bet);
                } else {
                    System.out.println("You lost $" + bet + ".");
                    player.updateBalance(-bet);
                }
                player.showOffEarnings();
            } while (playAgainPrompt(in));
        }
    }

    private int getBet(Player player, Scanner in) {
        while (true) {
            System.out.println("\nCurrent Balance: $" + player.getBalance());
            System.out.println("""
            Choose your bet amount:
              1. $20
              2. $50
              3. $100
              Type "exit" to leave.
            """);
            String input = in.nextLine().trim();
            return switch (input) {
                case "1" -> 20;
                case "2" -> 50;
                case "3" -> 100;
                case "exit" -> -1;
                default -> {
                    System.out.println("Invalid choice.");
                    yield getBet(player, in);
                }
            };
        }
    }

    private boolean playAgainPrompt(Scanner in) {
        System.out.print("Spin again? (y/n): ");
        String response = in.nextLine().trim();
        return response.equalsIgnoreCase("y");
    }
}
