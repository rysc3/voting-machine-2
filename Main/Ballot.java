package Main;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public record Ballot (String electionName, LocalDate startDate, LocalDate endDate, LocalTime startForDay, LocalTime endForDay,
                      ArrayList<Proposition> propositions) {}