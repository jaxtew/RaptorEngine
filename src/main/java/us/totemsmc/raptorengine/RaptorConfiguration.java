package us.totemsmc.raptorengine;

import org.bukkit.Material;
import us.totemsmc.raptorengine.api.JSONConfiguration;

public class RaptorConfiguration extends JSONConfiguration
{
    public static Boolean DEBUG;
    public static Integer MAP_HEIGHT;
    public static Material OBJECTIVE_INDICATOR;

    @Override
    public void checkFields()
    {
        if(DEBUG == null) DEBUG = true;
        if(MAP_HEIGHT == null) MAP_HEIGHT = 70;
        if(OBJECTIVE_INDICATOR == null) OBJECTIVE_INDICATOR = Material.BEDROCK;
    }
}
