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

    Room(String name, boolean locked, String helpText) {
        this.name = name;
        this.locked = locked;
        this.helpText = helpText;
    }

    boolean isLocked() {
        return this.locked;
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

    public void setHelpText(String text) {
        this.helpText = text;
    }

    public String enter() {
        return this.entryText;
    }

}
