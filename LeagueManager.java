import com.trevor.LeagueOrganizer;
import com.trevor.model.Player;
import com.trevor.model.Players;

import java.util.Arrays;
import java.util.List;

public class LeagueManager {

    public static void main(String[] args) {
        // Your code here!
        LeagueOrganizer leagueOrganizer= new LeagueOrganizer();
        System.out.printf("There are currently %d registered players.%n", leagueOrganizer.getPlayers().size());
        leagueOrganizer.run();
    }

}
