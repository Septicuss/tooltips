package fi.septicuss.tooltips.integrations;

import org.bukkit.entity.Player;

public abstract class Title {

	private PacketProvider packetProvider;
	private String title;
	private String subtitle;
	private int fadeIn;
	private int stay;
	private int fadeOut;
	
	
	public Title(PacketProvider packetProvider, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		this.packetProvider = packetProvider;
		this.title = title;
		this.subtitle = subtitle;
		this.fadeIn = fadeIn;
		this.stay = stay;
		this.fadeOut = fadeOut;
	}
	
	public abstract void send(Player player);
	
	public PacketProvider getPacketProvider() {
		return packetProvider;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getSubtitle() {
		return subtitle;
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
	
}
