import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LeagueManager {
  private Player[] masterPlayers;
  private Set<Player> availablePlayers;
  private Set<Team> teams;
  private BufferedReader reader;
  private Map<String, String> menu;

  public LeagueManager(Player[] masterPlayers) {
    this.masterPlayers = masterPlayers;
    this.availablePlayers = new TreeSet<>(Arrays.asList(masterPlayers));
    this.teams = new TreeSet<>();
    this.reader = new BufferedReader(new InputStreamReader(System.in));
    this.menu = new LinkedHashMap<>();
    this.menu.put("create", "Create a new team");
    this.menu.put("add", "Add a player to a team");
    this.menu.put("remove", "Remove a player from a team");
    this.menu.put("roster", "Print a team roster");
    this.menu.put("height", "View a team height report");
    this.menu.put("quit", "Exit the program");
  }

  public static void main(String[] args) {
    Player[] players = Players.load();
    System.out.printf("There are currently %d registered players.%n", players.length);
    LeagueManager leagueManager = new LeagueManager(players);
    leagueManager.run();

  }

  public void run() {
    String choice = "";
    do {
      try {
        choice = promptAction();
        switch (choice) {
          case "create":
            createTeam();
            break;
          case "add":
            addPlayerToTeam();
            break;
          case "remove":
            removePlayerFromTeam();
            break;
          case "roster":
            printTeamRoster();
            break;
          case "height":
            printHeightReport();
            break;
          case "quit":
            System.out.println("Thanks for using LeagueManager!");
            break;
          default:
            System.out.printf("Unknown choice: '%s'. Try again.%n%n", choice);
        }
      } catch (IOException ioe) {
        System.out.println("Problem with input");
        ioe.printStackTrace();
      }
    } while (!choice.equals("quit"));
  }

  private String promptAction() throws IOException {
    System.out.printf("%nThere are %d teams created so far.  Your options are:%n", this.teams.size());
    for (Map.Entry<String, String> option : this.menu.entrySet()) {
      System.out.printf("%s - %s%n", option.getKey(), option.getValue());
    }
    System.out.print("What do you want to do: ");
    String choice = this.reader.readLine();
    return choice.trim().toLowerCase();
  }

  private void createTeam() throws IOException {
    System.out.print("Enter the team name: ");
    String teamName = reader.readLine().trim();

    System.out.print("Enter the coach name: ");
    String coachName = reader.readLine().trim();

    Team team = new Team(teamName, coachName);
    boolean added = teams.add(team);

    if (added) {
      System.out.printf(
              "Team '%s' was created with coach %s.%n",
              teamName,
              coachName
      );
    } else {
      System.out.printf(
              "A team named '%s' already exists.%n",
              teamName
      );
    }
  }

  private int promptForIndex(int numberOfOptions) throws IOException {
    while (true) {
      System.out.printf("Enter a number between 1 and %d: ", numberOfOptions);
      String input = reader.readLine().trim();

      try {
        int selection = Integer.parseInt(input);

        if (selection >= 1 && selection <= numberOfOptions) {
          return selection - 1;
        }

        System.out.println("That number is outside the available range.");
      } catch (NumberFormatException nfe) {
        System.out.println("Please enter a valid number.");
      }
    }
  }

  private Team chooseTeam() throws IOException {
    if (teams.isEmpty()) {
      System.out.println("No teams have been created yet.");
      return null;
    }

    List<Team> teamOptions = new ArrayList<>(teams);

    System.out.println("\nChoose a team:");

    for (int i = 0; i < teamOptions.size(); i++) {
      Team team = teamOptions.get(i);

      System.out.printf(
              "%d. %s — Coach: %s%n",
              i + 1,
              team.getTeamName(),
              team.getCoachName()
      );
    }

    int selectedIndex = promptForIndex(teamOptions.size());

    return teamOptions.get(selectedIndex);
  }

  private Player chooseAvailablePlayer() throws IOException {
    if (availablePlayers.isEmpty()) {
      System.out.println("There are no available players.");
      return null;
    }

    List<Player> playerOptions = new ArrayList<>(availablePlayers);

    System.out.println("\nChoose an available player:");

    for (int i = 0; i < playerOptions.size(); i++) {
      Player player = playerOptions.get(i);

      System.out.printf(
              "%d. %s %s | Height: %d inches | Experience: %s%n",
              i + 1,
              player.getFirstName(),
              player.getLastName(),
              player.getHeightInInches(),
              player.isPreviousExperience() ? "Yes" : "No"
      );
    }

    int selectedIndex = promptForIndex(playerOptions.size());
    return playerOptions.get(selectedIndex);
  }

  private void addPlayerToTeam() throws IOException {
    Team team = chooseTeam();

    if (team == null) {
      return;
    }

    if (team.isFull()) {
      System.out.printf(
              "Team '%s' already has the maximum of %d players.%n",
              team.getTeamName(),
              Team.MAX_PLAYERS
      );
      return;
    }

    Player player = chooseAvailablePlayer();

    if (player == null) {
      return;
    }

    boolean added = team.addPlayer(player);

    if (added) {
      availablePlayers.remove(player);

      System.out.printf(
              "%s %s was added to %s.%n",
              player.getFirstName(),
              player.getLastName(),
              team.getTeamName()
      );
    } else {
      System.out.println("The player could not be added.");
    }
  }

  // remove player section

  private Player choosePlayerFromTeam(Team team) throws IOException {
    if (team.getPlayers().isEmpty()) {
      System.out.printf(
              "Team '%s' does not have any players.%n",
              team.getTeamName()
      );
      return null;
    }

    List<Player> playerOptions =
            new ArrayList<>(team.getPlayers());

    playerOptions.sort(null);

    System.out.printf(
            "%nChoose a player from %s:%n",
            team.getTeamName()
    );

    for (int i = 0; i < playerOptions.size(); i++) {
      Player player = playerOptions.get(i);

      System.out.printf(
              "%d. %s %s | Height: %d inches | Experience: %s%n",
              i + 1,
              player.getFirstName(),
              player.getLastName(),
              player.getHeightInInches(),
              player.isPreviousExperience() ? "Yes" : "No"
      );
    }

    int selectedIndex = promptForIndex(playerOptions.size());
    return playerOptions.get(selectedIndex);
  }

  private void removePlayerFromTeam() throws IOException {
    Team team = chooseTeam();

    if (team == null) {
      return;
    }

    Player player = choosePlayerFromTeam(team);

    if (player == null) {
      return;
    }

    boolean removed = team.removePlayer(player);

    if (removed) {
      availablePlayers.add(player);

      System.out.printf(
              "%s %s was removed from %s.%n",
              player.getFirstName(),
              player.getLastName(),
              team.getTeamName()
      );
    } else {
      System.out.println("The player could not be removed.");
    }
  }

  private void printTeamRoster() throws IOException {
    Team team = chooseTeam();

    if (team == null) {
      return;
    }

    System.out.printf(
            "%nRoster for %s — Coach: %s%n",
            team.getTeamName(),
            team.getCoachName()
    );

    if (team.getPlayers().isEmpty()) {
      System.out.println("This team does not have any players.");
      return;
    }

    Set<Player> sortedPlayers = new TreeSet<>(team.getPlayers());

    System.out.println("Name | Height | Experience");

    for (Player player : sortedPlayers) {
      System.out.printf(
              "%s %s | %d inches | %s%n",
              player.getFirstName(),
              player.getLastName(),
              player.getHeightInInches(),
              player.isPreviousExperience() ? "Yes" : "No"
      );
    }
  }

  private void printHeightReport() throws IOException {
    Team team = chooseTeam();

    if (team == null) {
      return;
    }

    if (team.getPlayers().isEmpty()) {
      System.out.printf(
              "Team '%s' does not have any players.%n",
              team.getTeamName()
      );
      return;
    }

    Map<String, List<Player>> heightGroups = new LinkedHashMap<>();

    heightGroups.put("35-40 inches", new ArrayList<>());
    heightGroups.put("41-46 inches", new ArrayList<>());
    heightGroups.put("47-52 inches", new ArrayList<>());

    Set<Player> sortedPlayers = new TreeSet<>(team.getPlayers());

    for (Player player : sortedPlayers) {
      int height = player.getHeightInInches();

      if (height <= 40) {
        heightGroups.get("35-40 inches").add(player);
      } else if (height <= 46) {
        heightGroups.get("41-46 inches").add(player);
      } else {
        heightGroups.get("47-52 inches").add(player);
      }
    }

    System.out.printf(
            "%nHeight report for %s:%n",
            team.getTeamName()
    );

    for (Map.Entry<String, List<Player>> group
            : heightGroups.entrySet()) {

      System.out.printf(
              "%n%s — %d player(s)%n",
              group.getKey(),
              group.getValue().size()
      );

      for (Player player : group.getValue()) {
        System.out.printf(
                "- %s %s (%d inches)%n",
                player.getFirstName(),
                player.getLastName(),
                player.getHeightInInches()
        );
      }
    }
  }

  private Map<Team, Map<String, Integer>> buildLeagueBalanceReport() {
    Map<Team, Map<String, Integer>> balanceReport =
            new LinkedHashMap<>();

    for (Team team : teams) {
      int experiencedPlayers = 0;
      int inexperiencedPlayers = 0;

      for (Player player : team.getPlayers()) {
        if (player.isPreviousExperience()) {
          experiencedPlayers++;
        } else {
          inexperiencedPlayers++;
        }
      }

      Map<String, Integer> teamCounts = new LinkedHashMap<>();

      teamCounts.put("Experienced", experiencedPlayers);
      teamCounts.put("Inexperienced", inexperiencedPlayers);

      balanceReport.put(team, teamCounts);
    }

    return balanceReport;
  }

}
