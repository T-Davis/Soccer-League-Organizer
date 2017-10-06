package com.trevor;

import com.trevor.model.Team;
import com.trevor.model.Player;
import com.trevor.model.Players;

import javax.naming.InvalidNameException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import java.util.Collections;

public class LeagueOrganizer {
    private BufferedReader reader;
    private List<Team> teams;
    private List<String> menu;
    private List<Player> players;
    private int totalTeamsNeeded;

    public LeagueOrganizer() {
        players = new ArrayList<>();
        players.addAll(Arrays.asList(Players.load()));
        Collections.sort(players);
        reader = new BufferedReader(new InputStreamReader(System.in));
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
        totalTeamsNeeded = players.size() / 11;
        System.out.printf("Welcome to the League Organizer " +
                "%nPlease input numbers to make your selections%n");
    }

    private int promptAction() throws IOException, IndexOutOfBoundsException, NumberFormatException {
        int teamsCount = teams.size();
        int teamsStillNeeded = totalTeamsNeeded - teamsCount;
        System.out.printf("%n%nThere are currently %d teams and %d unassigned players. %n" +
                "You will need %d more teams. %n" +
                "%nYour options are: %n", teamsCount, players.size(), teamsStillNeeded);
        int count = 1;
        for (String option : menu) {
            System.out.printf("%d - %s %n", count, option);
            count++;
        }
        System.out.printf("%nWhat would you like to do? ");
        int choice = Integer.parseInt(reader.readLine());
        return choice;
    }

    public void run() {
        int choice = 999;
        int teamsCount = teams.size();
        do {
            try {
                choice = promptAction();
                if(choice != 8) {
                    if (teamsCount == 0 && choice != 1) {
                        System.out.printf("%n----- You must create a team first ----- %n");
                        continue;
                    }
                }
                switch (choice) {
                    case 1:
                        if (teamsCount == totalTeamsNeeded) {
                            System.out.printf("%n----- You don't have enough players for more teams ----- %n");
                            break;
                        }
                        createTeam();
                        teamsCount++;
                        break;
                    case 2:
                        addPlayerToTeam();
                        break;
                    case 3:
                        removePlayerFromTeam();
                        break;
                    case 4:
                        teamReport(selectTeam(), true);
                        break;
                    case 5:
                        leagueReport();
                        break;
                    case 6:
                        printRoster();
                        break;
                    case 7:
                        if (teamsCount != totalTeamsNeeded) {
                            System.out.println("You must have all teams created to use auto-assign");
                            break;
                        }
                        autoAssignPlayers();
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.printf("Unknown Choice: '%s'. Try again %n", choice);
                }
            } catch (IOException ioe) {
                System.out.println("Problem with input");
                ioe.printStackTrace();
            } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                System.out.println("Not a valid selection");
            } catch (InvalidNameException ine) {
                System.out.println("Team name must not be a duplicate and only contain letters");
            }
        } while (choice != 8);
    }

    private Team promptNewTeam() throws IOException, InvalidNameException {
        System.out.print("Enter the team's name:  ");
        String teamName = reader.readLine().trim();
        String coachName = "";
        if (verifyTeamName(teamName)) {
            System.out.print("Enter the coach's name:  ");
            coachName = reader.readLine();
        } else {
            throw new InvalidNameException();
        }

        return new Team(teamName, coachName);
    }

    private boolean verifyTeamName(String teamName) {
        if(teamName == null || teamName.equals(""))
            return false;

        if(!teamName.matches("[a-zA-Z]*"))
                return false;

        for (Team team : teams) {
            if (Objects.equals(teamName, team.teamName))
                return false;
        }

        return teamName.matches("[a-zA-Z]*");
    }

    private void createTeam() throws IOException, InvalidNameException {
        Team team = promptNewTeam();
        teams.add(team);
        System.out.printf("%s added  %n", team);
        Collections.sort(teams);
    }

    private void addPlayerToTeam() throws IOException {
        Player player = selectUnassignedPlayer();
        Team team = selectTeam();
        team.players.add(player);
        players.remove(player);
        System.out.printf("%nPlayer %s added to team %s  %n", player, team);
        Collections.sort(team.players);
    }

    private void removePlayerFromTeam() throws IOException {
        Team team = selectTeam();
        Player player = selectAssignedPlayer(team);
        team.players.remove(player);
        players.add(player);
        Collections.sort(players);
    }

    private Player selectUnassignedPlayer() throws IOException, IndexOutOfBoundsException, NumberFormatException {
        int count = 1;
        for (Player player : players) {
            System.out.printf("%02d - %s %n", count, player);
            count++;
        }
        System.out.printf("%nChoose a player:  ");
        int choice = Integer.parseInt(reader.readLine());
        return players.get(choice - 1);
    }

    private Player selectAssignedPlayer(Team team) throws IOException, IndexOutOfBoundsException, NumberFormatException {
        int count = 1;
        for (Player player : team.players) {
            System.out.printf("%02d - %s %n", count, player);
            count++;
        }
        System.out.printf("%nChoose a player:  ");
        int choice = Integer.parseInt(reader.readLine());
        return team.players.get(choice - 1);
    }

    private Team selectTeam() throws IOException, IndexOutOfBoundsException, NumberFormatException {
        int count = 1;
        for (Team team : teams) {
            System.out.printf("%02d - %s %n", count, team);
            count++;
        }
        System.out.print("Choose a team:  ");
        int choice = Integer.parseInt(reader.readLine());
        return teams.get(choice - 1);
    }

    private void teamReport(Team team, boolean sortAndPrint) {
        int expPlayers = 0;
        int teamSize = team.players.size();
        if(sortAndPrint) {
            Collections.sort(team.players, Comparator.comparingInt(Player::getHeightInInches));
            for (Player player : team.players) {
                if (player.isPreviousExperience()) expPlayers++;
                System.out.println(player);
            }
            Collections.sort(team.players);
        } else {
            for (Player player : team.players) {
                if (player.isPreviousExperience()) expPlayers++;
            }
        }
        double expPlayerPerc = (double) expPlayers / (double) teamSize * 100;
        System.out.printf("%n%s has %d players and %.2f percent are experienced%n", team, teamSize, expPlayerPerc);
    }

    private void leagueReport() {
        for (Team team : teams) {
            teamReport(team, false);
            Map<Integer, Integer> playerHeights = new HashMap<>();
            for (Player player : team.players) {
                Integer playerHeight = player.getHeightInInches();
                if (playerHeights.containsKey(playerHeight)) {
                    Integer temp = playerHeights.get(playerHeight);
                    playerHeights.replace(playerHeight, ++temp);
                } else {
                    playerHeights.put(playerHeight, 1);
                }
            }
            System.out.println(playerHeights.entrySet());
        }
    }

    private void printRoster() throws IOException {
        for (Player player : selectTeam().players) {
            System.out.println(player);
        }
    }

    private void autoAssignPlayers() {
        Collections.sort(players, Comparator.comparing(Player::isPreviousExperience));
        int count = 0;
        for (Player player : players) {
            teams.get(count).players.add(player);
            count++;
            if (count == 3) count = 0;
        }
        players.clear();
    }
}
