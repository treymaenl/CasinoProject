import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a standard deck of 52 playing cards.
 * The deck is automatically populated and shuffled upon creation.
 */
public class CardDeck {
    private ArrayList<Card> cards = new ArrayList<>();

    /**
     * Constructs a standard 52-card deck.
     * Initializes all combinations of suits and values, then shuffles the deck.
     */
    public CardDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String suit : suits) {
            for (String value : values) {
                cards.add(new Card(value, suit));
            }
        }
        Collections.shuffle(cards);
    }

    /**
     * Draws (removes and returns) the top card from the deck.
     *
     * @return the top Card from the deck
     * @throws IndexOutOfBoundsException if the deck is empty
     */
    public Card draw() {
        return cards.remove(0);
    }
}
