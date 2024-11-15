package fi.septicuss.tooltips.managers.integration.impl.packetevents;

import fi.septicuss.tooltips.managers.integration.providers.PacketProvider;
import fi.septicuss.tooltips.managers.title.Title;

import javax.annotation.Nonnull;
import java.util.Optional;

public class PacketEventsPacketProvider implements PacketProvider {

    public PacketEventsPacketProvider() {

    }

    @Override
    public @Nonnull Optional<Title<?>> createTitle(String titleJson, String subtitleJson, int fadeIn, int stay, int fadeOut) {
        return Optional.of(new PacketEventsTitle(this, titleJson, subtitleJson, fadeIn, stay, fadeOut));
    }

    @Override
    public String identifier() {
        return "packetevents";
    }
}
