package com.ellirion.buildframework;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BuildFramework extends JavaPlugin {

    private static BuildFramework INSTANCE;
    
    private BuildFramework() {
    }

    /***
     *
     * @return instance
     */
    public static BuildFramework getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BuildFramework();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        getLogger().info("[Ellirion] BuildFramework is enabled.");
        createConfig();

    }

    @Override
    public void onDisable() {
        getLogger().info("[Ellirion] BuildFramework is disabled.");
    }

    private void createConfig() {
        final File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            getLogger().info("config.yml not found, creating!");
            saveConfig();
        } else {
            getLogger().info("config.yml found, loading!");
        }
    }

}
