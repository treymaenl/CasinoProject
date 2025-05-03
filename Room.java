/**
 * Represents a room in the game, including its name, whether it is locked, help text,
 * and if the player has visited it.
 */
public class Room {
    // The name of the room
    String name;

    // The text displayed when entering the room
    String entryText;

    // Help instructions specific to the room
    String helpText;

    // Flag indicating if the room is locked
    boolean locked;

    // Flag indicating if the player has visited the room before
    boolean visited;

    /**
     * Constructs a room with a name, unlocked and unvisited by default.
     *
     * @param name The name of the room.
     */
    Room(String name) {
        this.name = name;
        this.locked = false;
        this.visited = false;
    }

    /**
     * Constructs a room with a name and locked status.
     *
     * @param name The name of the room.
     * @param locked Whether the room is initially locked.
     */
    Room(String name, boolean locked) {
        this.name = name;
        this.locked = locked;
        this.visited = false;
    }

    /**
     * Constructs a room with name, locked, and visited status.
     *
     * @param name The name of the room.
     * @param locked Whether the room is locked.
     * @param visited Whether the room has been visited.
     */
    Room(String name, boolean locked, boolean visited) {
        this.name = name;
        this.locked = locked;
        this.visited = visited;
    }

    /**
     * Constructs a room with full descriptive fields.
     *
     * @param name The name of the room.
     * @param entryText Text shown upon first entering.
     * @param helpText Help instructions for this room.
     */
    Room(String name, String entryText, String helpText) {
        this.name = name;
        this.entryText = entryText;
        this.helpText = helpText;
        this.locked = false;
        this.visited = false;
    }

    /**
     * Checks if the room is locked.
     *
     * @return true if locked, false otherwise.
     */
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * Locks or unlocks the room.
     *
     * @param locked true to lock, false to unlock.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Returns the help text associated with the room.
     *
     * @return The help text.
     */
    public String getHelp() {
        return helpText;
    }

    /**
     * Returns the name of the room.
     *
     * @return The room's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Updates the help text of the room.
     *
     * @param text The new help text.
     */
    public void setHelpText(String text) {
        this.helpText = text;
    }

    /**
     * Returns the entry text if the room hasn't been visited.
     *
     * @return The entry text on first visit, otherwise an empty string.
     */
    public String enter() {
        if (!this.visited) {
            return this.entryText;
        } else {
            return "";
        }
    }

    /**
     * Sets the entry text for the room.
     *
     * @param entryText The new entry text.
     */
    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    /**
     * Checks whether the room has been visited.
     *
     * @return true if visited, false otherwise.
     */
    public boolean visited() {
        return this.visited;
    }

    /**
     * Updates the visited status of the room.
     *
     * @param visited true if visited, false otherwise.
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
