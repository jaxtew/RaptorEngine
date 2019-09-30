package us.totemsmc.raptorengine.team;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team
{
    private final String name;
    private final List<Player> players;

    public Team(String name)
    {
        this.name = name;
        this.players = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }

    public List<Player> getPlayers()
    {
        return players;
    }
}
