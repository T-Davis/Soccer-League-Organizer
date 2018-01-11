package com.trevor;

import com.trevor.model.Team;
import com.trevor.model.Player;
import com.trevor.model.Players;

import javax.naming.InvalidNameException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LeagueOrganizer {
    private BufferedReader reader;
    private TreeSet<Team> teams;
    private List<String> menu;
    private Set<Player> players;
    private int totalTeamsNeeded;

    public LeagueOrganizer() {
        players = new TreeSet(Arrays.asList(Players.load()));
        reader = new BufferedReader(new InputStreamReader(System.in));
        teams = new TreeSet<>();
        menu = new ArrayList<>();
        menu.add("Create new team");
        menu.add("Add player to team");
        menu.add("Remove player from team");
        menu.add("Height Report");
        menu.add("League Balance Report");
        menu.add("Print roster");
        menu.add("Auto-assign players");
        menu.add("Exit the program");
    }

//    private void addDuplicatePlayer() throws IOException {
//        Team team = selectTeam();
//        if (team.players.isEmpty()) {
//            System.out.println("There are no players assigned to this team");
//            return;
//        }
//        Player player = selectAssignedPlayer(team);
//        if (player == null) throw new IllegalStateException("Player cannot be null");
//        players.add(player);
//    }

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
        System.out.printf("%nWhat would you like to do? %n");
        return Integer.parseInt(reader.readLine());
    }

    public void run() {
        int choice = 999;
        int teamsCount = teams.size();
        do {
            try {
                choice = promptAction();
                if (choice != 8) {
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
                        if (teamsCount == 1 && teams.first().players.isEmpty()) {
                            System.out.printf("%n----- There are no players assigned ----- %n");
                            break;
                        }
                        removePlayerFromTeam();
                        break;
                    case 4:
                        heightReport(selectTeam());
                        break;
                    case 5:
                        leagueReport();
                        break;
                    case 6:
                        printRoster();
                        break;
                    case 7:
                        if (teamsCount != totalTeamsNeeded) {
                            System.out.printf("%n----- You must have all teams created to use auto-assign ----- %n");
                            break;
                        }
//                        autoAssignPlayers();
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.printf("%nUnknown Choice: '%s'. Try again %n", choice);
                }
            } catch (IOException ioe) {
                System.out.printf("%n----- Problem with input ----- %n");
                ioe.printStackTrace();
            } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                System.out.printf("%n----- Not a valid selection ----- %n");
            } catch (InvalidNameException ine) {
                System.out.printf("%n----- Team name must not be a duplicate and only contain letters ----- %n");
            }
        } while (choice != 8);
    }

    private Team promptNewTeam() throws IOException, InvalidNameException {
        System.out.print("Enter the team's name:  ");
        String teamName = reader.readLine().trim();
        String coachName;
        if (verifyTeamName(teamName)) {
            System.out.print("Enter the coach's name:  ");
            coachName = reader.readLine().trim();
        } else {
            throw new InvalidNameException();
        }

        return new Team(teamName, coachName);
    }

    private boolean verifyTeamName(String teamName) {
        if (teamName == null || teamName.equals(""))
            return false;

        if (!teamName.matches("[a-zA-Z]*"))
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
    }

    private void addPlayerToTeam() throws IOException {
        Player player = selectUnassignedPlayer();
        if (player == null) throw new IllegalStateException("Player cannot be null");
        Team team = selectTeam();
        if (team.players.size() >= 11) {
            System.out.printf("%n----- Teams cannot have more than 11 players ----- %n");
            return;
        }
        team.players.add(player);
        players.remove(player);
        System.out.printf("%nPlayer %s added to team %s  %n", player, team);
    }

    private void removePlayerFromTeam() throws IOException {
        Team team = selectTeam();
        if (team.players.isEmpty()) {
            System.out.printf("%n----- There are no players assigned to this team ----- %n");
            return;
        }
        Player player = selectAssignedPlayer(team);
        if (player == null) throw new IllegalStateException("Player cannot be null");
        team.players.remove(player);
        players.add(player);
//        Collections.sort(players);
    }

    private Player selectUnassignedPlayer() throws IOException, IndexOutOfBoundsException, NumberFormatException {
        int count = 1;
        for (Player player : players) {
            System.out.printf("%02d - %s %n", count, player);
            count++;
        }
        System.out.printf("%nChoose a player:  ");
        int choice = Integer.parseInt(reader.readLine());
        count = 1;

        for (Player player : players) {
            if (count == choice) return player;
            count++;
        }
        //this should never happen!
        return null;
    }

    private Player selectAssignedPlayer(Team team) throws IOException, IndexOutOfBoundsException, NumberFormatException {
        int count = 1;
        for (Player player : team.players) {
            System.out.printf("%02d - %s %n", count, player);
            count++;
        }
        System.out.printf("%nChoose a player:  ");
        int choice = Integer.parseInt(reader.readLine());
        count = 1;

        for (Player player : team.players) {
            if (count == choice) return player;
            count++;
        }

        //this should never happen!
        return null;
    }

    private Team selectTeam() throws IOException, IndexOutOfBoundsException, NumberFormatException {
        int count = 1;
        for (Team team : teams) {
            System.out.printf("%d - %s %n", count, team);
            count++;
        }
        System.out.print("Choose a team:  ");
        int choice = Integer.parseInt(reader.readLine());
        count = 1;

        for (Team team : teams) {
            if (count == choice) return team;
            count++;
        }

        //this should never happen!
        return null;
    }

    private void heightReport(Team team) {
        int teamSize = team.players.size();
        if (teamSize == 0) {
            System.out.printf("%n----- %s has zero players -----%n" +
                    "----- You must have players to run a Team Report -----%n", team);
            return;
        }

        Map<String, Set<Player>> playerHeights = new HashMap<>();
        String h1 = "35 - 40";
        String h2 = "41 - 46";
        String h3 = "47 - 50";
        playerHeights.put(h1, new TreeSet<>());
        playerHeights.put(h2, new TreeSet<>());
        playerHeights.put(h3, new TreeSet<>());
        for (Player player : team.players) {
            int playerHeight = player.getHeightInInches();

            if (playerHeight >= 35 && playerHeight <= 40) {
                playerHeights.get(h1).add(player);
            }
            if (playerHeight >= 41 && playerHeight <= 46) {
                playerHeights.get(h2).add(player);
            }
            if (playerHeight >= 47 && playerHeight <= 50) {
                playerHeights.get(h3).add(player);
            }
        }

        System.out.printf("There are %d players %s inches tall %n", playerHeights.get(h1).size(), h1);
        for (Player player : playerHeights.get(h1)) {
            System.out.println(player);
        }
        System.out.println();

        System.out.printf("There are %d players %s inches tall %n", playerHeights.get(h2).size(), h2);
        for (Player player : playerHeights.get(h2)) {
            System.out.println(player);
        }
        System.out.println();

        System.out.printf("There are %d players %s inches tall %n", playerHeights.get(h3).size(), h3);
        for (Player player : playerHeights.get(h3)) {
            System.out.println(player);
        }

//        System.out.println(playerHeights.entrySet());
//        for (String heightRange : playerHeights.keySet()) {
//            System.out.println("These players are between " + heightRange + " inches tall: ");
//            for (Player player : playerHeights.values()) {
//                System.out.println(player);
//            }
//            System.out.println();
//        }

//        for (Map.Entry<String, Player> e : playerHeights.entrySet()) {
////            player.getKey();
//            System.out.println(e.getValue());
//        }





//        if (sortAndPrint) {
//            List<Player> playerHeight = new ArrayList<>(team.players);
//            playerHeight.sort(Comparator.comparingInt(Player::getHeightInInches));
//            for (Player player : team.players) {
//                if (player.isPreviousExperience()) expPlayers++;
//                System.out.println(player);
//            }
//        } else {
//            for (Player player : team.players) {
//                if (player.isPreviousExperience()) expPlayers++;
//            }
//        }
//        double expPlayerPerc = (double) expPlayers / (double) teamSize * 100;
//        System.out.printf("%n%s has %d players and %.2f percent are experienced%n", team, teamSize, expPlayerPerc);
    }

    private void leagueReport() {

    }

    private void printRoster() throws IOException {
        for (Player player : selectTeam().players) {
            System.out.println(player);
        }
    }

//    private void autoAssignPlayers() {
//        if (players.isEmpty()) {
//            System.out.println("There are no unassigned players");
//            return;
//        }
////        players.sort(Comparator.comparing(Player::isPreviousExperience));
//        int count = 0;
//        for (Player player : players) {
//            if (teams.get(count).players.size() == 11) count++;
//            teams.get(count).players.add(player);
//            count++;
//            if (count == 3) count = 0;
//        }
//        players.clear();
//    }
}
