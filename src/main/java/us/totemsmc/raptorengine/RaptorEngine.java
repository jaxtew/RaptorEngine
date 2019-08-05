package us.totemsmc.raptorengine;

import org.bukkit.plugin.java.JavaPlugin;
import us.totemsmc.raptorengine.api.JSONConfiguration;
import us.totemsmc.raptorengine.map.RaptorMap;

import java.io.IOException;

public class RaptorEngine extends JavaPlugin
{
    private static RaptorConfiguration configuration;

    private RaptorMap map = null;

    @Override
    public void onLoad()
    {
        getDataFolder().mkdir();
        try
        {
            configuration = JSONConfiguration.load(RaptorConfiguration.class, getDataFolder().toPath().resolve(
                    "config.json"));
            if (RaptorConfiguration.DEBUG) getLogger().info("Loaded configuration");
        } catch (IOException e)
        {
            getLogger().severe("Failed to load configuration");
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
//                    map = new RaptorMap("test",
//                            getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder().toPath().resolve(
//                                    "schematics").resolve("test_map.schem"), new TestRaptorGame());
//                } catch (IOException e)
//                {
//                    e.printStackTrace();
//                } catch (WorldEditException e)
//                {
//                    e.printStackTrace();
//                }
//            } else
//            {
//                map.close();
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
            JSONConfiguration.save(configuration);
            if (RaptorConfiguration.DEBUG) getLogger().info("Saved configuration");
        } catch (IOException e)
        {
            getLogger().severe("Failed to save configuration");
            e.printStackTrace();
        }
    }

    public static RaptorConfiguration getConfiguration()
    {
        return configuration;
    }
}
