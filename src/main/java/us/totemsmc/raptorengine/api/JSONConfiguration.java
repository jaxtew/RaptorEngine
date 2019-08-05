package us.totemsmc.raptorengine.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class JSONConfiguration
{
    transient Path configPath;

    public abstract void checkFields();

    final void setConfigPath(Path path)
    {
        this.configPath = path;
    }

    public Path getConfigPath()
    {
        return configPath;
    }

    public static void save(JSONConfiguration configuration) throws IOException
    {
        try (BufferedWriter writer = Files.newBufferedWriter(configuration.getConfigPath()))
        {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(configuration));
        }
    }

    public static <T extends JSONConfiguration> T load(Class<T> configurationType, Path path) throws IOException
    {
        if(Files.notExists(path))
        {
            try
            {
                T configuration = configurationType.newInstance();
                configuration.setConfigPath(path);
                configuration.checkFields();
                save(configuration);
            } catch (Exception e)
            {
                Bukkit.getLogger().severe("Internal: failed to save default configuration file");
                e.printStackTrace();
                return null;
            }
        }
        try(BufferedReader reader = Files.newBufferedReader(path))
        {
            T configuration = new Gson().fromJson(reader, configurationType);
            configuration.setConfigPath(path);
            configuration.checkFields();
            return configuration;
        }
    }
}
