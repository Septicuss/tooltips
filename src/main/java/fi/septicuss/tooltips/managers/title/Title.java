package fi.septicuss.tooltips.managers.title;

import fi.septicuss.tooltips.managers.integration.providers.PacketProvider;
import org.bukkit.entity.Player;

public abstract class Title <T extends PacketProvider> {

    private final T packetProvider;
    private final String titleJson;
    private final String subtitleJson;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public Title(T packetProvider, String titleJson, String subtitleJson, int fadeIn, int stay, int fadeOut) {
        this.packetProvider = packetProvider;
        this.titleJson = titleJson;
        this.subtitleJson = subtitleJson;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public T getPacketProvider() {
        return packetProvider;
    }

    public String getTitleJson() {
        return titleJson;
    }

    public String getSubtitleJson() {
        return subtitleJson;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public abstract void preparePackets();

    public abstract void send(Player player);

}
