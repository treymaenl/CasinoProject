public class Room {
    String name;
    String entryText;
    String helpText;
    boolean locked;

    Room(String name) {
        this.name = name;
        this.locked = false;
    }

    Room(String name, boolean locked) {
        this.name = name;
        this.locked = locked;
    }

    void setLocked(boolean locked) {
        this.locked = locked;
    }

    String getHelp() {
        return helpText;
    }

    public String getName() {
        return this.name;
    }
}
