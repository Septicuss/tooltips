package fi.septicuss.tooltips.managers.preset.functions.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import fi.septicuss.tooltips.utils.Expr;
import fi.septicuss.tooltips.utils.Utils;
import org.bukkit.entity.Player;

import java.util.List;

public class IfFunction implements Function {

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        if (args.isEmpty() || args.size() < 3) return "";

        final Argument conditionArgument = args.get(0).process(player);
        final String string = conditionArgument.getAsString();

        final Expr.Builder expressionBuilder = new Expr.Builder();
        boolean result = true;

        if (this.needsParsing(expressionBuilder, string)) {
            float evaluated = parse(expressionBuilder, string);
            result = evaluated == 1;
        } else {
            result = Boolean.parseBoolean(conditionArgument.getAsString());
        }

        if (result) {
            return args.get(1).process(player).getAsString();
        }

        return args.get(2).process(player).getAsString();
    }

    private boolean needsParsing(Expr.Builder expressionBuilder, String value) {
        final List<String> tokens = expressionBuilder.tokenize(value);
        final Expr expression = expressionBuilder.parse(value, null, null);

        boolean fullyNumeric = true;

        if (tokens != null)
            for (var token : tokens) {
                if (Expr.Builder.OPS.containsKey(token))
                    continue;
                if (!Utils.isNumeric(token)) {
                    fullyNumeric = false;
                    break;
                }
            }

        if (tokens == null || expression == null || !fullyNumeric) {
            return false;
        }

        return true;
    }

    private float parse(Expr.Builder expressionBuilder, String value) {
        return expressionBuilder.parse(value, null, null).eval();
    }

}
