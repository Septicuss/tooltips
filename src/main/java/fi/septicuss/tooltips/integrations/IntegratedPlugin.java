package fi.septicuss.tooltips.integrations;

public enum IntegratedPlugin {

	PROTOCOLLIB("ProtocolLib"),
	PACKETEVENTS("packetevents"),
	PAPI("PlaceholderAPI"),
	WORLDGUARD("WorldGuard"),
	ITEMSADDER("ItemsAdder"),
	ORAXEN("Oraxen"),
	NEXO("Nexo"),
	CRUCIBLE("MythicCrucible"),
	NBTAPI("NBTAPI"),
	CITIZENS("Citizens"),
	MYTHICMOBS("MythicMobs");

	public static final IntegratedPlugin[] FURNITURE_PLUGINS = { IntegratedPlugin.ORAXEN, IntegratedPlugin.CRUCIBLE, IntegratedPlugin.ITEMSADDER,  IntegratedPlugin.NEXO,};
	public static final IntegratedPlugin[] AREA_PLUGINS = { IntegratedPlugin.WORLDGUARD };
	public static final IntegratedPlugin[] PACKET_PLUGINS = { IntegratedPlugin.PROTOCOLLIB, IntegratedPlugin.PACKETEVENTS };

	private String name;
	private boolean required = false;
	private boolean enabled = false;

	private IntegratedPlugin(String name) {
		this.name = name;
	}

	private IntegratedPlugin(String name, boolean required) {
		this.name = name;
		this.required = required;
	}

	public String getName() {
		return name;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
