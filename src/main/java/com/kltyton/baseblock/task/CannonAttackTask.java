package com.kltyton.baseblock.task;

import com.kltyton.baseblock.item.CannonBlockItem;
import com.kltyton.baseblock.util.CannonBlockUtils;
import com.kltyton.baseblock.util.RayCastUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class CannonAttackTask extends BukkitRunnable {
    private static final Set<Location> cannonLocations = new HashSet<>();
    private static final int RANGE = 6;
    private static final int DAMAGE = 5;

    @Override
    public void run() {
        for (Location loc : new ArrayList<>(cannonLocations)) {
            Block block = loc.getBlock();
            if (!CannonBlockUtils.isCannonBlock(block)) {
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
        TileState tileState = (TileState) center.getBlock().getState();
        int mode = tileState.getPersistentDataContainer().getOrDefault(CannonBlockItem.CANNON_MODE_KEY, PersistentDataType.INTEGER, 1);
        UUID owner = UUID.fromString(Objects.requireNonNull(tileState.getPersistentDataContainer().get(CannonBlockItem.CANNON_OWNER_KEY, PersistentDataType.STRING)));
        // 获取范围内实体并锁定最近目标
        List<LivingEntity> targets = world.getNearbyEntities(cannonCenter, RANGE, RANGE, RANGE)
                .stream()
                .filter(e -> {
                    if (!(e instanceof LivingEntity) || e instanceof ArmorStand) return false;
                    return switch (mode) {
                        case 1 -> attackMode1(e,owner);//模式1
                        case 2 -> attackMode2(e.getType());//模式2
                        case 3 -> attackMode3(e.getType());//模式3
                        default -> true;
                    };
                })
                .map(e -> (LivingEntity) e)
                .sorted(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(cannonCenter)))
                .limit(1) // 锁定最近的一个目标
                .toList();

        if (targets.isEmpty()) return;

        LivingEntity target = targets.get(0);
        // 获取目标中心坐标
        Location targetCenter = target.getEyeLocation().subtract(0, 0.25, 0);
        // 添加射线检测
        RayCastUtil.RayCastResult result = RayCastUtil.hasClearPath(cannonCenter, targetCenter);

        if (!result.hasClearPath) {
            // 绘制到碰撞点的粒子线
            drawLine(cannonCenter, result.hitLocation, Particle.FLAME);
            return;
        }
        // 执行攻击
        target.damage(DAMAGE);//伤害
        drawLine(cannonCenter, targetCenter, Particle.FLAME);
    }

    //模式修改
    private boolean attackMode1(Entity entity,UUID owner) {
        return !(entity instanceof Player) || !entity.getUniqueId().equals(owner);
    }
    private boolean attackMode2(EntityType type) {
        return true;
    }
    private boolean attackMode3(EntityType type) {
        return true;
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
                    if (!CannonBlockUtils.isCannonBlock(state.getBlock())) {
                        cannonLocations.add(state.getLocation());
                    }
                }
            }
        }
    }
}

