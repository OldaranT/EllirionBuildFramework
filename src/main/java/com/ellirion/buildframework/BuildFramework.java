package com.ellirion.buildframework;


import com.ellirion.buildframework.templateengine.command.CommandExportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandPutTemplate;
import org.bukkit.plugin.java.JavaPlugin;
import com.ellirion.buildframework.templateengine.command.CommandCreateTemplate;

public class BuildFramework extends JavaPlugin {
    private static BuildFramework instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("[Ellirion] BuildFramework is enabled.");
        getCommand("CreateTemplate").setExecutor(new CommandCreateTemplate());
        getCommand("PutTemplate").setExecutor(new CommandPutTemplate());
        getCommand("ExportTemplate").setExecutor(new CommandExportTemplate());
    }
    @Override
    public void onDisable() {
        getLogger().info("[Ellirion] BuildFramework is disabled.");
    }

    /**
     *
     * @return BuildFramework instance
     */
    public static BuildFramework getInstance() {
        if (instance == null) {
            instance = new BuildFramework();
        }
        return instance;
    }
}
