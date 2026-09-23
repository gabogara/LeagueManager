import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
            System.out.println("Create selected");
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

}
