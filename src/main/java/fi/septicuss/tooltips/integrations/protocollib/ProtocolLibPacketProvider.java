package fi.septicuss.tooltips.integrations.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import fi.septicuss.tooltips.integrations.PacketProvider;

public class ProtocolLibPacketProvider implements PacketProvider {

	private ProtocolManager protocolManager;

	public ProtocolLibPacketProvider() {
		this.protocolManager = ProtocolLibrary.getProtocolManager();
	}

	public ProtocolManager getProtocolManager() {
		return protocolManager;
	}

}
