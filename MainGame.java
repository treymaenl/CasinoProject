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
                                    "me" - show your information
                                    "exit" - prompt you to exit the game
                                    "restart" - prompt you to restart the game
                                    "move" - prompt you to move rooms
                                    "save" - save the game
                                    "room help" - show room specific help
                                    "all help" - show all command options""";

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
                System.out.println("This is a text based game, so prepare to read!");
            } else {
                System.out.println("Save file found. Loading game...");
                loadGame();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while handling the save file.");
            e.printStackTrace();
        }

        while (true) {
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
                System.out.println("Very well then!\nTUTORIAL\n" +
                                    "Here are some important commands that you will be using to play the game:");
                System.out.println(generalHelp);
                System.out.println("You can see commands at any point by typing \"help\" or \"?\"");
                System.out.printf("Type anything to continue: ");
                user = in.nextLine();
                System.out.println("\n\nIn a moment you will see your name appear in the console, it will appear as \n\nYOURNAME:\n\n" +
                                    "When this is on the screen you are able to type any command to play.");
                while (true) {
                    System.out.printf("Ready to continue to the game? y/n: ");
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

            System.out.printf("\n" + gambler.getName().toUpperCase() + ": ");
            user = in.nextLine();

            if (user.equals("?") || user.equalsIgnoreCase("help")) {
                System.out.println("General help:");
                System.out.println(generalHelp);

            } else if (user.equalsIgnoreCase("room help")) {
                System.out.println(gambler.inRoom.getName() + " help:");
                System.out.println(gambler.inRoom.getHelp());

            } else if (user.equalsIgnoreCase("all help")) {
                System.out.println();

            } else if (user.length() >= 4 && user.substring(0,4).equalsIgnoreCase("move")) {
                String moveTo = "";
                if (user.length() > 4) {
                    moveTo = user.substring(5, user.length());
                } else {
                    System.out.println("\nWhich room would you like to move to?");
                    for (Room room : rooms) {
                        System.out.println("\t" + room.getName());
                    }
                }

                int i = 0;
                while (true) {
                    if (moveTo == "" || i > 0) {
                        System.out.printf("\nRoom choice: ");
                        user = in.nextLine().toUpperCase();
                    } else {
                        user = moveTo;
                        System.out.println();
                    }
                    i++;
                    boolean moved = updateRoom(user);
                    if (moved) {
                        break;
                    }
                }


            } else if (user.equalsIgnoreCase("restart")) {
                while (true) {
                    System.out.printf("Are you sure you want to restart, all progress will be deleted: y/n: ");
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
                    System.out.printf("Are you sure you want to exit: y/n: ");
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

            } else if (user.equalsIgnoreCase("me")) {
                System.out.println("Name: " + gambler.getName() + 
                                "\nBalance: $" + gambler.getBalance() +
                                "\nIn room: " + gambler.inRoom.getName());

            } else if (user.equals("BigMoneyCheddar")) {
                gambler.updateBalance(10000000);
                System.out.println("nice.");

            } else {
                System.out.println("Unrecognized command.");
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