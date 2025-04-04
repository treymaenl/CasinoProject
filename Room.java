/**
* The Room class represents a location in the game
* Each room has a name, an optional entry text, help text,
* and states indicating whether it is locked or has been visited
*
* Features:
*     Multiple constructors for different scenarios
*     Methods to get and set room attributes
*     Track whether the room has already been visited
*     Provide help text and entry text
* 
* @author Group A
* @version 1.0
*/
public class Room {
    String name;
    String entryText;
    String helpText;
    boolean locked;
    boolean visited;

    /**
    * Constructs a room with a given name
    * The room starts unlocked and unvisited
    *
    * @param name The name of the room
    */
    Room(String name) {
        this.name = name;
        this.locked = false;
        this.visited = false;
    }

    /**
    * Constructs a room with a given name and lock status
    * The rooms starts unvisited
    * 
    * @param name The name of the room
    * @param locked Lock status of the room
    */
    Room(String name, boolean locked) {
        this.name = name;
        this.locked = locked;
        this.visited = false;
    }

    /**
     * Constructs a room with a given name, lock status, and visit status
     * 
     * @param name The name of the room
     * @param locked Lock status of the room
     * @param visited Visit status of the room
     */
    Room(String name, boolean locked, boolean visited) {
        this.name = name;
        this.locked = locked;
        this.visited = visited;
    }

    /**
     * Constructs a room with a name, entry text, and help text
     * The room starts as unlocked and unvisited
     * 
     * @param name The name of the room
     * @param entryText The text displayed when entering the room for the first time
     * @param helpText The help text describing the room
     */
    Room(String name, String entryText, String helpText) {
        this.name = name;
        this.entryText = entryText;
        this.helpText = helpText;
        this.locked = false;
        this.visited = false;
    }

    /**
     * Checks if the room is locked
     * 
     * @return true if the room is locked, otherwise false
     */
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * Sets the lock status of the room
     * 
     * @param locked true to lock the room, false to unlock it
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Retrieves the help text for the room
     * 
     * @return The help text
     */
    public String getHelp() {
        return helpText;
    }

    /**
     * Retrieves the name of the room
     * 
     * @return The room's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the help text for the room
     * 
     * @param text The new help text
     */
    public void setHelpText(String text) {
        this.helpText = text;
    }

    /**
     * Handles room entry. If the room is being entered for the first time, 
     * it returns the entry text; otherwise, it returns an empty string
     * 
     * @return The entry text if entering for the first time, otherwise an empty string
     */
    public String enter() {
        if (!this.visited) {
            return this.entryText;
        } else {
            return "";
        }
    }

    /**
     * Sets the entry text for the room
     * 
     * @param entryText The new entry text
     */
    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    /**
     * Checks if the room has been visited
     * 
     * @return true if the room has been visited, otherwise false
     */
    public boolean visited() {
        return this.visited;
    }

    /**
     * Sets the visit status of the room
     * 
     * @param visited true if the room has been visited, false otherwise
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

}
