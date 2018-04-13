package com.ellirion.buildframework;

import org.bukkit.plugin.java.JavaPlugin;

public class BuildFramework extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("[Ellirion] BuildFramework is enabled.");
    }
    @Override
    public void onDisable() {
        getLogger().info("[Ellirion] BuildFramework is disabled.");
    }
}
