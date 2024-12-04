package Main;
public class Option {
    private final String description;
    private boolean selected = false;

    public Option(String description, Boolean selected) {
        this.description = description;
        this.selected = selected;
    }

    public String description(){
        return description;
    }

    public boolean isSelected() {
        return selected;
    }

    public void select() {
        selected = true;
    }
    public void unselect() {
        selected = false;
    }
}
