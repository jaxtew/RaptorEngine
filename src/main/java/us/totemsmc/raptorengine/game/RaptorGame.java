package us.totemsmc.raptorengine.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.scheduler.BukkitTask;
import us.totemsmc.raptorengine.RaptorEngine;
import us.totemsmc.raptorengine.map.RaptorMap;
import us.totemsmc.raptorengine.map.event.RaptorMapEvent;
import us.totemsmc.raptorengine.map.event.RaptorMapEventConsumer;
import us.totemsmc.raptorengine.objective.block.BlockObjective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class RaptorGame
{
    private final String name;
    private RaptorMap map;
    private GameState state;
    private BukkitTask gameLoop;
    private boolean hasWinner;
    private BlockObjective[] blockObjectives;
    private final List<Player> players;

    public RaptorGame(String name)
    {
        state = GameState.INITIALIZING;
        this.name = name;
        this.map = null;
        gameLoop = null;
        hasWinner = false;
        this.players = new ArrayList<>();
        // initialize
        // open for players to join map
    }

    public final void setMap(RaptorMap map)
    {
        if(this.map == null) this.map = map;
    }

    public final RaptorMap getMap()
    {
        return this.map;
    }

    public final void setBlockObjectives(BlockObjective[] blockObjectives)
    {
        if(state != GameState.INITIALIZING) return;
        this.blockObjectives = blockObjectives;
        state = GameState.WAITING;
    }

    public final List<BlockObjective> getBlockObjectives()
    {
        return Arrays.asList(blockObjectives);
    }

    public final BlockObjective getBlockObjective(String name)
    {
        return Arrays.stream(blockObjectives).filter(bo -> bo.getName().equals(name)).findFirst().orElse(null);
    }

    public final void playerQuit(Player player)
    {
        players.remove(player);
        onPlayerQuit(player);
    }

    public abstract void onPlayerQuit(Player player);

    public final void playerJoined(Player player)
    {
        players.add(player);
        // TODO: spectating?
        // allow players to roam map until game starts?
        onPlayerJoin(player);
    }

    public abstract void onPlayerJoin(Player player);

    public final void start()
    {
        if(state == GameState.RUNNING) return;
        state = GameState.RUNNING;
        onStart();
        gameLoop = Bukkit.getScheduler().runTaskTimer(RaptorEngine.getPlugin(RaptorEngine.class), () ->
        {
            onTick();
            if (hasWinner)
            {
                stop();
            }
        }, 0L, 1L);
    }

    public abstract void onStart();

    public abstract void onTick();

    public final void stop()
    {
        gameLoop.cancel();
        if(state != GameState.RUNNING) return;
        state = GameState.FINISHING;
        onStop();
        // wait a few seconds?
    }

    public abstract void onStop(); // TODO: Add winner

    public final void setWinner()
    {
        hasWinner = true;
    }

    public final List<Player> getPlayers()
    {
        return new ArrayList<>(players);
    }

    public final GameState getState()
    {
        return state;
    }

    public final String getName()
    {
        return name;
    }
}
