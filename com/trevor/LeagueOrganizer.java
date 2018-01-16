package com.trevor;

import com.trevor.model.Player;
import com.trevor.model.Players;
import com.trevor.model.Team;

import javax.naming.InvalidNameException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

// fix league report and auto assign









public class LeagueOrganizer {
    private BufferedReader reader;
    private Set<Team> teams;
    private List<String> menu;
    private Set<Player> players;
    private int numTeamsNeeded;

    public LeagueOrganizer() {
        players = new TreeSet(Arrays.asList(Players.load()));
        numTeamsNeeded = players.size() / 11;
        reader = new BufferedReader(new InputStreamReader(System.in));
        teams = new TreeSet<>();
        menu = new ArrayList<>();
        menu.add("Create new team");
        menu.add("Add player to team");
        menu.add("Remove player from team");
        menu.add("League Balance Report");
        menu.add("Experience Report");
        menu.add("Print roster");
        menu.add("Auto-assign players");
        menu.add("Exit the program");
    }

    public void welcome() {
        System.out.printf("Welcome to the League Organizer " +
                "%nPlease input numbers to make your selections%n");
    }

    private int promptAction() throws IOException, IndexOutOfBoundsException, NumberFormatException {
        System.out.printf("%n%nThere are currently %d teams and %d unassigned players. %n" +
                "You will need %d more teams. %n" +
                "%nYour options are: %n", teams.size(), players.size(), numTeamsNeeded);
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
        do {
            try {
                choice = promptAction();
                if (numTeamsNeeded == 3 && choice != 1 && choice != 8) {
                    System.out.printf("%n----- You must create a team first ----- %n");
                    continue;
                }
                switch (choice) {
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
                        leagueBalanceReport(selectTeam());
                        break;
                    case 5:
                        experienceReport();
                        break;
                    case 6:
                        printRoster();
                        break;
                    case 7:
                        autoAssignPlayers();
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
            } catch (NumberFormatException | IndexOutOfBoundsException | IllegalStateException | NullPointerException ex) {
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
        if (numTeamsNeeded == 0) {
            System.out.printf("%n----- You don't have enough players for more teams ----- %n");
            return;
        }
        Team team = promptNewTeam();
        teams.add(team);
        numTeamsNeeded--;
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
        team.players.remove(player);
        players.add(player);
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

    private void leagueBalanceReport(Team team) {
        int teamSize = team.players.size();
        if (teamSize == 0) {
            System.out.printf("%n----- %s has zero players -----%n" +
                    "----- You must have players to run a Team Report -----%n", team);
            return;
        }

        String h1 = "35 - 40";
        String h2 = "41 - 46";
        String h3 = "47 - 50";

        Map<String, List<Player>> playerHeights = new HashMap<>();
        playerHeights.put("35 - 40", new ArrayList<>());
        playerHeights.put("41 - 46", new ArrayList<>());
        playerHeights.put("47 - 50", new ArrayList<>());
        double avgExperience = 0;
        for (Player player : team.players) {
            if (player.isPreviousExperience()) avgExperience++;
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

        int heightCount = 0;
        System.out.printf("There are %d players %s inches tall %n", playerHeights.get(h1).size(), h1);
        for (Player player : playerHeights.get(h1)) {
            System.out.println(player);
            heightCount++;
        }
        int count35to40 = heightCount;
        heightCount = 0;
        System.out.println();
        System.out.printf("There are %d players %s inches tall %n", playerHeights.get(h2).size(), h2);
        for (Player player : playerHeights.get(h2)) {
            System.out.println(player);
            heightCount++;
        }
        int count41to46 = heightCount;
        heightCount = 0;
        System.out.println();

        System.out.printf("There are %d players %s inches tall %n", playerHeights.get(h3).size(), h3);
        for (Player player : playerHeights.get(h3)) {
            System.out.println(player);
            heightCount++;
        }
        int count47to50 = heightCount;
        System.out.println();

        System.out.printf("Count of player heights: %n" +
                "35-40 = %d %n" +
                "41-46 = %d %n" +
                "47-50 = %d %n", count35to40, count41to46, count47to50);

        avgExperience = 100 * (avgExperience / teamSize);
        System.out.printf("Average experience is %.2f%%%n", avgExperience);
    }

    private void experienceReport() {
        Map<String, List> experiencedPlayers = new HashMap<>();

        for (Team team : teams) {
            String teamName = team.teamName;
            List<Player> noExperience = new ArrayList<>();
            List<Player> yesExperience = new ArrayList<>();
            for (Player player : team.players) {
                if (!player.isPreviousExperience()) noExperience.add(player);
                if (player.isPreviousExperience()) yesExperience.add(player);
            }
            List<List> listOfListOfExpOrNotPlayers = new ArrayList<>();
            listOfListOfExpOrNotPlayers.add(yesExperience);
            listOfListOfExpOrNotPlayers.add(noExperience);
            experiencedPlayers.put(teamName, listOfListOfExpOrNotPlayers);

            System.out.printf("Experienced players of team %s: %n", teamName);
            for (Player player : yesExperience) {
                System.out.println(player);
            }
            System.out.println();
            System.out.printf("Non-experienced players of team %s: %n", teamName);
            for (Player player : noExperience) {
                System.out.println(player);
            }
            double numExp = yesExperience.size();
            double numNonExp = noExperience.size();
            double expPerc = 100 * (numExp / (numExp + numNonExp));
            System.out.println();
            System.out.printf("Team %s has %d experienced and %d inexperienced players %n" +
                            "Team %s is %.2f%% experienced %n%n",
                    teamName, (int) numExp, (int) numNonExp, teamName, expPerc);
        }
    }

    private void printRoster() throws IOException {
        Team team = selectTeam();
        System.out.printf("%nPlayers on %s %n%n", team);
        for (Player player : team.players) {
            System.out.println(player);
        }
    }

    private void autoAssignPlayers() {
        if (players.isEmpty()) {
            System.out.println("There are no unassigned players");
            return;
        }

//        for (Team team : teams) {
//            while (team.players.size() != 11) {
//                Player player = players.iterator().next();
//                team.players.add(player);
//                players.remove(player);
//            }
//        }

        Comparator<Player> comparator = Comparator.comparing(player -> player.isPreviousExperience());
        comparator = comparator.thenComparing(Comparator.comparing(player -> player.getHeightInInches()));

        // Sort the stream:
        Stream<Player> playerStream = players.stream().sorted(comparator);
        playerStream.forEach(System.out::println);

        System.out.println("Auto-assign complete");
    }
}
