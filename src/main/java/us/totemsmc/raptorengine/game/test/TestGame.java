package us.totemsmc.raptorengine.game.test;

import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.game.RaptorGame;
import us.totemsmc.raptorengine.map.RaptorMapEventHandler;
import us.totemsmc.raptorengine.map.event.block.RMBlockBreakEvent;
import us.totemsmc.raptorengine.map.event.block.RMBlockPlaceEvent;
import us.totemsmc.raptorengine.map.event.objective.RMBreakBlockObjectiveCompleteEvent;
import us.totemsmc.raptorengine.map.event.objective.RMPlaceBlockObjectiveCompleteEvent;
import us.totemsmc.raptorengine.map.event.player.RMPlayerDeathEvent;
import us.totemsmc.raptorengine.map.event.player.RMPlayerSpawnEvent;

public class TestGame extends RaptorGame
{
    private long elapsed;

    public TestGame()
    {
        super("Test");
        elapsed = 0L;
    }

    @Override
    public void onPlayerQuit(Player player)
    {
        player.sendMessage("You have quit the test game");
    }

    @Override
    public void onPlayerJoin(Player player)
    {
        player.sendMessage("You have joined the test game");
    }

    @Override
    public void onStart()
    {
        getPlayers().forEach(player -> player.sendMessage("Test game started"));
    }

    @Override
    public void onTick()
    {
        elapsed++;
//        if(getBlockObjectives().stream().filter(RaptorObjective::isComplete).count() == getBlockObjectives().size()) setWinner();
    }

    @Override
    public void onStop()
    {
        getPlayers().forEach(player -> player.sendMessage("Test game over. " + (elapsed/20.0) + " seconds elapsed."));
    }

    @RaptorMapEventHandler
    public void onBlockBreak(RMBlockBreakEvent event)
    {
        event.getPlayer().sendMessage("You broke a " + event.getBlock().getType());
    }

    @RaptorMapEventHandler
    public void onBlockPlace(RMBlockPlaceEvent event)
    {
        event.getPlayer().sendMessage("You placed a " + event.getBlock().getType());
    }

    @RaptorMapEventHandler
    public void onBreakBlockObjectiveComplete(RMBreakBlockObjectiveCompleteEvent event)
    {
        event.getPlayer().sendMessage("You broke " + event.getObjective().getName());
    }

    @RaptorMapEventHandler
    public void onPlaceBlockObjectiveComplete(RMPlaceBlockObjectiveCompleteEvent event)
    {
        event.getPlayer().sendMessage("You placed " + event.getObjective().getName());
    }

    @RaptorMapEventHandler
    public void onPlayerDeath(RMPlayerDeathEvent event)
    {
        event.getPlayer().sendMessage("You died by " + event.getCause());
    }

    @RaptorMapEventHandler
    public void onPlayerSpawn(RMPlayerSpawnEvent event)
    {
        event.getPlayer().sendMessage("You spawned!");
    }
}
