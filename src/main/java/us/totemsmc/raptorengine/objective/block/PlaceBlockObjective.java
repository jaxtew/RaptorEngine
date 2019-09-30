package us.totemsmc.raptorengine.objective.block;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceBlockObjective extends BlockObjective
{
    private final Material blockType;

    public PlaceBlockObjective(String name, Block block, boolean chain, String team, Material blockType, boolean repeatable)
    {
        super(name, block, chain, team, repeatable);
        this.blockType = blockType;
        getBlocks().forEach(b -> b.setType(Material.AIR));
    }

    public Material getBlockType()
    {
        return blockType;
    }

    public List<Block> getPlacedAny()
    {
        return getBlocks().stream().filter(block -> block.getType() != Material.AIR).collect(Collectors.toList());
    }

    public List<Block> getPlacedCorrectly()
    {
        return getBlocks().stream().filter(block -> block.getType() == blockType).collect(Collectors.toList());
    }
}
