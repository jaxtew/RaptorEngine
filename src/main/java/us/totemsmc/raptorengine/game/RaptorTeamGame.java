package us.totemsmc.raptorengine.game;

import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.team.Team;
import us.totemsmc.raptorengine.util.RaptorLogger;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class RaptorTeamGame extends RaptorGame
{
    private Team[] teams;

    public RaptorTeamGame(String name)
    {
        super(name);
    }

    public final void setTeams(Team[] teams)
    {
        if(getState() != GameState.INITIALIZING) return;
        RaptorLogger.debug("Teams: " + String.join(", ", Arrays.stream(teams).map(team -> team.getName()).collect(Collectors.toList())));
        this.teams = teams;
    }

    public final Team[] getTeams()
    {
        return teams.clone();
    }

    public final Team getTeam(String name)
    {
        if(teams == null) return null;
        return Arrays.stream(teams).filter(team -> team.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public final Team getTeam(Player player)
    {
        if(teams == null) return null;
        return Arrays.stream(teams).filter(team -> team.getPlayers().contains(player)).findFirst().orElse(null);
    }

    @Override
    public final void onPlayerQuit(Player player)
    {
        for (Team t : teams)
        {
            if(t.getPlayers().contains(player)) t.getPlayers().remove(player);
        }
    }

    public abstract void onPlayerQuitTeam(Player player);

    @Override
    public final void onPlayerJoin(Player player)
    {
        //assign to random team
        Random rand = new Random();
        Team randomTeam = teams[rand.nextInt(teams.length)];
        randomTeam.getPlayers().add(player);
        onPlayerJoinTeam(player);
    }

    public abstract void onPlayerJoinTeam(Player player);

    @Override
    public abstract void onStart();

    @Override
    public abstract void onTick();

    @Override
    public abstract void onStop();
}
