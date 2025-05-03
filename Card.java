/**
 * Represents a playing card with a suit and value.
 * Provides functionality to determine the card's point value,
 * as used in card games like Blackjack.
 */
public class Card {
    public String suit;
    public String value;

    /**
     * Constructs a Card with the specified value and suit.
     *
     * @param value the face value of the card
     * @param suit  the suit of the card
     */
    public Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    /**
     * Returns the point value of the card.
     * - Aces ("A") are worth 11 points.
     * - Face cards ("K", "Q", "J") are worth 10 points.
     * - Number cards are worth their numeric value.
     *
     * @return the point value of the card
     */
    public int getPoints() {
        if (value.equals("A")) return 11;
        if (value.equals("K") || value.equals("Q") || value.equals("J")) return 10;
        return Integer.parseInt(value);
    }

    /**
     * Returns a string representation of the card in the format "Value of Suit".
     *
     * @return a string describing the card
     */
    @Override
    public String toString() {
        if (value.equalsIgnoreCase("A") && suit.equalsIgnoreCase("Gold")) {
            return "GOLDEN ACE";
        }
        return value + " of " + suit;
    }
}
