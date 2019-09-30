package us.totemsmc.raptorengine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class JSONConfiguration
{
    public static void save(JSONConfiguration configuration, Path path) throws IOException
    {
        try (BufferedWriter writer = Files.newBufferedWriter(path))
        {
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(configuration);
            writer.write(json);
        }
    }

    public static <T extends JSONConfiguration> T load(Class<T> configurationType, Path path) throws IOException
    {
        if (Files.notExists(path))
        {
            RaptorLogger.info("Creating default configuration");
            try
            {
                T configuration = configurationType.newInstance();
                configuration.checkFields();
                save(configuration, path);
            } catch (Exception e)
            {
                RaptorLogger.severe("Internal: failed to save default configuration file");
                e.printStackTrace();
                return null;
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(path))
        {
            T configuration = new Gson().fromJson(reader, configurationType);
            configuration.checkFields();
            return configuration;
        }
    }

    public abstract void checkFields();
}
