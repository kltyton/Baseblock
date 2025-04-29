package com.kltyton.baseblock.item;

import com.kltyton.baseblock.Baseblock;
import com.kltyton.baseblock.util.TurretBlockUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class TurretBlock extends ItemEx {
    public static final NamespacedKey CANNON_KEY = new NamespacedKey(Baseblock.PLUGIN_NAME, "cannon_block");
    public static final NamespacedKey CANNON_MODE_KEY = new NamespacedKey(Baseblock.PLUGIN_NAME, "cannon_mode");
    public static final NamespacedKey CANNON_OWNER_KEY = new NamespacedKey(Baseblock.PLUGIN_NAME, "cannon_owner");
    public static final NamespacedKey CANNON_LEVEL_KEY = new NamespacedKey(Baseblock.PLUGIN_NAME, "level");
    public static final NamespacedKey CANNON_HP_KEY = new NamespacedKey(Baseblock.PLUGIN_NAME, "hp");
    public static final NamespacedKey CANNON_ATTACK_CD = new NamespacedKey(Baseblock.PLUGIN_NAME, "cd");
    public static final int CUSTOM_MODEL_DATA = 114514;
    @Override
    public Material getMaterial() {
        return Material.END_STONE_BRICKS;
    }

    @Override
    public int getCode() {
        return -1;
    }

    @Override
    public String getDisplayName() {
        return "§5§l哨戒炮";
    }
    public ItemStack createCannonBlockItem() {
        ItemStack item = new ItemStack(Material.DISPENSER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getDisplayName());
        meta.setCustomModelData(CUSTOM_MODEL_DATA);
        //lore.add(ChatColor.GRAY + "/give @p dispenser{display:{Name:'{\"text\":\"自动炮台\",\"color\":\"red\"}',Lore:['{\"text\":\"放置后自动攻击周围生物\",\"color\":\"gray\"}']},CustomModelData:114514,PublicBukkitValues:{\"baseblock:cannon_block\":1}}\n");
        meta.setLore(getLore());
        ///give @p dispenser{display:{Name:'{"text":"自动炮台","color":"red"}',Lore:['{"text":"放置后自动攻击周围生物","color":"gray"}']},CustomModelData:114514,PublicBukkitValues:{"baseblock:cannon_block":1}}
        // 隐藏原版属性
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        // 数据
        meta.getPersistentDataContainer().set(CANNON_KEY, PersistentDataType.INTEGER, 1);
        meta.getPersistentDataContainer().set(CANNON_LEVEL_KEY, PersistentDataType.INTEGER, 1);
        meta.getPersistentDataContainer().set(CANNON_HP_KEY, PersistentDataType.INTEGER, 40);
        meta.getPersistentDataContainer().set(CANNON_ATTACK_CD, PersistentDataType.INTEGER, 60);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemStack create(int amount){
        ItemStack item = createCannonBlockItem();
        item.setAmount(amount);
        return item;
    }
    @Override
    public boolean is(ItemStack other){
        return super.is(other) && TurretBlockUtils.isCannonBlock(other);
    }
    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("§a§l效果:攻击来犯的敌人");
        lore.add("§r§8放置在基地内生效");
        return lore;
    }
}
