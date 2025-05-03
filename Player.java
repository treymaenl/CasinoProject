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
}