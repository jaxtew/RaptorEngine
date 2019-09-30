package us.totemsmc.raptorengine.util;

import us.totemsmc.raptorengine.RaptorConfiguration;
import us.totemsmc.raptorengine.RaptorEngine;

import java.util.logging.Level;

public class RaptorLogger
{
    public static void debug(String message)
    {
        if(RaptorEngine.config().DEBUG) info(message);
    }

    public static void info(String message)
    {
        log(Level.INFO, message);
    }

    public static void warning(String message)
    {
        log(Level.WARNING, message);
    }

    public static void severe(String message)
    {
        log(Level.SEVERE, message);
    }

    public static void log(Level level, String message)
    {
        RaptorEngine.getPlugin(RaptorEngine.class).getLogger().log(level, message);
    }
}
