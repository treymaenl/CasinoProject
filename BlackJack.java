import java.util.ArrayList;
import java.util.Scanner;

public class BlackJack {
    public void play(Player player, Scanner in) {
        while (true) {
            System.out.println("\nCurrent Balance: $" + player.getBalance());
            System.out.print("Enter bet amount (max $5000) or type \"exit\" to leave: ");
            String input = in.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Leaving BlackJack.");
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

            CardDeck deck = new CardDeck();
            ArrayList<Card> hand = new ArrayList<>();
            ArrayList<Card> dealer = new ArrayList<>();

            hand.add(deck.draw());
            hand.add(deck.draw());
            dealer.add(deck.draw());

            int playerScore = sum(hand);
            System.out.println("Your hand:");
            hand.forEach(System.out::println);

            System.out.println("Dealer shows: " + dealer.get(0));

            while (true) {
                System.out.print("Hit or Stand? ");
                String choice = in.nextLine().trim();
                if (choice.equalsIgnoreCase("hit")) {
                    Card newCard = deck.draw();
                    hand.add(newCard);
                    playerScore += newCard.getPoints();
                    System.out.println("You drew: " + newCard);
                    if (playerScore > 21) {
                        System.out.println("You busted! Lost $" + bet + ".");
                        player.updateBalance(-bet);
                        player.showOffEarnings();
                        break;
                    }
                } else if (choice.equalsIgnoreCase("stand")) {
                    break;
                } else {
                    System.out.println("Invalid choice.");
                }
            }

            if (playerScore <= 21) {
                dealer.add(deck.draw());
                int dealerScore = sum(dealer);
                while (dealerScore < 17) {
                    Card c = deck.draw();
                    dealer.add(c);
                    dealerScore = sum(dealer);
                }

                System.out.println("Dealer's hand:");
                dealer.forEach(System.out::println);

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
        }
    }

    private int sum(ArrayList<Card> cards) {
        int total = 0;
        int aceCount = 0;
        for (Card c : cards) {
            int pts = c.getPoints();
            if (pts == 11) aceCount++;
            total += pts;
        }
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }
        return total;
    }
}
