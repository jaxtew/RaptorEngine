package us.totemsmc.raptorengine.objective.block;

import org.bukkit.block.Block;
import us.totemsmc.raptorengine.objective.RaptorObjective;
import us.totemsmc.raptorengine.util.BlockUtil;

import java.util.ArrayList;
import java.util.List;

public class BlockObjective extends RaptorObjective
{
    private final List<Block> blocks;
    private final boolean isChained;
    private final String team;
    private final boolean repeatable;

    public BlockObjective(String name, Block block, boolean chain, String team, boolean repeatable)
    {
        super(name);
        this.blocks = new ArrayList<>();
//        RaptorLogger.debug("Chain: " + chain);
        if (chain) blocks.addAll(BlockUtil.getConnectedBlocks(block));
        else blocks.add(block);
        this.isChained = chain;
        this.team = team;
        this.repeatable = repeatable;
    }

    public String getTeam()
    {
        return team;
    }

    public boolean isChained()
    {
        return isChained;
    }

    public List<Block> getBlocks()
    {
        return blocks;
    }

    public boolean isRepeatable()
    {
        return repeatable;
    }
}
