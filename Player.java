/**
 * Player class represents a player in the game, extending MainGame
 * Each player has a name, a balance, and a reference for the room they are in
 *
 * Features:
 *     Initialize a player with a default balance of 1000
 *     Update and retrieve the players balance
 *     Display current earnings
 *     Track the players current room
 *
 * @author Group A
 * @version 1.0
 */
public class Player extends MainGame {
    private String name;
    private int balance;
    public Room inRoom;

    /**
    * Constructs a player with a specified name
    * The initial balance is set to 1000 and the player is not placed in a room
    *
    * @param name The name of the player
    */
    public Player(String name) {
        this.name = name;
        this.balance = 1000;
        this.inRoom = null;
    }

    /**
    * Displays the players current balance
    */
    void showOffEarnings() {
        System.out.println("Current balance: " + balance);
    }

    /**
    * Updates the players balance by adding a specified amount
    *
    * @param amount The amount to be added the players balance
    */
    void updateBalance(int amount) {
        balance += amount;
    }

    /**
    * Sets the players balanced to a specified amount
    *
    * @param balance The new balance
    */
    void setBalance(int balance) {
        this.balance = balance;
    }

    /**
    * Gets the players name
    *
    * @return The players name
    */
    public String getName() {
        return this.name;
    }

    /**
    * Gets the players current balance
    * 
    * @return The players current balance
    */
    public int getBalance() {
        return this.balance;
    }

}
