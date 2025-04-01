
public class MainGame {
    Player gambler;
    Room Bar = new Room("Bar"), GameRoom = new Room("GameRoom"), Vip = new Room("VIP");
    public Room[] rooms = {Bar, GameRoom, Vip};

    void startGame() {}
    void loadGame() {}
    void saveGame() {}
    void quitGame() {}

    void updateRoom(String newRoom) {
        for (Room room : rooms) {
            if (room.getName().equalsIgnoreCase(newRoom)) {
                gambler.inRoom = room;
            }
        }
    }
}
