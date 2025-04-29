public class Card {
    public String suit;
    public String value;

    public Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    public int getPoints() {
        if (value.equals("A")) return 11;
        if (value.equals("K") || value.equals("Q") || value.equals("J")) return 10;
        return Integer.parseInt(value);
    }

    @Override
    public String toString() {
        return value + " of " + suit;
    }
}
