import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MainGame {
    static Player gambler;
    public static ArrayList<Room> rooms = new ArrayList<>();
    static boolean tutorial = false;
    static String generalHelp = """
                                    \s\s"room" - show room specific options
                                    \s\s"me" - show your information
                                    \s\s"quit" - prompt you to quit the game
                                    \s\s"restart" - prompt you to restart the game
                                    \s\s"move" - prompt you to move rooms
                                    \s\s"save" - save the game
                                    \s\s"all" - show all command options""";
    static String allHelp = """
                                    \s\s"room" - show room specific options
                                    \s\s"me" - show your information
                                    \s\s"quit" - prompt you to quit the game
                                    \s\s"restart" - prompt you to restart the game
                                    \s\s"move" - prompt you to move rooms
                                    \s\s"save" - save the game
                                    \s\s"all" - show all command options
                                    \s\s"tutorial" - go through tutorial again""";

    static String[] bartenderStandard = {"Looking a little rough.", "Do I know you?", 
                                        "Just order something and get out of here,  would ya?", 
                                        "Shouldn't you be gambling or eating slop?", 
                                        "There was a time in my life that I felt like it wasn't going to work out for me. You know that feeling? Like nothing is going your way? Whatever, as fate would have it I ended up where I belonged. Don't tell anyone, but I sneak a whole bottle of burboun each time we get our new stock in, haha. Tell anyone and you're dead.",
                                        "AH"};
    static String[] bartenderVIP = {"Looking good!", "I knew the second you walked in that you'd beat the house!", "Way to go today boss!"};
    static int barLastTalk = -1;

    // Some important information to have to start the game
    private static void setupGame() throws FileNotFoundException {
        File roomData = new File("Rooms.txt");
        if (!roomData.exists()) {
            throw new FileNotFoundException("Rooms.txt file not found! Please find and download it.");
        }
        roomFromFile(roomData);
    }

    // Creates rooms based on textfile, can add more rooms easier
    private static void roomFromFile(File roomData) {
        try (Scanner reader = new Scanner(roomData)) {
            // boolean for room name (r), entry text (e), and help text (h)
            boolean r = false, e = false, h = false;
            // strings for room construction
            String roomName = "", entryText = "", helpText = "";

            // reading file
            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                // end signifies end of file
                if (line.equals("end")) {
                    System.out.println("Startup succesful...");
                    break;
                }

                // handling empty lines and markers
                String marker = "";
                if (line.trim().isBlank()) {
                    continue;
                } else if (line.length() >= 3) {
                    marker = line.substring(0, 3);
                } else {
                    // all lines with the file should be at least 3 characters long or blank
                    throw new Exception("Issue with Rooms.txt");
                }

                // room name marker
                if (marker.equals("<r>")) {
                    roomName = line.substring(3, line.length());
                    r = true;

                // entry text marker
                } else if (marker.equals("<e>")) {
                    entryText = line.substring(3, line.length());
                    e = true;

                // help text marker
                } else if (marker.equals("<h>")) {
                    StringBuilder roomHelp = new StringBuilder("");

                    // getting one or multiple lines of help text
                    while (true) {
                        line = reader.nextLine();

                        // help text end marker
                        if (line.equals("</h>")) {
                            break;
                        }
                        roomHelp.append("  " + line + "\n");
                    }

                    // deleting last newline character
                    roomHelp.deleteCharAt(roomHelp.length() - 1);

                    // toString
                    helpText = roomHelp.toString();
                    h = true;
                }

                // Once all info filled, creates rooms using information
                // File must be correctly formatted for this to work properly
                if (r & e & h) {
                    Room currentRoom = new Room(roomName, entryText, helpText);
                    rooms.add(currentRoom);
                    r = e = h = false;
                }
            }

            // Closing reader
            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // Setting VIP to locked
        rooms.get(2).setLocked(true);
    }

    public static void deleteBadSave(File saveFile) throws FileNotFoundException {
        // deleting bad saves
        if (saveFile.exists()) {

            // Nothing in file
            if (saveFile.length() == 0) {
                saveFile.delete();
                System.out.println("Bad save file deleted");

            // Checking for proper line counts
            } else {
                int lineCount = 0;
                Scanner reader = new Scanner(saveFile);

                // Counting lines
                while (reader.hasNextLine()) {
                    lineCount++;
                    reader.nextLine();
                }

                reader.close();

                // Proper save file should only be 4 lines
                if (lineCount != 5) {
                    saveFile.delete();
                    System.out.println("Bad save file deleted");
                }
            }
        }
    }

    public static void start(File saveFile, Scanner in) {
        // Starting or loading game
        try {
            // No prior save
            if (!saveFile.exists()) {
                // Give tutorial option
                tutorial = true;

                // Get name
                System.out.print("Enter your name: ");
                String playerName = in.nextLine();

                // Limit player name to 16 characters
                if (playerName.length() > 16) {
                    playerName = playerName.substring(0, 16);
                }

                // Welcome player
                System.out.println("Welcome, " + playerName + "!");

                // Create player object
                gambler = new Player(playerName);

                // Put player in bar
                gambler.inRoom = rooms.get(0);

                // Give a little tip
                System.out.println("For help with commands or options for your current room, simply type \"help\" or \"?\" at anytime!");

                // Create new save file and immediately put relevant information into it to avoid future problems
                saveFile.createNewFile();
                saveGame(false);

                // Game time
                System.out.println("This is a text based game, so prepare to read!");

            // Loading game from save file
            } else {
                System.out.println("Save file found. Loading game...");
                loadGame();
            }

        } catch (IOException e) {
            System.out.println("An error occurred while handling the save file.");
            e.printStackTrace();
        }
    }

    public static void tutorial(Scanner in) {
        String user;
        while (tutorial) { 
            System.out.printf("\nWould you like a tutorial? y/n: ");
            user = in.nextLine();
            int choice = yesOrNo(user);
            if (choice == 1) {
                break;
            } else if (choice == 0) {
                System.out.println("\nNo tutorial it is then! Type \"?\" or \"help\" if you get stuck!");
                tutorial = false;
                break;
            }
        }
        if (tutorial) {
            System.out.println("Very well then!\nTUTORIAL\n" +
                                "Here are some important commands that you will be using to play the game:");
            System.out.println(generalHelp);
            System.out.println("You can see commands at any point by typing \"help\" or \"?\"");
            System.out.printf("Hit ENTER to continue: ");
            user = in.nextLine();
            System.out.println("\n\nIn a moment you will see your name appear in the console, it will appear as \n\nYOURNAME:\n\n" +
                                "When your name is on the screen you are able to type any command to play.");
            while (true) {
                System.out.printf("Hit ENTER to continue to game: ");
                user = in.nextLine();
                System.out.println("\n\nGreat let's begin!");
                System.out.println(gambler.inRoom.enter());
                break;
            }
            tutorial = false;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        // looking for existing save file
        File saveFile = new File("save.txt");
        Scanner in = new Scanner(System.in);

        try {
            setupGame();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        deleteBadSave(saveFile);

        start(saveFile, in);

        while (true) {
            String user;

            if (tutorial) {
                tutorial(in);
            }

            System.out.printf("\n" + gambler.getName().toUpperCase() + ": ");
            user = in.nextLine().trim();

            handleCommand(user, in);
        }
    }

    public static void handleCommand(String user, Scanner in) {
        // help command
        if (user.equals("?") || user.equalsIgnoreCase("help")) {
            help();

        // room command
        } else if (user.equalsIgnoreCase("room")) {
            room();

        // all command
        } else if (user.equalsIgnoreCase("all")) {
            System.out.println(allHelp);

        // move command
        } else if (user.length() >= 4 && user.substring(0,4).equalsIgnoreCase("move")) {
            move(user, in);

        // restart command
        } else if (user.equalsIgnoreCase("restart")) {
            restart(in);

        // quit command
        } else if (user.equalsIgnoreCase("quit")) {
            quit(in);

        // save command
        } else if (user.equalsIgnoreCase("save")) {
            saveGame(true);

        // easter egg command
        } else if (user.equalsIgnoreCase("hello?")) {
            System.out.println("Hi :)");

        // tutorial command
        } else if (user.equals("tutorial")) {
            tutorial = true;

        // me command
        } else if (user.equalsIgnoreCase("me")) {
            me();

        // give lots of money command
        } else if (user.equals("BigMoneyCheddar")) {
            gambler.updateBalance(10000000);
            System.out.println("nice.");

        // bar order command
        } else if (gambler.inRoom == rooms.get(0) && user.equalsIgnoreCase("order")) {
            orderBar(in);

        // give vip command
        } else if (user.equalsIgnoreCase("GIVEMEVIP")) {
            rooms.get(2).setLocked(false);
            System.out.println("WOO! VIP!");

        // bartender talk command
        } else if (gambler.inRoom == rooms.get(0) && user.equalsIgnoreCase("talk")) {
            talk();

        // mocking command
        } else if (user.equals("Huh?")) {
            System.out.println("\nBARTENDER: Are we gonna have a problem?");

        // not valid command
        } else {
            if (gambler.inRoom == rooms.get(0)) {
                System.out.println("\nBARTENDER: Huh?");
            }
            System.out.println("Unrecognized Command");
        }

    }

    // for handling asking player yes or no
    // 1 for yes, 0 for no, -1 if neither
    static int yesOrNo(String user) {
        user = user.toUpperCase();
        
        // Handling "y" or "n"
        if (user.equals("Y")) {
            return 1;
        } else if (user.equals("N")) {
            return 0;
        } else {
            System.out.println("Invalid choice.");
            return -1;
        }
    }

    private static void quit(Scanner in) {
        String user;
        while (true) {
            System.out.printf("Are you sure you want to quit: y/n: ");
            user = in.nextLine();
            int choice = yesOrNo(user);
            if (choice == 1) {
                saveGame(true);
                System.out.println("Quitting...");
                in.close();
                System.exit(0);
            } else if (choice == 0) {
                System.out.println("Returning to game.");
                break;
            }
        }
    }

    // for restarting game
    static void restart(Scanner in) {
        File delete = new File("save.txt");
        String user;
        while (true) {
            System.out.printf("Are you sure you want to restart, all progress will be deleted: y/n: ");
            user = in.nextLine();
            int choice = yesOrNo(user);
            if (choice == 1) {
                System.out.println("Deleting old save...");
                delete.delete();
                System.out.println("Quitting...");
                in.close();
                System.exit(0);
            } else if (choice == 0) {
                System.out.println("Returning to game.");
                break;
            }
        }
    }

    // for loading game
    static void loadGame() {
        // Using save file to load game
        File saveFile = new File("save.txt");
        try (Scanner reader = new Scanner(saveFile)) {
            if (saveFile.exists()) {
                // Setting name, balance, room, VIP status, tutorial status
                gambler = new Player(reader.nextLine());
                gambler.setBalance(Integer.parseInt(reader.nextLine()));
                String roomName = reader.nextLine();
                for (Room room : rooms) {
                    if (room.getName().equals(roomName)) {
                        gambler.inRoom = room;
                    }
                }
                rooms.get(2).setLocked(Boolean.parseBoolean(reader.nextLine()));
                tutorial = Boolean.parseBoolean(reader.nextLine());

                // Succesful game load
                System.out.println("Game loaded. Welcome back, " + gambler.getName() + ".");
            } else {
                System.out.println("Save file not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading the game.");
            e.printStackTrace();
        }
    }

    // for saving game
    static void saveGame(boolean printSave) {
        try (FileWriter writer = new FileWriter("save.txt")) {
            // Name, balance, room, VIP status, tutorial status
            writer.write(gambler.getName() + "\n");
            writer.write(gambler.getBalance() + "\n");
            writer.write(gambler.inRoom.getName() + "\n");
            writer.write(rooms.get(2).locked + "\n");
            writer.write(tutorial + "\n");

            // Print save to terminal if wanted
            if (printSave) {
                System.out.println("Game saved successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error saving the game.");
            e.printStackTrace();
        }
    }

    // for moving rooms
    static boolean updateRoom(String newRoom) {
        Room from = gambler.inRoom;
        for (Room room : rooms) {
            // Look for room
            if (room.name.equalsIgnoreCase(newRoom)) {

                // Check if locked
                if (room.locked) {
                    System.out.println("Room is locked!");
                    return true;
                }

                // Check if it's the current room
                if (from == room) {
                    System.out.println("Staying in: " + room.getName());
                    return true;
                }

                // Moving room
                gambler.inRoom = room;
                System.out.println("Moved to: " + room.name);
                System.out.println(room.enter());
                return true;
            }
        }

        // Invalid room
        System.out.println("Room not found.");
        return false;
    }

    private static void help() {
        System.out.println("General help:");
        System.out.println(generalHelp);
    }

    private static void room() {
        System.out.println("\n" + gambler.inRoom.getName() + " options:");
        System.out.println(gambler.inRoom.getHelp());
    }

    private static void move(String user, Scanner in) {
        String moveTo = "";
            if (user.length() > 4) {
                moveTo = user.substring(4, user.length()).trim();
                System.out.println(moveTo);
            } else {
                System.out.println("\nWhich room would you like to move to?");
                for (Room room : rooms) {
                    System.out.println("\s\s" + room.getName());
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
                if (user.equalsIgnoreCase("game") || user.equalsIgnoreCase("gameroom")) {
                    user = "game room";
                }
                boolean moved = updateRoom(user);
                if (moved) {
                    break;
                } else {
                    System.out.println("\nWhich room would you like to move to?");
                    for (Room room : rooms) {
                        System.out.println("\t" + room.getName());
                    }
                }
            }
    }

    private static void me() {
        System.out.println("\nYOUR INFORMATION:" +
                        "\n\s\sName: " + gambler.getName() + 
                        "\n\s\sBalance: $" + gambler.getBalance() +
                        "\n\s\sIn room: " + gambler.inRoom.getName());
        if (!rooms.get(2).isLocked()) {
            System.out.println("\s\sVIP Member!");
        } else {
            System.out.println("\s\sStandard Customer");
        }
    }

    private static void talk() {
        System.out.printf("\nBARTENDER: ");

            Random random = new SecureRandom();
            int line = random.nextInt(bartenderStandard.length);
            while (line == barLastTalk) {
                line = random.nextInt(bartenderStandard.length);
            }

            if (line == 4) {
                int rare = 1;
                while (rare < 3) {
                    line = random.nextInt(bartenderStandard.length);
                    if (line == 4) {
                        rare++;
                    } else {
                        break;
                    }
                }
            }

            String bartender = bartenderStandard[line];
            System.out.println(bartender);
            barLastTalk = line;
    }

    private static void orderBar(Scanner in) {
        System.out.println("\nBARTENDER: What would you like?");
        System.out.println("""
                        Menu options:
                        \s\sDrinks
                        \s\sFood 
                        """);
            while (true) {
                System.out.printf("Menu: ");
                String menu = in.nextLine().trim();
                if (menu.equalsIgnoreCase("exit")) {
                    System.out.println("\nBARTENDER: Thanks for wasting my time.");
                    return;
                }
                int menuNum = 0;
                boolean valid = barMenu(menuNum, menu);
                if (!valid) {
                    continue;
                }
                System.out.println("Current Balance: $" + gambler.getBalance());
                boolean choosing = true;
                while (choosing) { 
                    choosing = barMenuOptions(menuNum, in);
                }
                return;
            }
    }

    private static boolean barMenu(int menuNum, String menu) {
        if (menu.equalsIgnoreCase("drinks")) {
            System.out.println("""
                            \s\sDRINKS:
                            \s\s1. "Dasani" - $25
                            \s\s2. "Tropical Smoothie" - $100
                            \s\s3. "Purified Mercury" - $1000
                            """);
            menuNum = 1;
        } else if (menu.equalsIgnoreCase("food")) {
            System.out.println("""
                            \s\sFOOD:
                            \s\s1. "Slop" - $150
                            \s\s2. "Crustless Greek Zucchini Pie" - $750
                            \s\s3. "Golden Donut" - $3500
                            """);
            menuNum = 2;
        } else {
            System.out.println("OPTIONS" +
                                "\n\s\s\"drinks\", \"food\", \"exit\"\n");
            return false;
        }

        return true;
    }

    private static boolean barMenuOptions(int menu, Scanner in) {
        System.out.printf("Order: ");
        String choice = in.nextLine().trim();
        if (choice.equalsIgnoreCase("exit")) {
            System.out.println("\nBARTENDER: Thanks for wasting my time.");
            return false;
        }
        System.out.println();
        switch(choice) {
            case "1":
                if (menu == 1) {
                    gambler.updateBalance(-25);
                    System.out.println("Dasani purchased!\nYou're thirstier than before.\nNew Balance: $" + gambler.getBalance());
                } else {
                    gambler.updateBalance(-150);
                    System.out.println("Slop purchased!\nYou feel slightly nauseous.\nNew Balance $" + gambler.getBalance());
                }
                return false;
            case "2":
                if (menu == 1) {
                    gambler.updateBalance(-100);
                    System.out.println("Tropical Smoothie purchased!\nIt is somewhat chunky, but refreshing!\nNew Balance: $" + gambler.getBalance());
                } else {
                    gambler.updateBalance(-750);
                    System.out.println("Crustless Greek Zucchini Pie purchased!\nVery facny, thankfully you only have a minor zucchini alergy!\nNew Balance $" + gambler.getBalance());
                }
                return false;
            case "3":
                if (menu == 1) {
                    gambler.updateBalance(-1000);
                    System.out.println("Purified Mercury purchased!\nThe pure euphoria of spending $1000 masks the horrible taste.\nNew Balance: $" + gambler.getBalance());
                } else {
                    gambler.updateBalance(-3500);
                    System.out.println("Golden Donut purchased!\nYou are unable to bite into the pure metal donut, but everyone envies you.\nNew Balance $" + gambler.getBalance());
                }
                return false;
            default:
                System.out.println("OPTIONS\n\s\s\"1\", \"2\", \"3\", \"exit\"\n");
                return true;
        }
    }

}