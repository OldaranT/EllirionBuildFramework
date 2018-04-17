package com.ellirion.buildframework;

import com.ellirion.buildframework.terraincorrector.command.Test;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BuildFramework extends JavaPlugin {

    private static final BuildFramework INSTANCE = new BuildFramework();

    private BuildFramework() {
    }

    /***
     *
     * @return instance
     */
    public static BuildFramework getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        getLogger().info("[Ellirion] BuildFramework is enabled.");
        createConfig();
        getCommand("test").setExecutor(new Test());

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
