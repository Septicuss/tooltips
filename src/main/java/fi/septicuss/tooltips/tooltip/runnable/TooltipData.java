package fi.septicuss.tooltips.tooltip.runnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class TooltipData {

	private UUID uuid;
	private String currentPreset;
	private List<String> currentText;
	private Map<CooldownType, Long> cooldowns;

	public TooltipData(Player player) {
		this(player.getUniqueId());
	}

	public TooltipData(UUID uuid) {
		this.uuid = uuid;
		this.cooldowns = new HashMap<>();
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public boolean hasCurrentPreset() {
		return (currentPreset != null);
	}

	public String getCurrentPresetId() {
		return currentPreset;
	}

	public void setCurrentPreset(String preset) {
		this.currentPreset = preset;
	}
	
	public boolean hasCurrentText() {
		return (currentText != null);
	}
	
	public List<String> getCurrentText() {
		return Collections.unmodifiableList(currentText);
	}
	
	public void setCurrentText(List<String> text) {
		this.currentText = text;
	}

	public boolean hasCooldown(CooldownType cooldownType) {
		if (!cooldowns.containsKey(cooldownType))
			return false;

		long remaining = cooldowns.get(cooldownType) - System.currentTimeMillis();

		if (remaining > 0)
			return true;
		
		cooldowns.remove(cooldownType);
		return false;
	}
	
	public void addCooldown(CooldownType cooldownType, int durationTicks) {
		long totalTime = System.currentTimeMillis() + ticksToMilliseconds(durationTicks);
		cooldowns.put(cooldownType, totalTime);
	}
	
	public void removeCooldown(CooldownType cooldownType) {
		cooldowns.remove(cooldownType);
	}
	
	private long ticksToMilliseconds(long ticks) {
		return (long) (((double) ticks / (double) 20) * 1000);
	}

	public enum CooldownType {
		FADE_OUT,
		STAY,
		FADE_IN,
		COOLDOWN;
	}

}
