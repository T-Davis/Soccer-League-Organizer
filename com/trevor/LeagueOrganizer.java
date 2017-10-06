package com.trevor;

import com.trevor.model.Team;
import com.trevor.model.Player;
import com.trevor.model.Players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import java.util.Collections;

public class LeagueOrganizer {
    private BufferedReader mReader;
    private List<Team> teams;
    private List<String> menu;
    private List<Player> players;

    public LeagueOrganizer() {
        players = new ArrayList<>();
        players.addAll(Arrays.asList(Players.load()));
        Collections.sort(players);
        mReader = new BufferedReader(new InputStreamReader(System.in));
        teams = new ArrayList<>();
        menu = new ArrayList<>();
        menu.add("Create new team");
        menu.add("Add player to team");
        menu.add("Remove player from team");
        menu.add("Exit the program");


    }

    private int promptAction() throws IOException {
        System.out.printf("Welcome to the League Organizer, there are currently " +
                "%d teams and %d players left to choose from. %nYour options are: %n", teams.size(), players.size());
        int count = 1;
        for(String option : menu) {
            System.out.printf("%d - %s %n", count, option);
            count++;
        }
        System.out.print("What would you like to do? ");
        int choice = Integer.parseInt(mReader.readLine());
        return choice;
    }

    public void run() {
        int choice = 999;
        do {
            try {
                choice = promptAction();
                switch(choice) {
                    case 1:
                        Team team = promptNewTeam();
                        teams.add(team);
                        System.out.printf("Team %s added  %n%n%n", team);
                        Collections.sort(teams);
                        break;
                    case 2:
                        Player playerToAdd = selectUnassignedPlayer();
                        Team teamToAddTo = selectTeam();
                        teamToAddTo.players.add(playerToAdd);
                        players.remove(playerToAdd);
                        System.out.printf("%nPlayer %s added to team %s  %n%n%n", playerToAdd, teamToAddTo);
                        Collections.sort(teamToAddTo.players);
                        break;
                    case 3:
                        Team teamToRemoveFrom = selectTeam();
                        Player playerToRemove = selectAssignedPlayer(teamToRemoveFrom);
                        teamToRemoveFrom.players.remove(playerToRemove);
                        players.add(playerToRemove);
                        Collections.sort(players);
                        break;
                    case 9:
                        System.out.println("Thanks for playing!");
                        break;
                    default:
                        System.out.printf("Unknown Choice: '%s'. Try again %n%n", choice);
                }
            } catch(IOException ioe) {
                System.out.println("Problem with input");
                ioe.printStackTrace();
            }
        } while(choice != 9);
    }

    private Team promptNewTeam() throws IOException {
        System.out.print("Enter the team's name:  ");
        String teamName = mReader.readLine();
        System.out.print("Enter the coach's name:  ");
        String coachName = mReader.readLine();
        return new Team(teamName, coachName);
    }

    private Player selectUnassignedPlayer() throws IOException {
        int count = 1;
        for(Player player : players) {
            System.out.printf("%02d - %s %n", count, player);
            count++;
        }
        System.out.print("Choose a player:  ");
        int choice = Integer.parseInt(mReader.readLine());
        return players.get(choice - 1);
    }

    private Player selectAssignedPlayer(Team team) throws IOException {
        int count = 1;
        for(Player player : team.players) {
            System.out.printf("%02d - %s %n", count, player);
            count++;
        }
        System.out.print("Choose a player:  ");
        int choice = Integer.parseInt(mReader.readLine());
        return team.players.get(choice - 1);
    }

    private Team selectTeam() throws IOException {
        int count = 1;
        for(Team team : teams) {
            System.out.printf("%02d - %s %n", count, team);
            count++;
        }
        System.out.print("Choose a team:  ");
        int choice = Integer.parseInt(mReader.readLine());
        return teams.get(choice - 1);
    }


    public List<Player> getPlayers() {
        return players;
    }
}
