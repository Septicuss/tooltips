package fi.septicuss.tooltips.managers.integration.providers;

import fi.septicuss.tooltips.managers.integration.wrappers.Title;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface PacketProvider extends Provider {

    @Nonnull Optional<Title<? extends PacketProvider>> createTitle(String titleJson, String subtitleJson, int fadeIn, int stay, int fadeOut);

}
