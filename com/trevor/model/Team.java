package com.trevor.model;

import java.util.ArrayList;
import java.util.List;

public class Team implements Comparable<Team> {
    public String teamName;
    public String coachName;
    public List<Player> players;

    public Team(String teamName, String coach) {
        this.teamName = teamName;
        this.coachName = coach;
        players = new ArrayList<>();
    }

    @Override
    public int compareTo(Team other) {
        return (this.teamName).compareTo(other.teamName);
    }

    @Override
    public String toString() {
        return String.format("Team %s coached by %s", teamName, coachName);
    }
}
