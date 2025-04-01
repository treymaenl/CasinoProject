public class Player extends MainGame {
    private String name;
    private int balance;
    public Room inRoom;

    public Player(String name) {
        this.name = name;
        this.balance = 1000;
        this.inRoom = Bar;
    }

    void showOffEarnings() {
        System.out.println("Current balance: " + balance);
    }

    void updateBalance(int amount) {
        balance += amount;
    }

    void setBalance(int balance) {
        this.balance = balance;
    }

    public String getName() {
        return this.name;
    }

    public int getBalance() {
        return this.balance;
    }

}
