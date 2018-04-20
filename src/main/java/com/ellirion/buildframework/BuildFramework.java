package com.ellirion.buildframework;

import com.ellirion.buildframework.templateengine.command.CommandExportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandImportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandPutTemplate;
import com.ellirion.buildframework.pathfinder.command.CommandPathFind;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.ellirion.buildframework.templateengine.command.CommandCreateTemplate;

public class BuildFramework extends JavaPlugin {

    private static BuildFramework instance;
    private FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        instance = this;
        getCommand("CreateTemplate").setExecutor(new CommandCreateTemplate());
        getCommand("PutTemplate").setExecutor(new CommandPutTemplate());
        getCommand("ExportTemplate").setExecutor(new CommandExportTemplate());
        getCommand("ImportTemplate").setExecutor(new CommandImportTemplate());
        getCommand("pf").setExecutor(new CommandPathFind());
        getLogger().info("[Ellirion] BuildFramework is enabled.");
        createConfig();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("BuildFramework is disabled.");
    }

    /**
     * @return BuildFramework instance
     */
    public static BuildFramework getInstance() {
        if (instance == null) {
            instance = new BuildFramework();
        }
        return instance;
    }

    private void createConfig() {
        this.config.options().header("Ellirion-BuildFramework configuration file");
        this.config.addDefault("TerrainValidation_OverheadLimit", 0);
        this.config.addDefault("templatePath", "plugins/Ellirion/BuildFramework/templates/");
        this.config.options().copyDefaults(true);
        this.saveConfig();
        this.reloadConfig();
    }
}
