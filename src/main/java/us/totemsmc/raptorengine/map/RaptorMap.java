package us.totemsmc.raptorengine.map;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import us.totemsmc.raptorengine.RaptorConfiguration;
import us.totemsmc.raptorengine.api.RaptorGame;
import us.totemsmc.raptorengine.objective.Objective;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RaptorMap implements Closeable, Listener
{
    private final String name;
    private List<Player> spectators;
    private MapState mapState;
    private RaptorGame game;

    public RaptorMap(String name, Path schematic, RaptorGame game) throws IOException, WorldEditException
    {
        mapState = MapState.CREATING;
        this.name = name;
        this.game = game;

        /*
            GENERATE VOID WORLD
         */
        MVWorldManager manager = ((MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core")).getMVWorldManager();
        manager.addWorld(name, World.Environment.NORMAL, "", WorldType.NORMAL, false, "VoidGenerator");

        /*
            PASTE SCHEMATIC
         */
        File file = schematic.toFile();
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file)))
        {
            Clipboard clipboard = reader.read();
            try (EditSession editSession =
                         WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(getWorld()), -1))
            {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(0, RaptorConfiguration.MAP_HEIGHT, 0))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            }
        }

        /*
            LOCATE POINTS OF INTEREST
         */
        Arrays.stream(getWorld().getLoadedChunks()).forEach(chunk ->
        {
            List<Objective> notLocated =
                    game.getObjectives().stream().filter(objective -> !objective.isLocated()).collect(Collectors.toList());
            if (notLocated.size() == 0)
            {
                game.objectivesReady();
                return;
            }
            for (int x = (chunk.getX() << 4); x < (chunk.getX() << 4) + 16; x++)
            {
                for (int y = 0; y < 256; y++)
                {
                    for (int z = (chunk.getZ() << 4); z < (chunk.getZ() << 4) + 15; z++)
                    {
                        Block block = getWorld().getBlockAt(x, y, z);
                        if (block.getType() == RaptorConfiguration.OBJECTIVE_INDICATOR)
                        {
                            Block objectiveBlock = block.getRelative(BlockFace.UP);
                            notLocated.stream().filter(objective -> objective.getMaterial() == objectiveBlock.getType()).forEach(objective ->
                            {
                                if (RaptorConfiguration.DEBUG) Bukkit.getLogger().info(MessageFormat.format("Found " +
                                                "objective at {0}, {1}, {2} of material {3}.", block.getX(), block.getY(),
                                        block.getZ(), block.getType().toString()));
                                objective.setLocation(objectiveBlock.getLocation());
                                objective.setLocated(true);
                            });
                        }
                    }
                }
            }
        });
        mapState = MapState.READY;
        game.start();
    }

    public MapState getMapState()
    {
        return mapState;
    }

    public String getName()
    {
        return name;
    }

    public World getWorld()
    {
        return Bukkit.getWorld(name);
    }

    @Override
    public void close()
    {
        mapState = MapState.DELETING;
        // end game if it's not already done
        game.stop();
        // remove players
        // delete world
        MVWorldManager manager = ((MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core")).getMVWorldManager();
        manager.deleteWorld(name);
    }
}
