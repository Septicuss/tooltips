package fi.septicuss.tooltips.managers.tooltip.tasks.data;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.preset.animation.Animation;
import fi.septicuss.tooltips.managers.preset.animation.Animations;
import fi.septicuss.tooltips.managers.preset.animation.ParsedAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
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
    private final AtomicReference<List<String>> sourceText = new AtomicReference<>();
    private final AtomicBoolean textChanged = new AtomicBoolean();
    private final AtomicBoolean firstTime = new AtomicBoolean();
    private final ConcurrentHashMap<CooldownType, Long> cooldowns = new ConcurrentHashMap<>();

    // Animations
    private final ArrayList<UUID> animations = new ArrayList<>();
    private boolean animationsSetup = false;
    private boolean animationsDone = false;

    // Context
    private Context context = new Context();

    // Send preset
    private String sentPreset;

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

    public void setSentPreset(String sentPreset) {
        this.sentPreset = sentPreset;
    }

    public String getSentPreset() {
        return sentPreset;
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

    public List<String> getSourceText() {
        return sourceText.get();
    }

    public void setSourceText(List<String> sourceText) {
        this.sourceText.set(sourceText);
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

    public void resetContext() {
        this.context = new Context();
    }

    public Context getContext() {
        return this.context;
    }

    public void addAnimation(UUID uuid) {
        this.animations.add(uuid);
    }

    public void tickAnimations() {
        if (this.animations.isEmpty()) return;
        if (!this.animationsSetup) setupAnimations();

        if (this.animationsDone)
            this.context.put("animation.finished", "true");

        final Player player = Bukkit.getPlayer(this.uuid);

        ParsedAnimation previousIdentifiedAnimation = null;

        for (UUID uuid : this.animations) {
            final ParsedAnimation animation = Animations.get(uuid);
            if (animation == null) continue;
            if (animation.finished()) continue;

            // Unaffected by order
            if (animation.id() == -1) {
                animation.tick(player);
                continue;
            }

            if (previousIdentifiedAnimation == null || previousIdentifiedAnimation.finished()) {
                previousIdentifiedAnimation = animation;
                animation.tick(player);
            }

        }

        runAnimationFinishedAction();

    }

    public void skipCurrentAnimation() {
        if (this.animationsDone) return;
        if (this.animations.isEmpty()) return;

        for (UUID uuid : this.animations) {
            final ParsedAnimation animation = Animations.get(uuid);
            if (animation == null) continue;
            if (animation.finished()) continue;
            if (animation.id() == -1) continue;

            animation.skip();
            this.runAction("animation-current-skipped");
            break;
        }

    }

    public void skipAllAnimations() {
        if (this.animationsDone) return;
        if (this.animations.isEmpty()) return;

        for (UUID uuid : this.animations) {
            final ParsedAnimation animation = Animations.get(uuid);
            if (animation == null) continue;
            if (animation.finished()) continue;
            if (animation.id() == -1) continue;

            animation.skip();
            this.runAction("animation-skipped");
        }

    }

    private void runAnimationFinishedAction() {
        if (this.animations.isEmpty()) return;
        if (this.animationsDone) return;

        for (UUID uuid : this.animations) {
            final ParsedAnimation animation = Animations.get(uuid);
            if (animation == null) continue;
            if (!animation.finished()) return;
        }

        this.animationsDone = true;
        this.runAction("animation-finished");

    }

    private void setupAnimations() {
        this.animations.sort((o1, o2) -> {
            final ParsedAnimation p1 = Animations.get(o1);
            final ParsedAnimation p2 = Animations.get(o2);
            return Integer.compare(p1.id(), p2.id());
        });
        this.animationsSetup = true;
    }

    public void clearAnimations() {
        for (UUID uuid : this.animations) {
            Animations.stopAnimation(uuid);
        }
        this.animations.clear();
        this.animationsDone = false;
        this.animationsSetup = false;
    }

    private void runAction(String action) {
        Tooltips.get().getTooltipManager().runActions(action, Bukkit.getPlayer(this.uuid));
    }

    private long ticksToMilliseconds(long ticks) {
        return (long) (((double) ticks / (double) 20) * 1000);
    }

}
