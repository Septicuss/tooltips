package fi.septicuss.tooltips.managers.tooltip.tasks.data;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerTooltipData {

    // Global data
    private final UUID uuid;

    // Condition Task
    private final AtomicReference<String> currentPreset = new AtomicReference<>();

    // Tooltip Task
    private final AtomicReference<String> displayedPreset = new AtomicReference<>();
    private final AtomicReference<ArrayList<String>> displayedText = new AtomicReference<>();
    private final AtomicReference<ArrayList<String>> savedText = new AtomicReference<>();
    private final AtomicBoolean textChanged = new AtomicBoolean();
    private final AtomicBoolean firstTime = new AtomicBoolean();
    private final ConcurrentHashMap<CooldownType, Long> cooldowns = new ConcurrentHashMap<>();


    public UUID getPlayersId() {
        return uuid;
    }

    public PlayerTooltipData(UUID uuid) {
        this.uuid = uuid;
    }

    public String getCurrentPreset() {
        return this.currentPreset.get();
    }

    public void setCurrentPreset(String presetId) {
        this.currentPreset.set(presetId);
    }

    public boolean hasCurrentPreset() {
        return (this.currentPreset.get() != null);
    }

    public String getDisplayedPreset() {
        return this.displayedPreset.get();
    }

    public void setDisplayedPreset(String presetId) {
        this.displayedPreset.set(presetId);
    }

    public boolean hasDisplayedPreset() {
        return (this.displayedPreset.get() != null);
    }

    public ArrayList<String> getDisplayedText() {
        return this.displayedText.get();
    }

    public void setDisplayedText(ArrayList<String> displayedText) {
        this.displayedText.set(displayedText);
    }

    public boolean hasDisplayedText() {
        return (this.displayedText.get() != null);
    }

    public ArrayList<String> getSavedText() {
        return this.savedText.get();
    }

    public void setSavedText(ArrayList<String> displayedText) {
        this.savedText.set(displayedText);
    }

    public boolean hasSavedText() {
        return (this.savedText.get() != null);
    }

    public boolean hasTextChanged() {
        return this.textChanged.get();
    }

    public void setTextChanged(boolean changed) {
        this.textChanged.set(changed);
    }

    public boolean isFirstTime() {
        return this.firstTime.get();
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime.set(firstTime);
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

    public String cooldowns() {
        String meow = "";
        for (Map.Entry<CooldownType, Long> entry : cooldowns.entrySet()) {
            meow += " {" + entry.getKey() + ": " + (entry.getValue()) + "}";
        }
        return meow;
    }

    private long ticksToMilliseconds(long ticks) {
        return (long) (((double) ticks / (double) 20) * 1000);
    }

}
