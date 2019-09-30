package us.totemsmc.raptorengine;

import us.totemsmc.raptorengine.util.JSONConfiguration;
import us.totemsmc.raptorengine.util.RaptorLogger;

public class RaptorConfiguration extends JSONConfiguration
{
    public Boolean DEBUG;
    public Integer MAP_HEIGHT;
    public String FALLBACK_WORLD_NAME;

    @Override
    public void checkFields()
    {
        if (DEBUG == null) DEBUG = true;
        if (MAP_HEIGHT == null) MAP_HEIGHT = 64;
        if (FALLBACK_WORLD_NAME == null) FALLBACK_WORLD_NAME = "world";
    }
}
