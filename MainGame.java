import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * MainGame is the text based game loop.
 * It handles player initialization, room transitions, interactions, save/load logic, game commands, and the main loop.
 * 
 * Features include:
 * - File-driven room loading
 * - Persistent save/load system
 * - Multiple game types (Slots, Roulette, BlackJack)
 * - VIP room perks and unlockable content
 * - Golden Donut tracking and usage
 * 
 * This class also maintains global game state, including the player object,
 * room list, and tutorial tracking.
 */

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

    /**
     * Initializes the game by reading room data from a file.
     * 
     * @throws FileNotFoundException if the Rooms.txt file is not found
     */
    private static void setupGame() throws FileNotFoundException {
        File roomData = new File("Rooms.txt");
        if (!roomData.exists()) {
            throw new FileNotFoundException("Rooms.txt file not found! Please find and download it.");
        }
        roomFromFile(roomData);
    }

    /**
     * Reads room data from a text file and populates the rooms list.
     * File must contain specific markers: <r>, <e>, <h>, </h>
     * 
     * @param roomData the file containing room definitions
     */
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

    /**
     * Deletes the save file if it's empty or incorrectly formatted.
     * 
     * @param saveFile the save file to validate
     * @throws FileNotFoundException if file read fails
     */
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
                if (lineCount != 6) {
                    saveFile.delete();
                    System.out.println("Bad save file deleted");
                }
            }
        }
    }

    /**
     * Starts a new game or loads a saved one depending on the existence of the save file.
     * 
     * @param saveFile the file storing saved game state
     * @param in the Scanner object for user input
     */
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

    /**
     * Presents a tutorial for new players if requested.
     * 
     * @param in the Scanner object for user input
     */
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
        // Tutorial text
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

    /**
     * Main entry point for the game. Initializes and starts the main loop.
     * 
     * @param args command line arguments (not used)
     * @throws FileNotFoundException if Rooms.txt is missing
     */
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

    /**
     * Processes and executes a player command.
     * 
     * @param user the command entered by the player
     * @param in the Scanner object for user input
     */
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
            talk(in);

        // mocking command
        } else if (user.equals("Huh?")) {
            System.out.println("\nBARTENDER: Are we gonna have a problem?");

        // games command
        } else if (!gambler.inRoom.getName().equalsIgnoreCase("Bar") && user.equalsIgnoreCase("games")) {
            games(in);

        // VIP bartender talk command
        } else if (gambler.inRoom.getName().equalsIgnoreCase("VIP") && user.equalsIgnoreCase("talk")) {
            talkVIP(in);

        // VIP order command
        } else if (gambler.inRoom.getName().equalsIgnoreCase("VIP") && user.equalsIgnoreCase("order")) {
            orderVIP(in);

        // invalid command
        } else {
            if (gambler.inRoom != rooms.get(1)) {
                System.out.println("\nBARTENDER: Huh?");
            }
            System.out.println("Unrecognized Command");
        }

    }

    /**
     * Converts user yes/no input into a response code.
     * 
     * @param user the raw input string
     * @return 1 for yes, 0 for no, -1 for invalid
     */
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

    /**
     * Prompts the user to confirm quitting and saves before exiting.
     * 
     * @param in the Scanner object for user input
     */
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

    /**
     * Prompts the user to confirm restarting and deletes the save file.
     * 
     * @param in the Scanner object for user input
     */
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

    /**
     * Loads game data from the save file.
     */
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
                // VIP status
                rooms.get(2).setLocked(Boolean.parseBoolean(reader.nextLine()));
                // Tutorial status
                tutorial = Boolean.parseBoolean(reader.nextLine());
                // Golden donuts
                gambler.setDonutCount(Integer.parseInt(reader.nextLine()));

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

    /**
     * Saves the current game state to a file.
     * 
     * @param printSave whether to print confirmation to the console
     */
    static void saveGame(boolean printSave) {
        try (FileWriter writer = new FileWriter("save.txt")) {
            // Name
            writer.write(gambler.getName() + "\n");
            // Balance
            writer.write(gambler.getBalance() + "\n");
            // Room
            writer.write(gambler.inRoom.getName() + "\n");
            // VIP status
            writer.write(rooms.get(2).locked + "\n");
            // Tutorial status
            writer.write(tutorial + "\n");
            // Golden donut count
            writer.write(gambler.getDonutCount() + "\n");

            // Print save to terminal if wanted
            if (printSave) {
                System.out.println("Game saved successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error saving the game.");
            e.printStackTrace();
        }
    }

    /**
     * Updates the player's current room.
     * 
     * @param newRoom the name of the room to move to
     * @return true if the move is successful or redundant; false if room is invalid
     */
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

    /**
     * Prints general command help to the console.
     */
    private static void help() {
        System.out.println("General help:");
        System.out.println(generalHelp);
    }

    /**
     * Displays help specific to the current room.
     */
    private static void room() {
        System.out.println("\n" + gambler.inRoom.getName() + " options:");
        System.out.println(gambler.inRoom.getHelp());
    }

    /**
     * Handles the "move" command and room selection.
     * 
     * @param user the initial move command input
     * @param in the Scanner object for user input
     */
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
        saveGame(false);
    }

    /**
     * Displays the player's current information, including balance and VIP status.
     */
    private static void me() {
        System.out.println("\nYOUR INFORMATION:" +
                        "\n\s\sName: " + gambler.getName() + 
                        "\n\s\sBalance: $" + gambler.getBalance() +
                        "\n\s\sIn room: " + gambler.inRoom.getName() +
                        "\n\s\sGolden Donuts: " + gambler.getDonutCount());
        if (!rooms.get(2).isLocked()) {
            System.out.println("\s\sVIP Member!");
        } else {
            System.out.println("\s\sStandard Customer");
        }
    }

    /**
     * Handles the dialogue interaction with the bartender in the normal bar.
     * Offers VIP access if criteria are met.
     * 
     * @param in the Scanner object for user input
     */
    private static void talk(Scanner in) {
        System.out.printf("\nBARTENDER: ");
    
        // VIP room offer logic
        if (!gambler.isVIP() && gambler.getBalance() >= 10000) {
            if (!gambler.offeredVIP) {
                System.out.println("You’ve been doing pretty well in the Game Room. Ever thought about the VIP Lounge?");
                System.out.println("  1. VIP?");
                System.out.println("  2. Leave");
                System.out.print("Choice: ");
                String choice = in.nextLine().trim();

                while (!choice.equals("1") && !choice.equals("2")) {
                    System.out.println("Invalid choice. Choose \"1\" or \"2\".");
                    System.out.print("Choice: ");
                    choice = in.nextLine().trim();
                }

                if (choice.equals("1")) {
                    System.out.println("\nINFO: VIP Lounge grants you perks like larger bets and the ability to spin multiple slots at once.");
                    System.out.println("\nBARTENDER: Access costs $10,000.");
                    System.out.println("\nCurrent Balance: $" + gambler.getBalance());
                    System.out.print("\nWould you like to purchase VIP access? (y/n): ");
                    String confirm = in.nextLine().trim().toLowerCase();
                    if (confirm.equals("y")) {
                        gambler.updateBalance(-10000);
                        rooms.get(2).setLocked(false);
                        System.out.println("Welcome to the VIP club!");
                    } else {
                        System.out.println("\n BARTENDER: No problem. Let me know if you change your mind.");
                        gambler.offeredVIP = true;
                    }
                    return;
                } else {
                    System.out.println("Alright. Come back if you're interested.");
                    return;
                }
            } else {
                System.out.println("VIP access is still available for $10,000. Would you like to buy it? (y/n): ");
                String confirm = in.nextLine().trim().toLowerCase();
                if (confirm.equals("y")) {
                    gambler.updateBalance(-10000);
                    rooms.get(2).setLocked(false);
                    System.out.println("Welcome to the VIP club!");
                    saveGame(true);
                } else {
                    System.out.println("Alright. Let me know if you change your mind.");
                }
                return;
            }
        }
    
        // Default bartender talk if no VIP logic applies
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

    /**
     * Displays the bar menu and processes item purchases in the normal bar.
     * 
     * @param in the Scanner object for user input
     */
    private static void orderBar(Scanner in) {
        System.out.println("\nBARTENDER: Here's the menu. Don't waste my time.");
    
        System.out.println("""
            BAR MENU:
              Drinks:
                1. Dasani - $25
                2. Tropical Smoothie - $100
                3. Purified Mercury - $1000
              Food:
                4. Slop - $150
                5. Crustless Greek Zucchini Pie - $750
                6. Golden Donut - $3500
            """);
    
        while(true) {
            System.out.print("Your choice (1-6 or \"exit\"): ");
            String choice = in.nextLine().trim().toLowerCase();
        
            switch (choice) {
                case "1" -> {
                    gambler.updateBalance(-25);
                    System.out.println("Dasani purchased! You're thirstier than before." +
                                        "\nNew Balance: $" + gambler.getBalance());
                    return;
                }
                case "2" -> {
                    gambler.updateBalance(-100);
                    System.out.println("Tropical Smoothie purchased! Somewhat chunky, but refreshing." +
                                        "\nNew Balance: $" + gambler.getBalance());
                    return;
                }
                case "3" -> {
                    gambler.updateBalance(-1000);
                    System.out.println("Purified Mercury purchased! The euphoria of spending $1000 masks the taste." +
                                        "\nNew Balance: $" + gambler.getBalance());
                    return;
                }
                case "4" -> {
                    gambler.updateBalance(-150);
                    System.out.println("Slop purchased! You feel slightly nauseous." + 
                                        "\nNew Balance: $" + gambler.getBalance());
                    return;
                }
                case "5" -> {
                    gambler.updateBalance(-750);
                    System.out.println("Crustless Greek Zucchini Pie purchased! Fancy, but you have a minor zucchini allergy." +
                                        "\nNew Balance: $" + gambler.getBalance());
                    return;
                }
                case "6" -> {
                    gambler.updateBalance(-3500);
                    System.out.println("Golden Donut purchased! Too hard to eat, but it impresses everyone." +
                                        "\nNew Balance: $" + gambler.getBalance());
                    gambler.addDonut();
                    System.out.println("You now have " + gambler.getDonutCount() + " Golden Donuts.");
                    return;
                }
                case "exit" -> {
                    System.out.println("\nBARTENDER: Thanks for wasting my time.");
                    return;
                } 
                default -> System.out.println("BARTENDER: Can't even read a menu? Try again.");
            }
        }
    }    

    /**
     * Handles user game selection (Slots, Roulette, BlackJack).
     * 
     * @param in the Scanner object for user input
     */
    private static void games(Scanner in) {
        System.out.println("""
                Choose your game:
                  1. Slots
                  2. Roulette
                  3. BlackJack
                """);
            System.out.print("Enter choice: ");
            String choice = in.nextLine().trim();
            while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
                System.out.println("Invalid choice. Choose \"1\", \"2\", \"3\", or \"exit\".");
                System.out.print("Enter choice: ");
                choice = in.nextLine().trim();
                if (choice.equalsIgnoreCase("exit")) {
                    return;
                }
            }
            switch (choice) {
                case "1" -> new Slots().play(gambler);
                case "2" -> new Roulette().play(gambler);
                case "3" -> new BlackJack().play(gambler, in);
                default -> System.out.println("Invalid choice.");
            }
    }

    /**
     * Handles dialogue interaction with the VIP bartender.
     * 
     * @param in the Scanner object for user input
     */
    private static void talkVIP(Scanner in) {
        System.out.println("\nVIP BARTENDER: Welcome back, boss.");
        
        Random rand = new SecureRandom();
        String[] vipLines = {
            "Another winning streak, I see.",
            "Your usual drink is ready on the rocks.",
            "Let me know if the slot machines are treating you right.",
            "They say the house always wins... I guess you're the house."
        };
    
        int index = rand.nextInt(vipLines.length);
        System.out.println(vipLines[index]);
    }

    /**
     * Displays the VIP menu and processes item purchases.
     * 
     * @param in the Scanner object for user input
     */
    private static void orderVIP(Scanner in) {
        System.out.println("\nVIP BARTENDER: Here's our exclusive menu, boss.");
    
        System.out.println("""
            VIP MENU:
              Drinks:
                1. Aged Scotch - $500
                2. Liquid Gold Martini - $2000
              Food:
                3. Filet Mignon Bites - $3000
                4. Caviar & Chips - $5000
            """);
    
        System.out.print("Your choice (1-4 or \"exit\"): ");
        String choice = in.nextLine().trim().toLowerCase();
    
        switch (choice) {
            case "1" -> {
                gambler.updateBalance(-500);
                System.out.println("You sip a deep, smoky Aged Scotch. Refined. New Balance: $" + gambler.getBalance());
            }
            case "2" -> {
                gambler.updateBalance(-2000);
                System.out.println("The Liquid Gold Martini glows faintly. You're not sure it's legal. New Balance: $" + gambler.getBalance());
            }
            case "3" -> {
                gambler.updateBalance(-3000);
                System.out.println("The Filet Mignon Bites melt in your mouth. Luxury. New Balance: $" + gambler.getBalance());
            }
            case "4" -> {
                gambler.updateBalance(-5000);
                System.out.println("You crunch the finest chips known to man, dipped in caviar. Power. New Balance: $" + gambler.getBalance());
            }
            case "exit" -> System.out.println("VIP BARTENDER: As you wish.");
            default -> System.out.println("VIP BARTENDER: I don't serve indecisiveness. Try again.");
        }
    }

}