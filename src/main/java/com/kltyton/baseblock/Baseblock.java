package com.kltyton.baseblock;

import com.kltyton.baseblock.block.CannonBlockListener;
import com.kltyton.baseblock.task.CannonAttackTask;
import org.bukkit.plugin.java.JavaPlugin;

public final class Baseblock extends JavaPlugin {
    public static final String PLUGIN_NAME = "baseblock";

    @Override
    public void onEnable() {
        //炮台监听
        getServer().getPluginManager().registerEvents(new CannonBlockListener(), this);

        CannonAttackTask.loadExistingCannons();
        // 启动攻击任务（60tick/次）
        new CannonAttackTask().runTaskTimer(this, 0, 60);
    }

    @Override
    public void onDisable() {

    }
}
