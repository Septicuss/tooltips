package fi.septicuss.tooltips.managers.schema;

import org.bukkit.configuration.ConfigurationSection;

import com.google.gson.JsonObject;

public record SchemaPart(ConfigurationSection schemaConfig, JsonObject schemaProvider) {

}
