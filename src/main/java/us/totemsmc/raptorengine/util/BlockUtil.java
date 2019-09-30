package us.totemsmc.raptorengine.util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BlockUtil
{
    private static final BlockFace[] FACES = new BlockFace[]{BlockFace.UP, BlockFace.DOWN,
            BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    public static Set<Block> getConnectedBlocks(Block block) {
        Set<Block> set = new HashSet<>();
        LinkedList<Block> list = new LinkedList<>();

        //Add the current block to the list of blocks that are yet to be done
        list.add(block);
//        set.add(block);

        //Execute this method for each block in the 'todo' list
        while((block = list.poll()) != null) {
            getConnectedBlocks(block, set, list);
        }
        return set;
    }

    public static void getConnectedBlocks(Block block, Set<Block> results, List<Block> todo)
    {
        //Here I collect all blocks that are directly connected to variable 'block'.
        //(Shouldn't be more than 6, because a block has 6 sides)
        Set<Block> result = results;

        //Loop through all block faces (All 6 sides around the block)
        for (BlockFace face : FACES)
        {
            Block b = block.getRelative(face);
            //Check if they're both of the same type
            if (b.getType() == block.getType())
            {
                //Add the block if it wasn't added already
                if (result.add(b))
                {
                    //Add this block to the list of blocks that are yet to be done.
                    todo.add(b);
                }
            }
        }
    }
}
