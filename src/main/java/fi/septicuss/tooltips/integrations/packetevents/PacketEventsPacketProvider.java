package fi.septicuss.tooltips.integrations.packetevents;

import com.github.retrooper.packetevents.PacketEvents;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.integrations.PacketProvider;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

public class PacketEventsPacketProvider implements PacketProvider {

	public PacketEventsPacketProvider() {
		PacketEvents.setAPI(SpigotPacketEventsBuilder.build(Tooltips.get()));
		if (!PacketEvents.getAPI().isLoaded())
			PacketEvents.getAPI().load();
		if (!PacketEvents.getAPI().isInitialized())
			PacketEvents.getAPI().init();
	}
	
}
