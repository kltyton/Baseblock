package com.kltyton.baseblock.item;

import com.kltyton.baseblock.Baseblock;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CannonBlockItem {
    public static final NamespacedKey CANNON_KEY = new NamespacedKey(Baseblock.PLUGIN_NAME, "cannon_block");
    public static final NamespacedKey CANNON_MODE_KEY = new NamespacedKey(Baseblock.PLUGIN_NAME, "cannon_mode");
    public static final NamespacedKey CANNON_OWNER_KEY = new NamespacedKey(Baseblock.PLUGIN_NAME, "cannon_owner");
    public static final int CUSTOM_MODEL_DATA = 114514;

    public ItemStack createCannonBlockItem() {
        ItemStack item = new ItemStack(Material.DISPENSER);
        ItemMeta meta = item.getItemMeta();

        // 基础设置
        meta.setDisplayName(ChatColor.RED + "自动炮台");
        meta.setCustomModelData(CUSTOM_MODEL_DATA);

        // 添加Lore描述
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "放置后自动攻击范围内生物");
        lore.add(ChatColor.GRAY + "/give @p dispenser{display:{Name:'{\"text\":\"自动炮台\",\"color\":\"red\"}',Lore:['{\"text\":\"放置后自动攻击周围生物\",\"color\":\"gray\"}']},CustomModelData:114514,PublicBukkitValues:{\"baseblock:cannon_block\":1}}\n");
        meta.setLore(lore);

        // 隐藏原版属性
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);

        // 持久化数据
        meta.getPersistentDataContainer().set(CANNON_KEY, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        return item;
    }
}
