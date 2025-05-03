import java.util.ArrayList;
import java.util.Scanner;

/**
 * A text-based Blackjack game implementation where a player plays against a dealer.
 * 
 * Rules and features:
 * <ul>
 *   <li>Aces count as 11, but convert to 1 if total exceeds 21.</li>
 *   <li>Dealer hits until reaching a score of at least 17.</li>
 *   <li>Player can bet up to $5000 per round.</li>
 * </ul>
 */
public class BlackJack {

    /**
     * Starts a Blackjack game session for the given player.
     * Continues until the player types "exit" or runs out of money.
     *
     * @param player the Player participating in the game
     * @param in the Scanner used for input
     */
    public void play(Player player, Scanner in) {
        while (true) {
            int bet = getBet(player, in);
            if (bet == -1) return;

            do {
                if (player.getBalance() < bet) {
                    System.out.println("Insufficient balance.");
                    break;
                }

                CardDeck deck = new CardDeck();
                ArrayList<Card> playerHand = new ArrayList<>();
                ArrayList<Card> dealerHand = new ArrayList<>();

                // Check if player wants to use a Golden Donut
                if (player.getDonutCount() > 0) {
                    System.out.print("\nUse a Golden Donut to draw a GOLDEN ACE? (y/n): ");
                    String use = in.nextLine().trim().toLowerCase();
                    if (use.equals("y") && player.useDonut()) {
                        player.useDonut();
                        playerHand.add(new Card("A", "Gold"));
                        playerHand.add(deck.draw());
                        System.out.println("You played a Golden Donut and received a GOLDEN ACE!");
                    } else {
                        playerHand.add(deck.draw());
                        playerHand.add(deck.draw());
                    }
                } else {
                    playerHand.add(deck.draw());
                    playerHand.add(deck.draw());
                }
                dealerHand.add(deck.draw());

                int playerScore = calculateScore(playerHand);
                System.out.println("\nYour hand:");
                for (Card c : playerHand) {
                    System.out.println("  " + c);
                }
                System.out.println("Dealer shows: " + dealerHand.get(0));

                // Player decision loop
                while (true) {
                    System.out.print("Hit(h) or Stand(s)? ");
                    String choice = in.nextLine().trim().toLowerCase();

                    if (choice.equals("h")) {
                        Card newCard = deck.draw();
                        playerHand.add(newCard);
                        playerScore = calculateScore(playerHand);
                        System.out.println("You drew: ");
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        System.out.println("  " + newCard);

                        if (playerScore > 21) {
                            System.out.println("You busted! Lost $" + bet);
                            player.updateBalance(-bet);
                            player.showOffEarnings();
                            break;
                        }
                    } else if (choice.equals("s")) {
                        break;
                    } else {
                        System.out.println("Invalid input. Type 'hit' or 'stand'.");
                    }
                }

                // Dealer logic, only if player hasn't busted
                if (playerScore <= 21) {
                    dealerHand.add(deck.draw());
                    int dealerScore = calculateScore(dealerHand);
                    while (dealerScore < 17) {
                        Card c = deck.draw();
                        dealerHand.add(c);
                        dealerScore = calculateScore(dealerHand);
                    }

                    System.out.println("\nDealer's hand:");
                    int i = 0;
                    for (Card c : dealerHand) {
                        try {
                            Thread.sleep(1000 + (i * 200));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        System.out.println("  " + c);
                        i++;
                    }

                    System.out.printf("Your score: %d | Dealer score: %d%n", playerScore, dealerScore);

                    if (dealerScore > 21 || playerScore > dealerScore) {
                        System.out.println("You win $" + bet + "!");
                        player.updateBalance(bet);
                    } else if (playerScore == dealerScore) {
                        System.out.println("Push. No money gained or lost.");
                    } else {
                        System.out.println("You lost $" + bet + ".");
                        player.updateBalance(-bet);
                    }

                    player.showOffEarnings();
                }
                MainGame.saveGame(false);
            } while (!playAgainPrompt(in));
        }
    }

    /**
     * Calculates the total Blackjack score for the given hand.
     * Aces count as 11, but are reduced to 1 if needed to avoid busting.
     *
     * @param hand a list of Cards representing a hand
     * @return the total Blackjack score
     */
    private int calculateScore(ArrayList<Card> hand) {
        int total = 0;
        int aceCount = 0;

        for (Card c : hand) {
            int val = c.getPoints();
            if (val == 11) aceCount++;
            total += val;
        }

        // Convert Aces from 11 to 1 if necessary
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    /**
     * Prompts the player to enter a bet amount or exit.
     *
     * @param player the Player placing the bet
     * @param in the Scanner used for input
     * @return the bet amount entered, or -1 if the player chooses to exit
     */
    private int getBet(Player player, Scanner in) {
        while (true) {
            System.out.println("\nCurrent Balance: $" + player.getBalance());
            int maxBet = player.inRoom.getName().equalsIgnoreCase("VIP") ? 20000 : 5000;
            System.out.print("Enter bet amount (max " + maxBet + ") or \"exit\": ");
            String input = in.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) return -1;

            try {
                int bet = Integer.parseInt(input);
                if (bet < 1 || bet > maxBet) {
                    System.out.println("Bet must be between 1 and 5000.");
                } else if (bet > player.getBalance()) {
                    System.out.println("You don't have enough balance.");
                } else {
                    return bet;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number or 'exit'.");
            }
        }
    }

    /**
     * Prompts the player to decide whether to play again with the same bet.
     *
     * @param in the Scanner used for input
     * @return true if the player wants to play again, false otherwise
     */
    private boolean playAgainPrompt(Scanner in) {
        System.out.print("Play again with same bet? (y/n): ");
        String input = in.nextLine().trim().toLowerCase();
        return input.equals("n");
    }
}
