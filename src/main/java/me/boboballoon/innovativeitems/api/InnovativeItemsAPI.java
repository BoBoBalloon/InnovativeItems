package me.boboballoon.innovativeitems.api;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.keywords.KeywordManager;

/**
 * A class used to easily retrieve internal information about the plugin
 */
public class InnovativeItemsAPI {
    /**
     * A method used to return the active instance of the keyword manager
     *
     * @return the active instance of the keyword manager
     */
    public static KeywordManager getKeywordManager() {
        return InnovativeItems.getInstance().getKeywordManager();
    }

    /**
     * A method used to return the active instance of the config manager
     *
     * @return the active instance of the config manager
     */
    public static ConfigManager getConfigManager() {
        return InnovativeItems.getInstance().getConfigManager();
    }

    /**
     * A method used to return the active instance of the garbage collector
     *
     * @return the active instance of the garbage collector
     */
    public static GarbageCollector getGarbageCollector() {
        return InnovativeItems.getInstance().getGarbageCollector();
    }
}
