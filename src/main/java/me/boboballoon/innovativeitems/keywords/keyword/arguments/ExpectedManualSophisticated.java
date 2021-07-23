package me.boboballoon.innovativeitems.keywords.keyword.arguments;

import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A class used to wrap the ExpectedManual interface to provided support for error messages
 */
public class ExpectedManualSophisticated implements ExpectedArguments {
    private final ExpectedManual manual;
    private final Consumer<KeywordContext> onError;

    public ExpectedManualSophisticated(@NotNull ExpectedManual manual, @NotNull Consumer<KeywordContext> onError) {
        this.manual = manual;
        this.onError = onError;
    }

    public ExpectedManualSophisticated(@NotNull ExpectedManual manual, @NotNull String fieldName) {
        this(manual, context -> LogUtil.logKeywordError(context, fieldName));
    }

    /**
     * A method that returns the method used to get the desired object
     *
     * @return the method used to get the desired object
     */
    public ExpectedManual getManual() {
        return this.manual;
    }

    /**
     * A method that returns the method to be called on if the parsing fails for any reason
     *
     * @return the method to be called on if the parsing fails for any reason
     */
    public Consumer<KeywordContext> getOnError() {
        return this.onError;
    }

    /**
     * A method used to get the object called by the current manual method stored in this object
     *
     * @param rawValue the raw value of the number
     * @param context the context in which the argument is being parsed
     * @return the object called by the current manual method stored in this object
     */
    public Object getValue(String rawValue, KeywordContext context) {
        Object value;
        try {
            value = this.manual.getValue(rawValue, context);
        } catch (Throwable e) {
            this.onError.accept(context);
            return null;
        }

        if (value == null) {
            this.onError.accept(context);
            return null;
        }

        return value;
    }
}
