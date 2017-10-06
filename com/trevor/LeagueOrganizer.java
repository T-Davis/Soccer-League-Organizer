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
        menu.add("Team Report");
        menu.add("League Balance Report");
        menu.add("Print roster");
        menu.add("Auto-assign players");
        menu.add("Exit the program");
    }

    public void welcome() {
        System.out.printf("Welcome to the League Organizer " +
                "%nPlease input numbers to make your selections%n%n");
    }

    private int promptAction() throws IOException {
        System.out.printf("There are currently %d teams and %d unassigned players. " +
                "%nYour options are: %n", teams.size(), players.size());
        int count = 1;
        for(String option : menu) {
            System.out.printf("%d - %s %n", count, option);
            count++;
        }
        System.out.printf("%nWhat would you like to do? ");
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
                        createTeam();
                        break;
                    case 2:
                        addPlayerToTeam();
                        break;
                    case 3:
                        removePlayerFromTeam();
                        break;
                    case 4:
                        teamReport(selectTeam());
                        break;
                    case 5:
                        //team balance report
                        break;
                    case 6:
                        //print roster
                        break;
                    case 7:
                        //auto assign players
                        break;
                    case 8:
                        //quit program
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

    private void createTeam() throws IOException {
        Team team = promptNewTeam();
        teams.add(team);
        System.out.printf("%s added  %n%n%n", team);
        Collections.sort(teams);
    }

    private void addPlayerToTeam() throws IOException {
        Player player = selectUnassignedPlayer();
        Team team = selectTeam();
        team.players.add(player);
        players.remove(player);
        System.out.printf("%nPlayer %s added to team %s  %n%n%n", player, team);
        Collections.sort(team.players);
    }

    private void removePlayerFromTeam() throws IOException {
        Team team = selectTeam();
        Player player = selectAssignedPlayer(team);
        team.players.remove(player);
        players.add(player);
        Collections.sort(players);
    }

    private Player selectUnassignedPlayer() throws IOException {
        int count = 1;
        for(Player player : players) {
            System.out.printf("%02d - %s %n", count, player);
            count++;
        }
        System.out.printf("%nChoose a player:  ");
        int choice = Integer.parseInt(mReader.readLine());
        return players.get(choice - 1);
    }

    private Player selectAssignedPlayer(Team team) throws IOException {
        int count = 1;
        for(Player player : team.players) {
            System.out.printf("%02d - %s %n", count, player);
            count++;
        }
        System.out.printf("(enter number)%nChoose a player:  ");
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

    private void teamReport(Team team) {
        Collections.sort(team.players, (a, b) -> a.getHeightInInches() < b.getHeightInInches()
                ? -1 : a.getHeightInInches() == b.getHeightInInches() ? 0 : 1);
        double expLevel = 0;
        for (Player player : team.players) {
            if(player.isPreviousExperience() == true) {
                expLevel++;
            }
            System.out.println();
            System.out.println(player);
        }
        //System.out.println(team.players);
        System.out.println("Experience of team: " + (expLevel / team.players.size() * 100) + "% ");
        System.out.println();
        Collections.sort(team.players);
    }
}
