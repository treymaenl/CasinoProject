import java.util.ArrayList;
import java.util.Scanner;

public class BlackJack {
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

                playerHand.add(deck.draw());
                playerHand.add(deck.draw());
                dealerHand.add(deck.draw());

                int playerScore = calculateScore(playerHand);
                System.out.println("\nYour hand:");
                for (Card c : playerHand) {
                    System.out.println("  " + c);
                }
                System.out.println("Dealer shows: " + dealerHand.get(0));

                // Player decision
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

                // Dealer logic only runs if player didn't bust
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
            } while (playAgainPrompt(in));
        }
    }

    private int calculateScore(ArrayList<Card> hand) {
        int total = 0;
        int aceCount = 0;

        for (Card c : hand) {
            int val = c.getPoints();
            if (val == 11) aceCount++;
            total += val;
        }

        // Adjust Aces if total is over 21
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    private int getBet(Player player, Scanner in) {
        while (true) {
            System.out.println("\nCurrent Balance: $" + player.getBalance());
            System.out.print("Enter bet amount (max $5000) or type 'exit': ");
            String input = in.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) return -1;

            try {
                int bet = Integer.parseInt(input);
                if (bet < 1 || bet > 5000) {
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

    private boolean playAgainPrompt(Scanner in) {
        System.out.print("Play again with same bet? (y/n): ");
        String input = in.nextLine().trim().toLowerCase();
        return input.equals("y");
    }
}
