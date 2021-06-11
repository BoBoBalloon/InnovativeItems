package me.boboballoon.innovativeitems.keywords;

import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.logging.Level;

public final class KeywordManager {
    private final HashMap<String, Keyword> keywords;

    public KeywordManager() {
        this.keywords = new HashMap<>();
        LogUtil.logUnblocked(Level.INFO, "Keyword manager initialized!");
        //unblocked because debug level is null
    }

    /**
     * A method used to register a new keyword in the cache
     *
     * @param keyword the keyword you wish to register
     */
    public void registerKeyword(@NotNull Keyword keyword) {
        String identifier = keyword.getIdentifier();

        if (this.contains(identifier)) {
            //unblocked because debug level may be null at this point
            LogUtil.logUnblocked(Level.WARNING, "Keyword with the identifier of " + keyword + ", is already registered! Skipping...");
            return;
        }

        this.keywords.put(identifier, keyword);
    }

    /**
     * A method used to register new keywords in the cache
     *
     * @param keywords all the keywords you wish to register
     */
    public void registerKeywords(@NotNull Keyword... keywords) {
        for (Keyword keyword : keywords) {
            this.registerKeyword(keyword);
        }
    }

    /**
     * A method used to get a keyword already registered in the cache
     *
     * @param identifier the identifier of the keyword
     * @return the keyword
     */
    @Nullable
    public Keyword getKeyword(@NotNull String identifier) {
        return this.keywords.get(identifier);
    }

    /**
     * A method used to check whether the cache contains a keyword with the provided identifier
     *
     * @param identifier the identifier of the keyword
     * @return a boolean that is true when said identifier is present
     */
    public boolean contains(String identifier) {
        return (this.keywords.containsKey(identifier));
    }
}
