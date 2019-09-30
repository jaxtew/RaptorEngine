package us.totemsmc.raptorengine.map.event.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.map.RaptorMap;

public class RMBlockPlaceEvent extends RMBlockEvent
{
    public RMBlockPlaceEvent(RaptorMap map, Player player, Block block)
    {
        super(map, player, block);
    }
}
