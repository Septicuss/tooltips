package fi.septicuss.tooltips.managers.preset.animation;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;

public interface AnimationProvider {

    Animation create(String text, Arguments arguments);

}
