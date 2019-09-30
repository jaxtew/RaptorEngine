package us.totemsmc.raptorengine.game.test;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.game.RaptorGame;
import us.totemsmc.raptorengine.map.RaptorMapEventHandler;
import us.totemsmc.raptorengine.map.event.objective.RMPlaceBlockObjectiveCompleteEvent;

public class PlaceGame extends RaptorGame
{
    public PlaceGame()
    {
        super("placegame");
    }

    @Override
    public void onPlayerQuit(Player player)
    {
        player.sendMessage("You have quit the place game.");
    }

    @Override
    public void onPlayerJoin(Player player)
    {
        player.sendMessage("You have joined the place game.");
        start();
    }

    @Override
    public void onStart()
    {
        Bukkit.broadcastMessage("Place game has started!");
    }

    @Override
    public void onTick()
    {

    }

    @Override
    public void onStop()
    {
        Bukkit.broadcastMessage("Place game has ended!");
    }

    @RaptorMapEventHandler
    public void onPlaceObjectiveComplete(RMPlaceBlockObjectiveCompleteEvent event)
    {
        getPlayers().forEach(player -> player.sendMessage("Game over... you win!"));
        setWinner();
    }
}
