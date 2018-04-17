package com.ellirion.buildframework;

import com.ellirion.buildframework.terraincorrector.command.Test;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BuildFramework extends JavaPlugin {

    private static BuildFramework instance;


    /***
     *
     * @return instance
     */
    public static BuildFramework getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("[Ellirion] BuildFramework is enabled.");
        getCommand("Test").setExecutor(new Test());
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
