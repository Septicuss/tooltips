package fi.septicuss.tooltips.managers.schema;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;

public record SchemaPart(ConfigurationSection schemaConfig, JsonObject schemaProvider) {

}
