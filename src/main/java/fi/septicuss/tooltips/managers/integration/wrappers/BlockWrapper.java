package fi.septicuss.tooltips.managers.integration.wrappers;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public record BlockWrapper(String plugin, String id, String displayName, @Nullable Location location) {}
