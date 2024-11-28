package fi.septicuss.tooltips.managers.preset.animation;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import org.bukkit.entity.Player;

public record ParsedAnimation(Animation animation, String text, Arguments arguments) {

    public int id() {
        final Argument idArgument = this.arguments().get("id");
        if (idArgument == null || !idArgument.isNumber())
            return -1;
        return idArgument.getAsInt();
    }


    public void tick(Player player) {
        this.animation().tick(player);
    }

    public void skip() {
        this.animation().skip();
    }

    public boolean finished() {
        return this.animation().finished();
    }

    public String text() {
        return this.animation().text();
    }

}
