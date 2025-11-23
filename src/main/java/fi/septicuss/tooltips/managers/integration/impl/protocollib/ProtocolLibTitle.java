package fi.septicuss.tooltips.managers.integration.impl.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ProtocolLibTitle extends Title<ProtocolLibPacketProvider> {

    private final ProtocolManager protocolManager;
    private final List<PacketContainer> packets = new ArrayList<>();
    private boolean setup = false;

    public ProtocolLibTitle(ProtocolLibPacketProvider packetProvider, String titleJson, String subtitleJson, int fadeIn, int stay, int fadeOut) {
        super(packetProvider, titleJson, subtitleJson, fadeIn, stay, fadeOut);

        this.protocolManager = packetProvider.getProtocolManager();
    }

    @Override
    public void preparePackets() {
        var titlePacket = new PacketContainer(PacketType.Play.Server.SET_TITLE_TEXT);
        titlePacket.getChatComponents().write(0, WrappedChatComponent.fromJson(getTitleJson()));

        var subtitlePacket = new PacketContainer(PacketType.Play.Server.SET_SUBTITLE_TEXT);
        subtitlePacket.getChatComponents().write(0, WrappedChatComponent.fromJson(getSubtitleJson()));

        var timePacket = new PacketContainer(PacketType.Play.Server.SET_TITLES_ANIMATION);
        timePacket.getIntegers().write(0, getFadeIn()).write(1, getStay()).write(2, getFadeOut());

        packets.add(timePacket);
        packets.add(titlePacket);
        packets.add(subtitlePacket);

        this.setup = true;
    }

    @Override
    public void send(Player player) {
        if (!setup) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Tooltips.get(), () -> {
            if (player == null || !player.isOnline())
                return;

            for (PacketContainer packet : this.packets) {
                protocolManager.sendServerPacket(player, packet);
            }
        });

    }
}
