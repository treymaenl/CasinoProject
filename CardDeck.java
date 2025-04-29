import java.util.ArrayList;
import java.util.Collections;

public class CardDeck {
    private ArrayList<Card> cards = new ArrayList<>();

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

    public Card draw() {
        return cards.remove(0);
    }
}
