package us.totemsmc.raptorengine.map.event.objective;

import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.map.RaptorMap;
import us.totemsmc.raptorengine.objective.block.PlaceBlockObjective;

public class RMPlaceBlockObjectiveCompleteEvent extends RMBlockObjectiveCompleteEvent
{
    public RMPlaceBlockObjectiveCompleteEvent(RaptorMap map, PlaceBlockObjective objective, Player player)
    {
        super(map, objective, player);
    }
}
