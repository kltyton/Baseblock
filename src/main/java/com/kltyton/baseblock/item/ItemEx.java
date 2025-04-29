package com.kltyton.baseblock.item;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class ItemEx {
    private final int code;
    private final ItemStack item;

    public ItemEx() {
        this.item = new ItemStack(getMaterial());
        this.code = getCode();

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getDisplayName());
        meta.setCustomModelData(code);
        meta.setLore(getLore());
        item.setItemMeta(meta);
    }
    public abstract Material getMaterial();
    public abstract int getCode();
    public abstract String getDisplayName();
    public abstract List<String> getLore();
    public ItemStack create(int amount){
        item.setAmount(amount);
        return item;
    }
    public boolean is(ItemStack other){
        if (other == null)return false;
        ItemMeta other_meta = other.getItemMeta();
        if (other_meta.hasCustomModelData()){
            if (getCode() == -1)
                return other.getType() == getMaterial();
            return other_meta.getCustomModelData() == getCode();
        }
        return false;
    }
    public boolean is(Block other){
        return other.getType() == getMaterial();
    }
}

