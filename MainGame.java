import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MainGame {
    static Player gambler;
    static Room Bar = new Room("Bar"), GameRoom = new Room("Game Room"), Vip = new Room("VIP", true);
    public static Room[] rooms = {Bar, GameRoom, Vip};
    static boolean tutorial = false;
    static String generalHelp = """
                                    "exit" - will prompt you to exit the game
                                    "restart" - will prompt you to restart the game
                                    "move" - will prompt you to move rooms
                                    "save" - will save the game""";

    private static void setupGame() {
        
    }

    public static void main(String[] args) {
        File saveFile = new File("save.txt");
        Scanner in = new Scanner(System.in);
        MainGame game = new MainGame();

        setupGame();

        try {
            if (!saveFile.exists()) {
                tutorial = true;
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
            System.out.printf(gambler.getName().toUpperCase() + ": ");
            String user;

            while (tutorial) { 
                System.out.printf("Would you like a tutorial? y/n: ");
                user = in.nextLine();
                int choice = yesOrNo(user);
                if (choice == 1) {
                    break;
                } else if (choice == 0) {
                    System.out.println("No tutorial it is then!");
                    tutorial = false;
                    break;
                }
            }
            if (tutorial) {
                System.out.println("Very well then!\n" +
                                    "Seeing as this is your first time playing, here are some important commands that you will be using to play the game:");
                System.out.println(generalHelp);
                System.out.println("You can see commands at any point by typing \"help\" or \"?\"");
                while (true) {
                    System.out.printf("Ready to continue to the game? Y/N: ");
                    user = in.nextLine();
                    int choice = yesOrNo(user);
                    if (choice == 1) {
                        System.out.println("Great let's begin at the bar!");
                        break;
                    } else {
                        System.out.println("...");
                    }
                }
                tutorial = false;
            }


            user = in.nextLine();
            if (user.equals("?") || user.equalsIgnoreCase("help")) {
                System.out.println("General help:");
                System.out.println(generalHelp);
                System.out.println(gambler.inRoom.getName() + " help:");
                System.out.println(gambler.inRoom.getHelp());


            } else if (user.equalsIgnoreCase("move")) {
                System.out.println("Which room would you like to move to?");
                for (Room room : rooms) {
                    System.out.println(room.getName());
                }
                while (true) {
                    System.out.printf("Room choice: ");
                    user = in.nextLine().toUpperCase();
                    boolean moved = updateRoom(user);
                    if (moved) {
                        break;
                    }
                }


            } else if (user.equalsIgnoreCase("restart")) {
                while (true) {
                    System.out.printf("Are you sure you want to restart, all progress will be deleted: Y/N: ");
                    user = in.nextLine();
                    int choice = yesOrNo(user);
                    if (choice == 1) {
                        restart();
                        System.exit(0);
                    } else if (choice == 0) {
                        System.out.println("Returning to game.");
                        break;
                    }
                }

            } else if (user.equalsIgnoreCase("exit")) {
                while (true) {
                    System.out.printf("Are you sure you want to exit: Y/N: ");
                    user = in.nextLine();
                    int choice = yesOrNo(user);
                    if (choice == 1) {
                        saveGame(true);
                        System.out.println("Quitting...");
                        System.exit(0);
                    } else if (choice == 0) {
                        System.out.println("Returning to game.");
                        break;
                    }
                }

            } else if (user.equalsIgnoreCase("save")) {
                saveGame(true);

            } else if (user.equalsIgnoreCase("hello?")) {
                System.out.println("Hi :)");

            } else if (user.equals("tutorial")) {
                tutorial = true;
            }
        }
    }

    static int yesOrNo(String user) {
        user = user.toUpperCase();
        if (user.equals("Y")) {
            return 1;
        } else if (user.equals("N")) {
            return 0;
        } else {
            System.out.println("Invalid choice.");
            return -1;
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
                tutorial = Boolean.parseBoolean(reader.nextLine());
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
            writer.write(tutorial + "\n");
            if (printSave) {
                System.out.println("Game saved successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error saving the game.");
            e.printStackTrace();
        }
    }

    static boolean updateRoom(String newRoom) {
        Room from = gambler.inRoom;
        for (Room room : rooms) {
            if (room.name.equalsIgnoreCase(newRoom)) {
                if (room.locked) {
                    System.out.println("Room is locked!");
                    return true;
                }
                if (from == room) {
                    System.out.println("Staying in: " + room.getName());
                    return true;
                }
                gambler.inRoom = room;
                System.out.println("Moved to: " + room.name);
                return true;
            }
        }
        System.out.println("Room not found.");
        return false;
    }
}