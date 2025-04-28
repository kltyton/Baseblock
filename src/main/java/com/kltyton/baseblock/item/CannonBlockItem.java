package com.kltyton.baseblock.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CannonBlock {
    public ItemStack createCannonBlockItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "炮台");
        meta.setCustomModelData(114514);
        item.setItemMeta(meta);
        return item;
    }
}
