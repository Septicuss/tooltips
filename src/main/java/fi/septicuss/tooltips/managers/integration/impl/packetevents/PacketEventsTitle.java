package fi.septicuss.tooltips.managers.integration.impl.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleSubtitle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleText;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleTimes;
import fi.septicuss.tooltips.managers.title.Title;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PacketEventsTitle extends Title<PacketEventsPacketProvider> {

    private final ArrayList<PacketWrapper<?>> packets = new ArrayList<>();

    public PacketEventsTitle(PacketEventsPacketProvider packetProvider, String titleJson, String subtitleJson, int fadeIn, int stay, int fadeOut) {
        super(packetProvider, titleJson, subtitleJson, fadeIn, stay, fadeOut);
    }

    @Override
    public void preparePackets() {
        packets.clear();
        packets.add(new WrapperPlayServerSetTitleTimes(getFadeIn(), getStay(), getFadeOut()));
        packets.add(new WrapperPlayServerSetTitleText(getTitleJson()));
        packets.add(new WrapperPlayServerSetTitleSubtitle(getSubtitleJson()));
    }

    @Override
    public void send(Player player) {
        final var user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        for (var packet : this.packets) {
            user.sendPacket(packet);
        }
    }

}
