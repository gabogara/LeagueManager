package com.teamtreehouse.model;
import java.util.HashSet;
import java.util.Set;

public class Team {
    public static final int MAX_PLAYERS = 11;
    private String teamName;
    private String coachName;
    private Set<Player> players;

    public Team(String teamName, String coachName) {
    this.teamName = teamName;
    this.coachName = coachName;
    this.players = new HashSet<>();
    }
    public String getTeamName() {
        return teamName;
    }
    public String getCoachName() {
        return coachName;
    }

    public Set<Player> getPlayers() {
        return players;
    }
}