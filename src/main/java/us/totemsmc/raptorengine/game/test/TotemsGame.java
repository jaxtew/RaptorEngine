package us.totemsmc.raptorengine.game.test;

import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.game.RaptorTeamGame;
import us.totemsmc.raptorengine.map.RaptorMapEventHandler;
import us.totemsmc.raptorengine.map.event.objective.RMBreakBlockObjectiveCompleteEvent;
import us.totemsmc.raptorengine.map.event.player.RMPlayerSpawnEvent;
import us.totemsmc.raptorengine.team.Team;

public class TotemsGame extends RaptorTeamGame
{
    private Team winner;

    public TotemsGame()
    {
        super("totems");
    }

    @Override
    public void onPlayerQuitTeam(Player player)
    {

    }

    @Override
    public void onPlayerJoinTeam(Player player)
    {
        String teamName = getTeam(player).getName();
        player.sendMessage("You are on " + teamName + " team.");
    }

    @Override
    public void onStart()
    {
        getPlayers().forEach(player -> player.sendMessage("Game starting, good luck"));
    }

    @Override
    public void onTick()
    {

    }

    @Override
    public void onStop()
    {
        getPlayers().forEach(player -> player.sendMessage("GG"));
    }

    @RaptorMapEventHandler
    public void onTotemBreak(RMBreakBlockObjectiveCompleteEvent event)
    {
        Team team = getTeam(event.getObjective().getTeam());
        team.getPlayers().forEach(player -> player.sendMessage("Your totem has been broken!"));
        setWinner();
    }

    @RaptorMapEventHandler
    public void onPlayerSpawn(RMPlayerSpawnEvent event)
    {

    }
}
