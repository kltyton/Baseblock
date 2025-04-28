package com.kltyton.baseblock.block;

import com.kltyton.baseblock.item.CannonBlockItem;
import com.kltyton.baseblock.task.CannonAttackTask;
import com.kltyton.baseblock.util.CannonBlockUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataType;

public class CannonBlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (CannonBlockUtils.isCannonBlock(event.getItemInHand())) {
            Block block = event.getBlock();
            if (block.getState() instanceof TileState) {
                TileState state = (TileState) block.getState();
                state.getPersistentDataContainer().set(
                        CannonBlockItem.CANNON_KEY,
                        PersistentDataType.INTEGER,
                        1
                );
                // 保存主人UUID
                state.getPersistentDataContainer().set(
                        CannonBlockItem.CANNON_OWNER_KEY,
                        PersistentDataType.STRING,
                        event.getPlayer().getUniqueId().toString()
                );

                // 设置默认模式
                state.getPersistentDataContainer().set(
                        CannonBlockItem.CANNON_MODE_KEY,
                        PersistentDataType.INTEGER,
                        1
                );
                state.update();
                CannonAttackTask.addCannonLocation(block.getLocation());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (CannonBlockUtils.isCannonBlock(event.getBlock())) {
            if (event.getBlock().getState() instanceof TileState) {
                TileState state = (TileState) event.getBlock().getState();
                state.getPersistentDataContainer().remove(CannonBlockItem.CANNON_KEY);
                state.update();
                CannonAttackTask.removeCannonLocation(event.getBlock().getLocation());
            }
        }
    }
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        // 加载时注册所有有效炮台
        for (BlockState state : chunk.getTileEntities()) {
            if (CannonBlockUtils.isCannonBlock(state.getBlock())) {
                CannonAttackTask.addCannonLocation(state.getLocation());
            }
        }
    }
}

