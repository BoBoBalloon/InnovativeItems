package me.boboballoon.innovativeitems.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * A class that represents an ability from the config file that has not been finalized
 */
public class IncompleteAbility {
    private final ConfigurationSection section;

    public IncompleteAbility(ConfigurationSection section) {
        this.section = section;
    }

    /**
     * A method that returns the configuration section of this ability
     *
     * @return the configuration section of this ability
     */
    public ConfigurationSection getSection() {
        return this.section;
    }

    //no idea what is going on
}
