package fi.septicuss.tooltips.managers.preset.animation.impl;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.animation.Animation;
import fi.septicuss.tooltips.managers.preset.animation.AnimationProvider;
import org.bukkit.entity.Player;

public class StaticAnimationProvider implements AnimationProvider {
    @Override
    public Animation create(String text, Arguments arguments) {
        return new StaticAnimation(text);
    }

    record StaticAnimation(String text) implements Animation {
        @Override
        public void tick(Player player) {}

        @Override
        public void skip() {}

        @Override
        public boolean finished() {
            return true;
        }
    }
}
