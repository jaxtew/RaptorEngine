package us.totemsmc.raptorengine.map.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.totemsmc.raptorengine.map.RaptorMap;

public class RMPlayerSpawnEvent extends RMPlayerEvent
{
    private Location location;

    public RMPlayerSpawnEvent(RaptorMap map, Player player, Location location)
    {
        super(map, player);
        this.location = location;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }
}
