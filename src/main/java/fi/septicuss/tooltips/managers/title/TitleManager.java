package fi.septicuss.tooltips.managers.title;

import javax.annotation.Nullable;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.integrations.IntegratedPlugin;
import fi.septicuss.tooltips.integrations.PacketProvider;
import fi.septicuss.tooltips.integrations.Title;
import fi.septicuss.tooltips.integrations.packetevents.PacketEventsTitle;
import fi.septicuss.tooltips.integrations.protocollib.ProtocolLibTitle;

public class TitleManager {

	private IntegratedPlugin packetPlugin;
	private PacketProvider packetProvider;
	
	public TitleManager(Tooltips tooltips) {
		this.packetProvider = tooltips.getPacketProvider();
		this.packetPlugin = tooltips.getPacketPlugin();
	}
	
	public @Nullable Title newTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		
		switch (packetPlugin) {
		case PACKETEVENTS:
			return new PacketEventsTitle(packetProvider, title, subtitle, fadeIn, stay, fadeOut);
		case PROTOCOLLIB:
			return new ProtocolLibTitle(packetProvider, title, subtitle, fadeIn, stay, fadeOut);
		default:
			break;
		}
		
		return null;
		
	}
	
}
