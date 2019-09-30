package us.totemsmc.raptorengine.map.event.player;

import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.map.RaptorMap;
import us.totemsmc.raptorengine.map.event.RaptorMapEvent;

public class RMPlayerEvent extends RaptorMapEvent
{
    private final Player player;

    RMPlayerEvent(RaptorMap map, Player player)
    {
        super(map);
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }
}
