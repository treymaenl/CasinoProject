public class Player {
    private String name;
    private int balance;
    public Room inRoom;

    void showOffEarnings() {
        System.out.println("Current balance: " + balance);
    }

    void updateBalance(int amount) {
        balance += amount;
    }

}
