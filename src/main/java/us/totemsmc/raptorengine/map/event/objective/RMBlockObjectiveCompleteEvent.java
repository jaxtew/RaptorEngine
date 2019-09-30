package us.totemsmc.raptorengine.map.event.objective;

import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.map.RaptorMap;
import us.totemsmc.raptorengine.map.event.RaptorMapEvent;
import us.totemsmc.raptorengine.objective.block.BlockObjective;

public class RMBlockObjectiveCompleteEvent extends RaptorMapEvent
{
    private final BlockObjective objective;
    private final Player player;

    RMBlockObjectiveCompleteEvent(RaptorMap map, BlockObjective objective, Player player)
    {
        super(map);
        this.objective = objective;
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    public BlockObjective getObjective()
    {
        return objective;
    }
}
