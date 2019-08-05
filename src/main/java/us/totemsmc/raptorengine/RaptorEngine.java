package us.totemsmc.raptorengine;

import org.bukkit.plugin.java.JavaPlugin;
import us.totemsmc.raptorengine.api.JSONConfiguration;

import java.io.IOException;

public class RaptorEngine extends JavaPlugin
{
    private RaptorConfiguration configuration;

    @Override
    public void onLoad()
    {
        getDataFolder().mkdir();
        try
        {
            configuration = JSONConfiguration.load(RaptorConfiguration.class, getDataFolder().toPath().resolve(
                    "config.json"));
            if(configuration.DEBUG) getLogger().info("Loaded configuration");
        } catch (IOException e)
        {
            getLogger().severe("Failed to load configuration");
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable()
    {

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
            if(configuration.DEBUG) getLogger().info("Saved configuration");
        } catch (IOException e)
        {
            getLogger().severe("Failed to save configuration");
            e.printStackTrace();
        }
    }

    public RaptorConfiguration getConfiguration()
    {
        return configuration;
    }
}
