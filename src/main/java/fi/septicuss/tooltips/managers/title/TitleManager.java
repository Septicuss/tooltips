package fi.septicuss.tooltips.managers.title;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.integration.providers.PacketProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.Title;
import fi.septicuss.tooltips.utils.AdventureUtils;
import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;
import java.util.Optional;

public class TitleManager {

    private final Tooltips plugin;

    public TitleManager(Tooltips plugin) {
        this.plugin = plugin;
    }

    public Optional<Title<? extends PacketProvider>> newTitle(String titleJson, String subtitleJson, int fadeIn, int stay, int fadeOut) {
        final PacketProvider packetProvider = plugin.getIntegrationManager().getPacketProvider();

        if (packetProvider == null) {
            return Optional.empty();
        }

        final Optional<Title<? extends PacketProvider>> optionalTitle = packetProvider.createTitle(titleJson, subtitleJson, fadeIn, stay, fadeOut);
        optionalTitle.ifPresent(Title::preparePackets);

        return optionalTitle;
    }

    public Optional<Title<? extends PacketProvider>> newTitle(Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        return this.newTitle(AdventureUtils.GSONSERIALIZER.serialize(title), AdventureUtils.GSONSERIALIZER.serialize(subtitle), fadeIn, stay, fadeOut);
    }

}
