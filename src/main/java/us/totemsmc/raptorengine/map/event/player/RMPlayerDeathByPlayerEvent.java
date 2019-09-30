package us.totemsmc.raptorengine.map.event.player;

import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.map.RaptorMap;

public class RMPlayerDeathByPlayerEvent extends RMPlayerEvent
{
    private final Player killer;

    public RMPlayerDeathByPlayerEvent(RaptorMap map, Player player, Player killer)
    {
        super(map, player);
        this.killer = killer;
    }

    public Player getKiller()
    {
        return killer;
    }
}
