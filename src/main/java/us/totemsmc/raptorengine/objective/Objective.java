package us.totemsmc.raptorengine.objective;

import org.bukkit.Location;
import org.bukkit.Material;

public class Objective
{
    private final Material material;
    private boolean located;
    private Location location;

    public Objective(Material material)
    {
        this.material = material;
        located = false;
        location = null;
    }

    public Material getMaterial()
    {
        return material;
    }

    public boolean isLocated()
    {
        return located;
    }

    public void setLocated(boolean located)
    {
        this.located = located;
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
