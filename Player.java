/**
 * The Player class represents a player in the casino game.
 * It stores the player's name, balance, current room, and VIP offer status.
 * Provides methods to access and update player details and check VIP membership.
 */
public class Player extends MainGame {
    // The name of the player
    private String name;

    // The current balance of the player
    private int balance;

    // The current room the player is in
    public Room inRoom;

    // Flag indicating if the player has already been offered VIP directly
    public boolean offeredVIP = false;

    // Golden donuts in possession
    private int goldenDonuts = 0;

    /**
     * Constructs a new Player with a given name and initial balance of $1000.
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.balance = 1000;
        this.inRoom = null;
    }

    /**
     * Displays the current balance of the player.
     */
    void showOffEarnings() {
        System.out.println("Current balance: $" + balance);
    }

    /**
     * Updates the player's balance by a specific amount.
     *
     * @param amount The amount to adjust the balance by (can be negative).
     */
    void updateBalance(int amount) {
        balance += amount;
    }

    /**
     * Sets the player's balance to a specific value.
     *
     * @param balance The new balance to set.
     */
    void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * Retrieves the player's name.
     *
     * @return The name of the player.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the player's current balance.
     *
     * @return The balance of the player.
     */
    public int getBalance() {
        return this.balance;
    }

    /**
     * Checks whether the player is a VIP.
     *
     * @return true if VIP room is unlocked; false otherwise.
     */
    public boolean isVIP() {
        return !MainGame.rooms.get(2).isLocked();
    }

    /**
     * Adds one Golden Donut to the player's inventory.
     */
    public void addDonut() {
        goldenDonuts++;
    }

    /**
     * Uses one Golden Donut if the player has any.
     *
     * @return true if a donut was successfully used; false if none were available.
     */
    public boolean useDonut() {
        if (goldenDonuts > 0) {
            goldenDonuts--;
            return true;
        }
        return false;
    }

    /**
     * Returns the number of Golden Donuts the player currently has.
     *
     * @return the count of Golden Donuts.
     */
    public int getDonutCount() {
        return goldenDonuts;
    }

    /**
     * Sets the number of Golden Donuts (used during loading saved games).
     *
     * @param count the number of donuts to assign to the player.
     */
    public void setDonutCount(int count) {
        this.goldenDonuts = count;
    }
}