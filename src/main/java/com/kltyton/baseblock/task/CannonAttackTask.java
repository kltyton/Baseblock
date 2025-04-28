package com.kltyton.baseblock.task;

import com.kltyton.baseblock.item.CannonBlockItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class CannonAttackTask extends BukkitRunnable {
    private static final Set<Location> cannonLocations = new HashSet<>();
    private static final int RANGE = 6;

    @Override
    public void run() {
        for (Location loc : new ArrayList<>(cannonLocations)) {
            Block block = loc.getBlock();
            if (block.getType() != Material.DISPENSER) {
                cannonLocations.remove(loc);
                continue;
            }

            attackEntities(loc);
        }
    }

    private void attackEntities(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        // 获取炮台中心坐标
        Location cannonCenter = center.clone().add(0.5, 0.5, 0.5);

        // 获取范围内实体并锁定最近目标
        List<LivingEntity> targets = world.getNearbyEntities(
                        cannonCenter, RANGE, RANGE, RANGE,
                        e -> e instanceof LivingEntity && !(e instanceof ArmorStand)
                ).stream()
                .map(e -> (LivingEntity) e)
                .sorted(Comparator.comparingDouble(e ->
                        e.getLocation().distanceSquared(cannonCenter)))
                .limit(1) // 锁定最近的一个目标
                .toList();

        if (targets.isEmpty()) return;

        LivingEntity target = targets.get(0);
        // 获取目标中心坐标
        Location targetCenter = target.getEyeLocation().subtract(0, 0.25, 0);

        // 执行攻击
        target.damage(5.0);//伤害
        drawLine(cannonCenter, targetCenter, Particle.FLAME);
    }
    private void drawLine(Location start, Location end, Particle particle) {
        // 增加高度修正（保持直线可见性）
        start = start.clone().add(0, 0.25, 0);
        end = end.clone().add(0, 0.1, 0);

        World world = start.getWorld();
        Vector direction = end.toVector().subtract(start.toVector());
        double distance = direction.length();
        direction.normalize();

        // 优化粒子密度参数
        double step = 0.15;
        int particles = (int) (distance / step);
        Vector increment = direction.multiply(step);

        Location current = start.clone();
        for (int i = 0; i < particles; i++) {
            world.spawnParticle(
                    particle,
                    current,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.02
            );
            current.add(increment);
        }
    }
    public static void addCannonLocation(Location loc) {
        cannonLocations.add(loc);
    }

    public static void removeCannonLocation(Location loc) {
        cannonLocations.remove(loc);
    }

    public static void loadExistingCannons() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState state : chunk.getTileEntities()) {
                    if (state.getType() == Material.DISPENSER &&
                            state instanceof TileState) {
                        TileState tileState = (TileState) state;
                        if (tileState.getPersistentDataContainer().has(CannonBlockItem.CANNON_KEY)) {
                            cannonLocations.add(state.getLocation());
                        }
                    }
                }
            }
        }
    }
}

