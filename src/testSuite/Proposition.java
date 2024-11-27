package testSuite;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Proposition {
    private final String name;
    private final String description;
    private final int maxNumSelections;
    private final String[] options;
    private boolean[] selections;
    private final String[] navOptions;


    public Proposition(String name, String description, int maxNumSelections, String[] options, String[] navOptions) {
        this.name = name;
        this.description = description;
        this.maxNumSelections = maxNumSelections;
        this.options = options;
        if (this.options != null){
            this.selections = new boolean[options.length];
        } else {
            this.selections = null;
        }
        this.navOptions = navOptions;
    }

    public String getName() {
        return this.name;
    }

    public String[] getNavOptions() {
        return this.navOptions;
    }

    public int getMaxNumSelections() {
        return this.maxNumSelections;
    }

    public String getDescription() {
        return this.description;
    }

    public String[] getOptions() {
        return this.options;
    }

    public boolean[] getSelections() {
        return this.selections;
    }

    public void setSelection(int index, boolean newValue) {
        try {

            this.selections[index] = newValue;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: Index " + index + " is out of bounds for selections array.");
        }
    }

    public void setSelections(boolean[] selections) {
        this.selections = selections;
    }


    public int getNumCurrentSelections(){
        int i = 0;
        for(Boolean b : selections){
            if (b){
                i++;
            }
        }
        return i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        sb.append(description).append("\n");
        sb.append(maxNumSelections).append("\n");
        for (int i = 0; i < options.length; i++) {
            sb.append(options[i]).append("-------").append(selections[i]).append("\n");
        }
        return sb.toString();
    }
}