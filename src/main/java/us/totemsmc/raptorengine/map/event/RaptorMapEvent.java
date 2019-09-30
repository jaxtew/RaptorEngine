package us.totemsmc.raptorengine.map.event;

import us.totemsmc.raptorengine.map.RaptorMap;

public class RaptorMapEvent
{
    private final RaptorMap map;

    protected RaptorMapEvent(RaptorMap map)
    {
        this.map = map;
    }

    public final RaptorMap getMap()
    {
        return map;
    }
}
