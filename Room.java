public class Room {
    String name;
    String entryText;
    String helpText;
    boolean locked;
    boolean visited;

    Room(String name) {
        this.name = name;
        this.locked = false;
        this.visited = false;
    }

    Room(String name, boolean locked) {
        this.name = name;
        this.locked = locked;
        this.visited = false;
    }

    Room(String name, boolean locked, boolean visited) {
        this.name = name;
        this.locked = locked;
        this.visited = visited;
    }

    Room(String name, String entryText, String helpText) {
        this.name = name;
        this.entryText = entryText;
        this.helpText = helpText;
        this.locked = false;
        this.visited = false;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getHelp() {
        return helpText;
    }

    public String getName() {
        return this.name;
    }

    public void setHelpText(String text) {
        this.helpText = text;
    }

    public String enter() {
        if (!this.visited) {
            return this.entryText;
        } else {
            return "";
        }
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public boolean visited() {
        return this.visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

}
