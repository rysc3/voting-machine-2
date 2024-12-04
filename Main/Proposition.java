package Main;
import java.util.ArrayList;

public record Proposition (String propName, String propDesc, int selectableOptions, ArrayList<Option> options) {}
