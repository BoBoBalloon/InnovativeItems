package me.boboballoon.innovativeitems;

import org.bukkit.plugin.java.JavaPlugin;

public final class InnovativeItems extends JavaPlugin {
    private static InnovativeItems instance;

    @Override
    public void onEnable() {
        InnovativeItems.instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static InnovativeItems getInstance() {
        return InnovativeItems.instance;
    }
}
