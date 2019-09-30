package us.totemsmc.raptorengine.map.event.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.map.RaptorMap;
import us.totemsmc.raptorengine.map.event.RaptorMapEvent;

public class RMBlockEvent extends RaptorMapEvent
{
    private final Block block;
    private final Player player;

    RMBlockEvent(RaptorMap map, Player player, Block block)
    {
        super(map);
        this.player = player;
        this.block = block;
    }

    public Player getPlayer()
    {
        return player;
    }

    public final Block getBlock()
    {
        return block;
    }
}
