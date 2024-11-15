package fi.septicuss.tooltips.managers.integration.impl.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fi.septicuss.tooltips.managers.integration.providers.PacketProvider;
import fi.septicuss.tooltips.managers.title.Title;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ProtocolLibPacketProvider implements PacketProvider {

    private final ProtocolManager protocolManager;

    public ProtocolLibPacketProvider() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public ProtocolManager getProtocolManager() {
        return this.protocolManager;
    }

    @Override
    public @Nonnull Optional<Title<?>> createTitle(String titleJson, String subtitleJson, int fadeIn, int stay, int fadeOut) {
        return Optional.of(new ProtocolLibTitle(this, titleJson, subtitleJson, fadeIn, stay, fadeOut));
    }

    @Override
    public String identifier() {
        return "ProtocolLib";
    }
}
