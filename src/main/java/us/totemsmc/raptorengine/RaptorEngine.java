package us.totemsmc.raptorengine;

import org.bukkit.plugin.java.JavaPlugin;
import us.totemsmc.raptorengine.util.JSONConfiguration;
import us.totemsmc.raptorengine.util.RaptorLogger;

import java.io.IOException;
import java.nio.file.Path;

public class RaptorEngine extends JavaPlugin
{
    private static RaptorConfiguration configuration;
    private Path configFile;

//    private RaptorMap map = null;

    @Override
    public void onLoad()
    {
        getDataFolder().mkdir();
        configFile = getDataFolder().toPath().resolve("config.json");
        try
        {
            configuration = JSONConfiguration.load(RaptorConfiguration.class, configFile);
            RaptorLogger.debug("Loaded configuration");
        } catch (IOException e)
        {
            RaptorLogger.severe("Failed to load configuration");
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable()
    {
//        getCommand("raptor").setExecutor(((sender, command, label, args) ->
//        {
//            if (map == null)
//            {
//                try
//                {
//                    sender.sendMessage("Creating map...");
//                    map = new RaptorMap("testgame",
//                            getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder().toPath().resolve(
//                                    "schematics").resolve("testmap.schem"), new TestGame());
//                    sender.sendMessage("Done.");
//                } catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            } else
//            {
//                if(args.length > 0)
//                {
//                    map.getGame().start();
//                }else
//                {
//                    sender.sendMessage("Closing map...");
//                    map.close();
//                    map = null;
//                    sender.sendMessage("Done.");
//                }
//            }
//            return true;
//        }));
    }

    @Override
    public void onDisable()
    {
        saveConfig();
    }

    @Override
    public void saveConfig()
    {
        try
        {
            JSONConfiguration.save(configuration, configFile);
            RaptorLogger.debug("Saved configuration");
        } catch (IOException e)
        {
            RaptorLogger.severe("Failed to save configuration");
            e.printStackTrace();
        }
    }

    public static RaptorConfiguration config()
    {
        return configuration;
    }
}
