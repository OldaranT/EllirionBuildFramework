package com.ellirion.buildframework;

import org.bukkit.plugin.java.JavaPlugin;
import com.ellirion.buildframework.templateengine.command.CommandCreateTemplate;

public class BuildFramework extends JavaPlugin {
    private static BuildFramework instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("[Ellirion] BuildFramework is enabled.");
        getCommand("CreateTemplate").setExecutor(new CommandCreateTemplate());
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
