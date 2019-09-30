package us.totemsmc.raptorengine.map;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import us.totemsmc.raptorengine.RaptorEngine;
import us.totemsmc.raptorengine.game.GameState;
import us.totemsmc.raptorengine.game.RaptorGame;
import us.totemsmc.raptorengine.game.RaptorTeamGame;
import us.totemsmc.raptorengine.map.event.RaptorMapEvent;
import us.totemsmc.raptorengine.map.event.block.RMBlockBreakEvent;
import us.totemsmc.raptorengine.map.event.block.RMBlockPlaceEvent;
import us.totemsmc.raptorengine.map.event.objective.RMBreakBlockObjectiveCompleteEvent;
import us.totemsmc.raptorengine.map.event.objective.RMPlaceBlockObjectiveCompleteEvent;
import us.totemsmc.raptorengine.map.event.player.RMPlayerDeathEvent;
import us.totemsmc.raptorengine.map.event.player.RMPlayerSpawnEvent;
import us.totemsmc.raptorengine.objective.block.BlockObjective;
import us.totemsmc.raptorengine.objective.block.BreakBlockObjective;
import us.totemsmc.raptorengine.objective.block.PlaceBlockObjective;
import us.totemsmc.raptorengine.objective.block.Spawnpoint;
import us.totemsmc.raptorengine.team.Team;
import us.totemsmc.raptorengine.util.ObjectiveParser;
import us.totemsmc.raptorengine.util.RaptorLogger;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class RaptorMap implements Closeable, Listener
{
    private final String name;
    private final List<BlockObjective> blockObjectives;
    private final RaptorGame game;
    private final Map<Class, Method> eventHandlers;
    private MapState mapState;

    public RaptorMap(String name, Path schematic, RaptorGame game) throws IOException
    {
        Instant start = Instant.now();
        mapState = MapState.CREATING;
        RaptorLogger.debug("Starting creation of map \"" + name + "\" from " + schematic);
        this.name = name;
        RaptorLogger.debug("Map Name: " + name);
        this.game = game;
        RaptorLogger.debug("Game: " + game.getName());

        /*
            GENERATE VOID WORLD
         */
        MVWorldManager manager = ((MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core")).getMVWorldManager();
        manager.addWorld(name, World.Environment.NORMAL, "", WorldType.NORMAL, false, "VoidGenerator", false);
        RaptorLogger.debug("Created empty map \"" + name + "\"");

        /*
            PASTE SCHEMATIC
         */
        RaptorLogger.debug("Pasting map schematic, this can take a few seconds...");
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
                        .to(BlockVector3.at(0, RaptorEngine.config().MAP_HEIGHT, 0))
                        .ignoreAirBlocks(true)
                        .build();
                Operations.completeBlindly(operation);
            }
        }
        RaptorLogger.debug("Pasted map schematic");

        /*
            REGISTER EVENTS AND PASS TO GAME HANDLERS
         */
        Bukkit.getPluginManager().registerEvents(this, RaptorEngine.getPlugin(RaptorEngine.class));
        // register event handlers in game class (the generic way)
        eventHandlers = new HashMap<>();
        Arrays.asList(game.getClass().getDeclaredMethods()).forEach(method ->
        {
            if (method.isAnnotationPresent(RaptorMapEventHandler.class))
            {
                List<Parameter> params = Arrays.asList(method.getParameters());
                params.forEach(param ->
                {
                    if (RaptorMapEvent.class.isAssignableFrom(param.getType()) && params.size() == 1)
                    {
                        eventHandlers.put(param.getType(), method);
                    }
                });
            }
        });
        RaptorLogger.info("Event handlers registered");

        /*
            LOCATE BLOCK OBJECTIVES
         */
        RaptorLogger.debug("Processing block objectives...");
        blockObjectives = new ArrayList<>();
        List<ChunkSnapshot> asyncChunkList = Arrays.stream(getWorld().getLoadedChunks()).map(Chunk::getChunkSnapshot).collect(Collectors.toList());

        List<Team> teams = new ArrayList<>();

        List<Location> signLocations = new ArrayList<>();

        Consumer<Location> syncSignProcessor = (location) ->
        {
            Location realLocation = new Location(getWorld(), location.getX(), location.getY(), location.getZ());
            Sign sign = (Sign) realLocation.getBlock().getState();
            if (ObjectiveParser.isObjective(sign.getLines()))
            {
                realLocation.getBlock().setType(Material.AIR);
            }
            if (ObjectiveParser.isObjectiveFor(sign.getLines(), game))
            {
                Block objectiveBlock;
                Map<String, String> objProps = ObjectiveParser.parseSign(sign.getLines());
                // do team parsing
                if (game instanceof RaptorTeamGame && objProps.containsKey("team"))
                {
                    Team newTeam = new Team(objProps.get("team"));
                    if (!teams.stream().map(team -> team.getName()).collect(Collectors.toList()).contains(newTeam.getName()))
                    {
                        teams.add(newTeam);
                    }
                }
                BlockFace f;
                if (sign.getBlockData() instanceof WallSign)
                {
                    WallSign wallSign = (WallSign) sign.getBlockData();
                    f = wallSign.getFacing().getOppositeFace();
                } else
                {
                    f = BlockFace.DOWN;
                }
                objectiveBlock = getWorld().getBlockAt(realLocation).getRelative(f);
                BlockObjective objective = ObjectiveParser.parseObjective(objectiveBlock, objProps);
                RaptorLogger.debug("OBJECTIVE FOUND: " + objectiveBlock.getType().name() +
                        " at " + objectiveBlock.getX() + ", " + objectiveBlock.getY() + ", " + objectiveBlock.getZ());
                objProps.forEach((type, value) -> RaptorLogger.debug(WordUtils.capitalize(type) + ": " + value));
                blockObjectives.add(objective);
            }
        };

        Runnable asyncChunkIterator = () ->
        {
            asyncChunkList.forEach(chunkSnapshot ->
            {
                for (int x = 0; x < 16; x++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        for (int y = 0; y < chunkSnapshot.getHighestBlockYAt(x, z); y++)
                        {
                            Material t = chunkSnapshot.getBlockType(x, y, z);
                            if (t.name().endsWith("_SIGN") || t.name().endsWith("_WALL_SIGN"))
                            {
                                final int wX = (chunkSnapshot.getX() * 16) + x;
                                final int wY = y;
                                final int wZ = (chunkSnapshot.getZ() * 16) + z;
                                signLocations.add(new Location(null, wX, wY, wZ));
                            }
                        }
                    }
                }
            });
            Bukkit.getScheduler().scheduleSyncDelayedTask(RaptorEngine.getPlugin(RaptorEngine.class), () ->
            {
                signLocations.forEach(syncSignProcessor);
                if (game instanceof RaptorTeamGame)
                {
                    ((RaptorTeamGame) game).setTeams(teams.toArray(new Team[teams.size()]));
                }
                game.setBlockObjectives(blockObjectives.toArray(new BlockObjective[blockObjectives.size()]));
                RaptorLogger.debug("Block objectives processed.");

                setMapState(MapState.READY);
                Duration generationTime = Duration.between(start, Instant.now());
                RaptorLogger.debug("Generation time: " + (generationTime.toMillis() / 1000.0) + " seconds");
            });
        };

        // run the async chunk iterator defined above, which schedules sync processing for each sign.
        Bukkit.getScheduler().runTaskAsynchronously(RaptorEngine.getPlugin(RaptorEngine.class), asyncChunkIterator);
    }

    private void handleEvent(RaptorMapEvent event)
    {
        try
        {
            if (eventHandlers.containsKey(event.getClass()))
            {
                eventHandlers.get(event.getClass()).invoke(game, event);
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event)
    {
        String from = event.getFrom().getName();
        String to = event.getPlayer().getWorld().getName();
        if (to.equalsIgnoreCase(getWorld().getName())) game.playerJoined(event.getPlayer());
        else if (from.equalsIgnoreCase(getWorld().getName())) game.playerQuit(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        World fallback = Bukkit.getWorld(RaptorEngine.config().FALLBACK_WORLD_NAME);
        if (fallback == null) fallback = Bukkit.getWorlds().stream().filter(world -> !world.equals(getWorld())).findAny().orElse(null);
        event.getPlayer().teleport(fallback.getSpawnLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (!player.getWorld().equals(getWorld()) || game.getState() != GameState.RUNNING) return;
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        boolean ranBreakEventAlready = false;
        Runnable breakEvent = () ->
        {
            RMBlockBreakEvent blockBreakEvent = new RMBlockBreakEvent(this, player, block, tool);
            handleEvent(blockBreakEvent);
        };
        for (BlockObjective blockObjective : blockObjectives)
        {
            if (blockObjective instanceof BreakBlockObjective)
            {
                BreakBlockObjective obj = (BreakBlockObjective) blockObjective;
                if (obj.getBlocks().contains(block))
                {
                    Material t = obj.getTool();
                    boolean correctTool = false;
                    if (t == null || tool.getType() == t) correctTool = true;
                    if(!correctTool)
                    {
                        event.setCancelled(true);
                        return;
                    }
                    if(!ranBreakEventAlready)
                    {
                        breakEvent.run();
                        ranBreakEventAlready = true;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RaptorEngine.getPlugin(RaptorEngine.class), () ->
                    {
                        if (obj.getBroken().size() == obj.getBlocks().size() && (obj.isRepeatable() || obj.getTimesCompleted() == 0))
                        {
                            obj.addCompletion();

                            RMBreakBlockObjectiveCompleteEvent objectiveCompleteEvent = new RMBreakBlockObjectiveCompleteEvent(this, obj, player);
                            handleEvent(objectiveCompleteEvent);
                            if (obj.isRepeatable())
                            {
                                obj.getBlocks().forEach(b ->
                                {
                                    BlockState state = b.getState();
                                    state.setBlockData(obj.getOriginalState().getBlockData());
                                    state.update(true);
                                });
                            }
                        }
                    });
                }
            }
        }
        if(!ranBreakEventAlready) breakEvent.run();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (!player.getWorld().equals(getWorld()) || game.getState() != GameState.RUNNING) return;
        RMBlockPlaceEvent placeEvent = new RMBlockPlaceEvent(this, player, block);
        handleEvent(placeEvent);
        for (BlockObjective blockObjective : blockObjectives)
        {
            if (blockObjective instanceof PlaceBlockObjective && blockObjective.getBlocks().contains(block))
            {
                PlaceBlockObjective obj = (PlaceBlockObjective) blockObjective;
                if (obj.getPlacedCorrectly().size() == obj.getBlocks().size() && (obj.isRepeatable() || obj.getTimesCompleted() == 0))
                {
                    obj.addCompletion();
                    RMPlaceBlockObjectiveCompleteEvent objectiveCompleteEvent = new RMPlaceBlockObjectiveCompleteEvent(this, obj, player);
                    handleEvent(objectiveCompleteEvent);
                    if (obj.isRepeatable())
                    {
                        obj.getBlocks().forEach(b -> b.setType(Material.AIR));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player deceased = event.getEntity();
        if (!deceased.getWorld().equals(getWorld()) || game.getState() != GameState.RUNNING) return;
        RaptorLogger.debug("Player died");
        EntityDamageEvent.DamageCause cause = deceased.getLastDamageCause().getCause();
        RMPlayerDeathEvent e = new RMPlayerDeathEvent(this, event.getEntity(), cause);
        handleEvent(e);
    }

    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        Location location = event.getRespawnLocation();
        if (!location.getWorld().equals(getWorld())) return;
        List<Spawnpoint> spawnpoints = blockObjectives.stream()
                .filter(objective -> objective instanceof Spawnpoint).map(objective -> (Spawnpoint) objective).collect(Collectors.toList());
        if (spawnpoints.size() > 0)
        {
            if (game instanceof RaptorTeamGame)
            {
                RaptorTeamGame teamGame = (RaptorTeamGame) game;
                spawnpoints = spawnpoints.stream().filter(spawnpoint ->
                        teamGame.getTeam(spawnpoint.getTeam()).equals(teamGame.getTeam(player))).collect(Collectors.toList());
            }
            Spawnpoint chosen = spawnpoints.get(new Random().nextInt(spawnpoints.size()));
            event.setRespawnLocation(chosen.getSuitableLocation(true));
        }// what happens with no spawnpoints?
        RMPlayerSpawnEvent e = new RMPlayerSpawnEvent(this, player, event.getRespawnLocation());
        handleEvent(e);
    }

    private void setMapState(MapState state)
    {
        RaptorLogger.debug(name + ": " + state.name());
        mapState = state;
    }

    public RaptorGame getGame()
    {
        return game;
    }

    public MapState getState()
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
        setMapState(MapState.DELETING);
        // end game if it's not already done
        if (game.getState() == GameState.RUNNING) game.stop();
        // unload events
        eventHandlers.clear();
        HandlerList.unregisterAll(this);
        // remove players
        // delete world
        MVWorldManager manager = ((MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core")).getMVWorldManager();
        manager.deleteWorld(name);
    }
}
