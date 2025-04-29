package com.kltyton.baseblock.block;

import com.kltyton.baseblock.item.TurretBlock;
import com.kltyton.baseblock.task.TurretBlockAttackTask;
import com.kltyton.baseblock.util.TurretBlockUtils;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TurretBlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (TurretBlockUtils.isCannonBlock(event.getItemInHand())) {
            Block block = event.getBlock();
            if (block.getState() instanceof TileState state) {
                @NotNull PersistentDataContainer itemData = state.getPersistentDataContainer();
                state.getPersistentDataContainer().set(
                        TurretBlock.CANNON_KEY,
                        PersistentDataType.INTEGER,
                        itemData.has(TurretBlock.CANNON_KEY, PersistentDataType.INTEGER) ? Objects.requireNonNull(itemData.get(TurretBlock.CANNON_KEY, PersistentDataType.INTEGER)) : 1
                );
                // 保存主人UUID
                state.getPersistentDataContainer().set(
                        TurretBlock.CANNON_OWNER_KEY,
                        PersistentDataType.STRING,
                        itemData.has(TurretBlock.CANNON_OWNER_KEY, PersistentDataType.STRING) ? Objects.requireNonNull(itemData.get(TurretBlock.CANNON_OWNER_KEY, PersistentDataType.STRING)) : event.getPlayer().getUniqueId().toString()
                );

                // 设置默认模式
                state.getPersistentDataContainer().set(
                        TurretBlock.CANNON_MODE_KEY,
                        PersistentDataType.INTEGER,
                        itemData.has(TurretBlock.CANNON_MODE_KEY, PersistentDataType.INTEGER) ? Objects.requireNonNull(itemData.get(TurretBlock.CANNON_MODE_KEY, PersistentDataType.INTEGER)) : 1
                );

                state.getPersistentDataContainer().set(
                        TurretBlock.CANNON_LEVEL_KEY,
                        PersistentDataType.INTEGER,
                        itemData.has(TurretBlock.CANNON_LEVEL_KEY, PersistentDataType.INTEGER) ? Objects.requireNonNull(itemData.get(TurretBlock.CANNON_LEVEL_KEY, PersistentDataType.INTEGER)) : 1
                );

                state.getPersistentDataContainer().set(
                        TurretBlock.CANNON_ATTACK_CD,
                        PersistentDataType.INTEGER,
                        itemData.has(TurretBlock.CANNON_ATTACK_CD, PersistentDataType.INTEGER) ? Objects.requireNonNull(itemData.get(TurretBlock.CANNON_ATTACK_CD, PersistentDataType.INTEGER)) : 60
                );

                state.getPersistentDataContainer().set(
                        TurretBlock.CANNON_HP_KEY,
                        PersistentDataType.INTEGER,
                        itemData.has(TurretBlock.CANNON_HP_KEY, PersistentDataType.INTEGER) ? Objects.requireNonNull(itemData.get(TurretBlock.CANNON_HP_KEY, PersistentDataType.INTEGER)) : 40
                );
                state.update();
                TurretBlockAttackTask.addCannonLocation(block.getLocation());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!TurretBlockUtils.isCannonBlock(block)) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (!(block.getState() instanceof TileState)) return;
        TileState state = (TileState) block.getState();

        // 获取当前HP
        int currentHp = TurretBlockUtils.getCannonBlockHp(state);
        if (currentHp <= 0) {
            block.getWorld().spawnParticle(Particle.CLOUD,
                    block.getLocation().clone().add(0.5, 0.5, 0.5),
                    30,
                    0.5,
                    0.5,
                    0.5,
                    0.2
            );
            state.getPersistentDataContainer().remove(TurretBlock.CANNON_KEY);
            state.update();
            TurretBlockAttackTask.removeCannonLocation(block.getLocation());
            block.breakNaturally(); // 掉落
            return;
        }

        // 计算伤害值
        ItemStack tool = player.getInventory().getItemInMainHand();
        int damage = calculateDamage(tool.getType());

        if (damage == 0) {
            player.sendActionBar(ChatColor.RED + "你需要使用镐类工具来破坏炮台！");
            return;
        }

        // 更新HP
        int newHp = currentHp - damage;
        TurretBlockUtils.setCannonBlockHp(state, newHp);
        state.update();

/*        // 发送提示信息
        String message = ChatColor.GOLD + "炮台受到攻击 [" +
                (newHp > 0 ? ChatColor.GREEN : ChatColor.RED) +
                newHp + ChatColor.GOLD + "/" +
                TurretBlockUtils.getCannonBlockHp(state) + "]";
        player.sendActionBar(message);*/

        if (newHp <= 0) {
            block.getWorld().spawnParticle(Particle.CLOUD,
                    block.getLocation().clone().add(0.5, 0.5, 0.5),
                    30,
                    0.5,
                    0.5,
                    0.5,
                    0.2
            );
            state.getPersistentDataContainer().remove(TurretBlock.CANNON_KEY);
            state.update();
            TurretBlockAttackTask.removeCannonLocation(block.getLocation());
            block.breakNaturally(); // 掉落
        }
    }

    private int calculateDamage(Material tool) {
        return switch (tool) {
            case WOODEN_PICKAXE -> 1;
            case STONE_PICKAXE -> 2;
            case IRON_PICKAXE -> 3;
            case GOLDEN_PICKAXE -> 4;
            case DIAMOND_PICKAXE -> 4;
            case NETHERITE_PICKAXE -> 5;
            default -> tool.name().endsWith("_PICKAXE") ? 1 : 0;
        };
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        // 加载时注册所有有效炮台
        for (BlockState state : chunk.getTileEntities()) {
            if (TurretBlockUtils.isCannonBlock(state.getBlock())) {
                TurretBlockAttackTask.addCannonLocation(state.getLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || !TurretBlockUtils.isCannonBlock(block)) return;

        Player player = event.getPlayer();
        TileState state = (TileState) block.getState();

        // 验证所有权
        String ownerUUID = TurretBlockUtils.getCannonBlockOwner(state);

        if (!player.getUniqueId().toString().equals(ownerUUID)) {
            player.sendActionBar(ChatColor.RED + "你没有权限修改这个炮台！");
            event.setCancelled(true);
            return;
        }

        // 切换模式
        int currentMode = TurretBlockUtils.getCannonBlockMode(state);
        int newMode = currentMode % 3 + 1;
        TurretBlockUtils.setCannonBlockMode(state, newMode);
        state.update();

        player.sendActionBar(ChatColor.GOLD + "炮台模式已切换至模式 " + newMode);
        event.setCancelled(true);
    }
}

