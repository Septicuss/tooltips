package fi.septicuss.tooltips.integrations.packetevents;

import org.bukkit.entity.Player;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;

import fi.septicuss.tooltips.integrations.PacketProvider;
import fi.septicuss.tooltips.integrations.Title;
import net.kyori.adventure.text.Component;


public class PacketEventsTitle extends Title {

	private Component title;
	private Component subtitle;

	public PacketEventsTitle(PacketProvider packetProvider, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		super(packetProvider, title, subtitle, fadeIn, stay, fadeOut);
		this.title = AdventureSerializer.parseComponent(title);
		this.subtitle = AdventureSerializer.parseComponent(subtitle);
	}

	@Override
	public void send(Player player) {
		PacketEvents.getAPI().getPlayerManager().getUser(player).sendTitle(title, subtitle, getFadeIn(), getStay(), getFadeOut());
	}

}
