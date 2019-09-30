package us.totemsmc.raptorengine.map.event.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.totemsmc.raptorengine.map.RaptorMap;

public class RMBlockBreakEvent extends RMBlockEvent
{
    private final BlockState previousState;
    private final ItemStack tool;

    public RMBlockBreakEvent(RaptorMap map, Player player, Block block, ItemStack tool)
    {
        super(map, player, block);
        this.previousState = block.getState();
        this.tool = tool;
    }

    public ItemStack getTool()
    {
        return tool;
    }

    public BlockState getPreviousState()
    {
        return previousState;
    }
}
