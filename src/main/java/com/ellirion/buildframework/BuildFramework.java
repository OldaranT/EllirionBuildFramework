package com.ellirion.buildframework;

import com.ellirion.buildframework.templateengine.command.CommandCreateTemplateHologram;
import com.ellirion.buildframework.templateengine.command.CommandAddMarker;
import com.ellirion.buildframework.templateengine.command.CommandExportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandImportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandPutTemplate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.ellirion.buildframework.templateengine.command.CommandCreateTemplate;
import com.ellirion.buildframework.templateengine.command.CommandRemoveHologram;
import com.ellirion.buildframework.templateengine.command.CommandRemoveMarker;

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
        getCommand("AddMarker").setExecutor(new CommandAddMarker());
        getCommand("RemoveMarker").setExecutor(new CommandRemoveMarker());
        getCommand("CreateHologram").setExecutor(new CommandCreateTemplateHologram());
        getCommand("RemoveHologram").setExecutor(new CommandRemoveHologram());
        createConfig();
        getLogger().info("BuildFramework is enabled.");
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
        config.options().header("Ellirion-BuildFramework configuration file");
        config.addDefault("TerrainValidation_OverheadLimit", 0);
        config.addDefault("templatePath", "plugins/Ellirion/BuildFramework/templates/");
        config.addDefault("DOOR", 0);
        config.addDefault("PATH", 1);
        config.addDefault("GROUND", 2);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }
}
