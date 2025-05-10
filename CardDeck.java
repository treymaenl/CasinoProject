import java.util.ArrayList;

public class CardDeck extends Card {
    ArrayList<Card> deck = new ArrayList<>();

    void shuffle() {}
    void remove() {}
    void add(Card card) {}
    void reset() {}
    Card getTop() { return deck.isEmpty() ? null : deck.get(0); }
}
