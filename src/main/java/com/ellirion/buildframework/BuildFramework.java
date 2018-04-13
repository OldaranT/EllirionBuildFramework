package com.ellirion.buildframework;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BuildFramework extends JavaPlugin {

    @Getter
    private static FileConfiguration config;

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
        config = getConfig();
    }
}
