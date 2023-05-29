package fi.septicuss.tooltips.utils.title;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import fi.septicuss.tooltips.Tooltips;

public class Title {

	private ProtocolManager protocolManager;
	private List<PacketContainer> packets;

	public Title(ProtocolManager protocolManager, WrappedChatComponent title, WrappedChatComponent subtitle, int fadeIn,
			int stay, int fadeOut) {
		this.protocolManager = protocolManager;
		this.packets = new ArrayList<>();

		PacketContainer titlePacket = new PacketContainer(PacketType.Play.Server.SET_TITLE_TEXT);
		titlePacket.getChatComponents().write(0, title);

		PacketContainer subtitlePacket = new PacketContainer(PacketType.Play.Server.SET_SUBTITLE_TEXT);
		subtitlePacket.getChatComponents().write(0, subtitle);

		PacketContainer time = new PacketContainer(PacketType.Play.Server.SET_TITLES_ANIMATION);
		time.getIntegers().write(0, fadeIn).write(1, stay).write(2, fadeOut);

		packets.add(time);
		packets.add(titlePacket);
		packets.add(subtitlePacket);
	}

	public void send(Player player) {
		Bukkit.getScheduler().runTaskAsynchronously(Tooltips.get(), () -> {
			try {
				for (PacketContainer packet : packets) {
					protocolManager.sendServerPacket(player, packet);
				}
			} catch (InvocationTargetException e) {
				Tooltips.warn("Failed to send title packets");
				e.printStackTrace();
			}
		});

	}

}
