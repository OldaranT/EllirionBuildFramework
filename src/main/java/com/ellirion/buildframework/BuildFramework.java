package com.ellirion.buildframework;


import com.ellirion.buildframework.terraincorrector.command.Test;
import com.ellirion.buildframework.terraincorrector.command.ValidateCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BuildFramework extends JavaPlugin {

    private static BuildFramework instance;
    private FileConfiguration config = getConfig();


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
        getCommand("Validate").setExecutor(new ValidateCommand());
        createConfig();
        getCommand("test").setExecutor(new Test());

    }

    @Override
    public void onDisable() {
        getLogger().info("[Ellirion] BuildFramework is disabled.");
    }

    private void createConfig() {

        config.options().header("Ellirion-BuildFramework configuration file");
        config.addDefault("TerrainValidation_OverheadLimit", 0);
        config.addDefault("TerrainValidation_BocksLimit", 0);
        config.addDefault("TerrainValidation_TotalLimit", 0);
        config.addDefault("TerrainValidation_offset", 5);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }
}
