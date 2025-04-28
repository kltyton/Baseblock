package com.kltyton.baseblock.util;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RayCastUtil {
    // 可穿透的方块集合
    private static final Set<Material> TRANSPARENT_MATERIALS = new HashSet<>(Arrays.asList(
            Material.AIR, Material.WATER, Material.LAVA,
            Material.GLASS, Material.GLASS_PANE, Material.IRON_BARS,
            Material.SLIME_BLOCK, Material.HONEY_BLOCK, Material.DISPENSER
    ));

    public static class RayCastResult {
        public final boolean hasClearPath;
        public final Location hitLocation;

        public RayCastResult(boolean hasClearPath, Location hitLocation) {
            this.hasClearPath = hasClearPath;
            this.hitLocation = hitLocation;
        }
    }

    public static RayCastResult hasClearPath(Location start, Location end) {
        World world = start.getWorld();
        if (world == null || !world.equals(end.getWorld())) {
            return new RayCastResult(false, start.clone());
        }

        Vector direction = end.toVector().subtract(start.toVector());
        double maxDistance = direction.length();
        direction = direction.normalize();

        // 射线步进检测
        Location current = start.clone();
        double step = 0.1; // 精度参数（可调整）
        double traveled = 0;

        while (traveled < maxDistance) {
            Block block = current.getBlock();

            // 当遇到第一个非透明方块时停止
            if (!isTransparent(block.getType())) {
                return new RayCastResult(false, current.clone());
            }

            // 步进移动
            current.add(direction.clone().multiply(step));
            traveled += step;
        }

        return new RayCastResult(true, end.clone());
    }

    private static boolean isTransparent(Material material) {
        return TRANSPARENT_MATERIALS.contains(material) ||
                material.isTransparent();
    }
}
