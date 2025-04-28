package com.kltyton.baseblock.util;

import com.kltyton.baseblock.item.CannonBlockItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;

public class CannonBlockUtils {
    public static boolean isCannonBlock(ItemStack item) {
        return item != null
                && item.getType() == Material.DISPENSER
                && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(CannonBlockItem.CANNON_KEY);
    }

    public static boolean isCannonBlock(Block block) {
        if (block.getType() != Material.DISPENSER) return false;
        if (!(block.getState() instanceof TileState)) return false;

        TileState state = (TileState) block.getState();
        return state.getPersistentDataContainer().has(CannonBlockItem.CANNON_KEY);
    }
}

