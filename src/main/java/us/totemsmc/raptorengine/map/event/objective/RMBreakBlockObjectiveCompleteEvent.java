package us.totemsmc.raptorengine.map.event.objective;

import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.map.RaptorMap;
import us.totemsmc.raptorengine.objective.block.BreakBlockObjective;

public class RMBreakBlockObjectiveCompleteEvent extends RMBlockObjectiveCompleteEvent
{
    public RMBreakBlockObjectiveCompleteEvent(RaptorMap map, BreakBlockObjective objective, Player player)
    {
        super(map, objective, player);
    }
}
