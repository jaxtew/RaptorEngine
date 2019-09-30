package us.totemsmc.raptorengine.objective.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.List;
import java.util.stream.Collectors;

public class BreakBlockObjective extends BlockObjective
{
    private final Material tool;
    private final BlockState originalState;

    public BreakBlockObjective(String name, Block block, boolean chain, String team, Material tool, boolean repeatable)
    {
        super(name, block, chain, team, repeatable);
        this.tool = tool;
        this.originalState = block.getState();
    }

    public Material getTool()
    {
        return tool;
    }

    public boolean isBroken()
    {
        return getPercentBroken() == 100;
    }

    public int getPercentBroken()
    {
        return Math.round(getBlocks().stream().filter(block -> block.getType() == Material.AIR).count() / getBlocks().size()) * 100;
    }

    public List<Block> getBroken()
    {
        return getBlocks().stream().filter(b -> b.getType() == Material.AIR).collect(Collectors.toList());
    }

    public BlockState getOriginalState()
    {
        return originalState;
    }
}
