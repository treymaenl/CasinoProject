import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MainGame {
    static Player gambler;
    static Room Bar = new Room("Bar"), GameRoom = new Room("GameRoom"), Vip = new Room("VIP", true);
    public Room[] rooms = {Bar, GameRoom, Vip};

    private static void setupGame() {
        
    }

    public static void main(String[] args) {
        File saveFile = new File("save.txt");
        Scanner in = new Scanner(System.in);
        MainGame game = new MainGame();

        setupGame();

        try {
            if (!saveFile.exists()) {
                System.out.print("Enter your name: ");
                String playerName = in.nextLine();
                System.out.println("Welcome, " + playerName + "!");
                gambler = new Player(playerName);
                System.out.println("For help with commands or options for your current room, simply type \"help\" or \"?\" at anytime!");
                saveFile.createNewFile();
                saveGame(false);
            } else {
                System.out.println("Save file found. Loading game...");
                loadGame();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while handling the save file.");
            e.printStackTrace();
        }

        while (true) {
            if (in.nextLine().equals("?") || in.nextLine().equalsIgnoreCase("help")) {
                System.out.println("General help:\n");
                System.out.println("Room help:\n");
                System.out.println(gambler.inRoom.getHelp());
            }
        }
    }

    static void restart() {
        File delete = new File("save.txt");
        if (delete.delete()) {
            System.out.println("Old save deleted, restart game to begin fresh.");
        } else {
            System.out.println("Nothing to restart!");
        }
    }

    static void loadGame() {
        File saveFile = new File("save.txt");
        try (Scanner reader = new Scanner(saveFile)) {
            if (saveFile.exists()) {
                gambler = new Player(reader.nextLine());
                gambler.setBalance(Integer.parseInt(reader.nextLine()));
                Vip.setLocked(Boolean.parseBoolean(reader.nextLine()));
                System.out.println("Game loaded. Welcome back, " + gambler.getName() + ".");
            } else {
                System.out.println("Save file not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading the game.");
            e.printStackTrace();
        }
    }

    static void saveGame(boolean printSave) {
        try (FileWriter writer = new FileWriter("save.txt")) {
            writer.write(gambler.getName() + "\n");
            writer.write(gambler.getBalance() + "\n");
            writer.write(Vip.locked + "\n");
            if (printSave) {
                System.out.println("Game saved successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error saving the game.");
            e.printStackTrace();
        }
    }

    void updateRoom(String newRoom) {
        for (Room room : rooms) {
            if (room.name.equalsIgnoreCase(newRoom)) {
                if (room.locked) {
                    System.out.println("Room is locked!");
                    return;
                }
                gambler.inRoom = room;
                System.out.println("Moved to: " + room.name);
                return;
            }
        }
        System.out.println("Room not found.");
    }
}