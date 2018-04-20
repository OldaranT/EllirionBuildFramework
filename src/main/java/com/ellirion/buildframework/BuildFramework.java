package com.ellirion.buildframework;

import com.ellirion.buildframework.templateengine.command.CommandExportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandImportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandPutTemplate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.ellirion.buildframework.templateengine.command.CommandCreateTemplate;

public class BuildFramework extends JavaPlugin {

    private static BuildFramework instance;
    private FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        instance = this;
        this.getCommand("CreateTemplate").setExecutor(new CommandCreateTemplate());
        this.getCommand("PutTemplate").setExecutor(new CommandPutTemplate());
        this.getCommand("ExportTemplate").setExecutor(new CommandExportTemplate());
        this.getCommand("ImportTemplate").setExecutor(new CommandImportTemplate());
        this.createConfig();
        this.getLogger().info("BuildFramework is enabled.");
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
