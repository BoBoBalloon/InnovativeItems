package me.boboballoon.innovativeitems.util;

import me.boboballoon.innovativeitems.InnovativeItems;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * A class used to check for possible plugin updates
 */
public class UpdateChecker {
    private final PluginDescriptionFile description;

    private static final String RESOURCE_ID = InnovativeItems.isPluginPremium() ? "93570" : "96456";

    public UpdateChecker(Plugin plugin) {
        this.description = plugin.getDescription();
    }

    /**
     * A method that will compare the current version of the plugin and the newest version of the plugin and will notify console if the current version of the plugin is out of date
     */
    public void checkForUpdates() {
        String currentVersion = this.description.getVersion();
        String newestVersion = UpdateChecker.getNewestVersion();

        if (currentVersion.equals(newestVersion)) {
            //if is up to date
            LogUtil.logUnblocked(LogUtil.Level.INFO, "You are currently running the newest version of the plugin!");
            return;
        }

        //if is out of date
        LogUtil.logUnblocked(LogUtil.Level.INFO, "You are currently running an outdated version of this plugin. You are currently on version " + currentVersion + ", while the newest version is version " + newestVersion + "!");
    }

    /**
     * A method that returns a string that is the newest version of the plugin
     *
     * @return a string that is the newest version of the plugin
     */
    public static String getNewestVersion() {
        InputStream input;
        try {
            input = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID).openStream();
        } catch (IOException e) {
            LogUtil.log(LogUtil.Level.SEVERE, "There was an issue while trying to make an api call to spigot's version api! Please contact the developer of the plugin!");
            e.printStackTrace();
            return null;
        }

        Scanner scanner = new Scanner(input);

        String version = scanner.hasNext() ? scanner.next() : null;

        scanner.close();

        return version;
    }
}
