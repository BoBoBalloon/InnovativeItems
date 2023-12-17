package me.boboballoon.innovativeitems.functions.arguments;

import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class that represents a var-arg as the last argument in a function that returns a list of the object returned in the constructors functional interface
 */
public final class ExpectedVarArg implements ExpectedArguments {
    private final ExpectedArguments parse;
    private final Consumer<FunctionContext> onError;

    public ExpectedVarArg(@NotNull ExpectedArguments parse, @NotNull Consumer<FunctionContext> onError) {
        this.parse = parse;
        this.onError = onError;
    }

    public ExpectedVarArg(@NotNull ExpectedArguments parse) {
        this(parse, context -> {
            String argumentType = context.getFunction() instanceof Keyword ? "keyword" : context.getFunction() instanceof Condition ? "condition" : "function";
            LogUtil.log(LogUtil.Level.WARNING, "Line number " + context.getLineNumber() + " on " + argumentType + " " +  context.getFunction().getIdentifier() + " on ability " + context.getAbilityName() + " was unable to be parsed... Are you sure you provided the correct data type and that you follow proper var-arg syntax?");
        });
    }

    @Override
    @Nullable
    public List<Object> getValue(@NotNull String rawValue, @NotNull FunctionContext context) {
        String[] args = Arrays.stream(rawValue.split(",")).map(String::trim).toArray(String[]::new);
        List<Object> parsed = new ArrayList<>(args.length);

        for (String raw : args) {
            Object parse;
            try {
                parse = this.parse.getValue(raw, context);

                if (parse == null) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }

            parsed.add(parse);
        }

        return parsed;
    }

    @Override
    @NotNull
    public Consumer<FunctionContext> getOnError() {
        return this.onError;
    }
}