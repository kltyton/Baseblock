package com.kltyton.baseblock;

import com.kltyton.baseblock.block.TurretBlockListener;
import com.kltyton.baseblock.task.TurretBlockAttackTask;
import org.bukkit.plugin.java.JavaPlugin;

public final class Baseblock extends JavaPlugin {
    public static final String PLUGIN_NAME = "baseblock";

    @Override
    public void onEnable() {
        //炮台监听
        getServer().getPluginManager().registerEvents(new TurretBlockListener(), this);

        TurretBlockAttackTask.loadExistingCannons();
        // 启动攻击任务
        new TurretBlockAttackTask().runTaskTimer(this, 0, 1);
    }

    @Override
    public void onDisable() {

    }
}
