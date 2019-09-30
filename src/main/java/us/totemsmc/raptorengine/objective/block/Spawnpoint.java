package us.totemsmc.raptorengine.objective.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spawnpoint extends BlockObjective
{
    private final Block originalBlock;
    private final float yaw;

    public Spawnpoint(String name, Block block, String team, boolean chain, float yaw)
    {
        super(name, block, chain, team, false);
        originalBlock = block;
        this.yaw = yaw;
    }

    public Location getSuitableLocation(boolean random)
    {
        Block chosenBlock = originalBlock;
        if (!random)
        {
            if (isSuitable(originalBlock))
            {
                chosenBlock = originalBlock;
            } else
            {
                for (Block b : getSuitableAround(originalBlock))
                {
                    if (isSuitable(b)) chosenBlock = b;
                }
            }
        } else
        {
            Random rand = new Random();
            Block chosenOne = getBlocks().get(rand.nextInt(getBlocks().size()));
            List<Block> suitable = getSuitableAround(chosenOne);
            if (suitable.size() > 0)
            {
                chosenBlock = suitable.get(rand.nextInt(suitable.size()));
            }
        }
        Block spawn = chosenBlock.getRelative(BlockFace.UP);
        Location location = spawn.getLocation();
        location.add(0.5, 0, 0.5); // center on block
        location.setYaw(yaw);
        return location;
    }

    public Block getOriginalBlock()
    {
        return originalBlock;
    }

    private List<Block> getSuitableAround(Block block)
    {
        List<Block> suitable = new ArrayList<>();
        for (BlockFace face : BlockFace.values())
        {
            Block relative = block.getRelative(face);
            if (isSuitable(relative))
            {
                suitable.add(relative);
            }
        }
        return suitable;
    }

    private boolean isSuitable(Block block)
    {
        Block above = block.getRelative(BlockFace.UP);
        return above.getType() == Material.AIR &&
                above.getRelative(BlockFace.UP).getType() == Material.AIR &&
                block.getType() != Material.AIR;
    }
}
