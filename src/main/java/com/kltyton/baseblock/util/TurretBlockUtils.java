package com.kltyton.baseblock.util;

import com.kltyton.baseblock.item.TurretBlock;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class TurretBlockUtils {
    public static boolean isCannonBlock(ItemStack item) {
        return item != null
                && item.getType() == Material.DISPENSER
                && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(TurretBlock.CANNON_KEY);
    }

    public static boolean isCannonBlock(Block block) {
        if (block.getType() != Material.DISPENSER) return false;
        if (!(block.getState() instanceof TileState)) return false;

        TileState state = (TileState) block.getState();
        return state.getPersistentDataContainer().has(TurretBlock.CANNON_KEY);
    }
    public static int getCannonBlockMode(TileState state) {
        return state.getPersistentDataContainer().getOrDefault(TurretBlock.CANNON_MODE_KEY, PersistentDataType.INTEGER, 1);
    }
    public static void setCannonBlockMode(TileState state, int mode) {
        state.getPersistentDataContainer().set(TurretBlock.CANNON_MODE_KEY, PersistentDataType.INTEGER, mode);
    }
    public static int getCannonBlockLevel(TileState state) {
        return state.getPersistentDataContainer().getOrDefault(TurretBlock.CANNON_LEVEL_KEY, PersistentDataType.INTEGER, 1);
    }
    public static void setCannonBlockLevel(TileState state, int level) {
        state.getPersistentDataContainer().set(TurretBlock.CANNON_LEVEL_KEY, PersistentDataType.INTEGER, level);
    }

    public static int getCannonBlockHp(TileState state) {
        return state.getPersistentDataContainer().getOrDefault(TurretBlock.CANNON_HP_KEY, PersistentDataType.INTEGER, 40);
    }
    public static void setCannonBlockHp(TileState state, int hp) {
        state.getPersistentDataContainer().set(TurretBlock.CANNON_HP_KEY, PersistentDataType.INTEGER, hp);
    }

    public static int getCannonBlockAttackCd(TileState state) {
        return state.getPersistentDataContainer().getOrDefault(TurretBlock.CANNON_ATTACK_CD, PersistentDataType.INTEGER, 60);
    }
    public static void setCannonBlockAttackCd(TileState state, int cd) {
        state.getPersistentDataContainer().set(TurretBlock.CANNON_ATTACK_CD, PersistentDataType.INTEGER, cd);
    }
    public static String getCannonBlockOwner(TileState state) {
        return state.getPersistentDataContainer().get(TurretBlock.CANNON_OWNER_KEY, PersistentDataType.STRING);
    }
    public static void setCannonBlockOwner(TileState state, String owner) {
        state.getPersistentDataContainer().set(TurretBlock.CANNON_OWNER_KEY, PersistentDataType.STRING, owner);
    }
    // 序列化方法
    public Document serializeTurret(Location loc, TileState state) {
        return new Document()
                .append("_id", String.format("%s:%d:%d:%d",
                        loc.getWorld().getName(),
                        loc.getBlockX(),
                        loc.getBlockY(),
                        loc.getBlockZ()))
                .append("location", new Document()
                        .append("world", loc.getWorld().getName())
                        .append("x", loc.getBlockX())
                        .append("y", loc.getBlockY())
                        .append("z", loc.getBlockZ()))
                .append("data", new Document()
                        .append("mode", TurretBlockUtils.getCannonBlockMode(state))
                        .append("level", TurretBlockUtils.getCannonBlockLevel(state))
                        .append("hp", TurretBlockUtils.getCannonBlockHp(state))
                        .append("cd", TurretBlockUtils.getCannonBlockAttackCd(state))
                        .append("owner", TurretBlockUtils.getCannonBlockOwner(state)));
    }

    // 反序列化方法
    public void deserializeTurret(Document doc, TileState state) {
        Document data = doc.get("data", Document.class);

        TurretBlockUtils.setCannonBlockMode(state, data.getInteger("mode"));
        TurretBlockUtils.setCannonBlockLevel(state, data.getInteger("level"));
        TurretBlockUtils.setCannonBlockHp(state, data.getInteger("hp"));
        TurretBlockUtils.setCannonBlockAttackCd(state, data.getInteger("cd"));
        TurretBlockUtils.setCannonBlockOwner(state, data.getString("owner"));
        state.update();
    }
}

