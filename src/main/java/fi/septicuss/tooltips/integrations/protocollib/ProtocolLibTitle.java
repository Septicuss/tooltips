package fi.septicuss.tooltips.integrations.protocollib;

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
import fi.septicuss.tooltips.integrations.PacketProvider;
import fi.septicuss.tooltips.integrations.Title;

public class ProtocolLibTitle extends Title {

	private ProtocolManager protocolManager;
	private List<PacketContainer> packets;
	private boolean setup = false;
	
	public ProtocolLibTitle(PacketProvider packetProvider, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		super(packetProvider, title, subtitle, fadeIn, stay, fadeOut);
		
		if (!(packetProvider instanceof ProtocolLibPacketProvider protocolLib))
			return;
		
		this.protocolManager = protocolLib.getProtocolManager();
		this.packets = new ArrayList<>();

		PacketContainer titlePacket = new PacketContainer(PacketType.Play.Server.SET_TITLE_TEXT);
		titlePacket.getChatComponents().write(0, WrappedChatComponent.fromJson(title));

		PacketContainer subtitlePacket = new PacketContainer(PacketType.Play.Server.SET_SUBTITLE_TEXT);
		subtitlePacket.getChatComponents().write(0, WrappedChatComponent.fromJson(subtitle));

		PacketContainer time = new PacketContainer(PacketType.Play.Server.SET_TITLES_ANIMATION);
		time.getIntegers().write(0, fadeIn).write(1, stay).write(2, fadeOut);

		packets.add(time);
		packets.add(titlePacket);
		packets.add(subtitlePacket);
		
		this.setup = true;
	}

	@Override
	public void send(Player player) {
		if (!setup)
			return;
		
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
