package fi.septicuss.tooltips.utils.variable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.object.preset.condition.argument.Argument;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.FileUtils;

public class Variables {

	public static Persistent PERSISTENT = new Persistent();
	public static Local LOCAL = new Local();

	public static class Persistent implements VariableProvider, Saveable<File> {

		private static FileConfiguration GLOBAL_VARIABLE_CONFIG;
		private static FileConfiguration PLAYER_VARIABLE_CONFIG;

		private static File GLOBAL_FILE;
		private static File PLAYER_FILE;

		@Override
		public void load(File directory) {
			GLOBAL_FILE = new File(directory, "global-variables.yml");
			PLAYER_FILE = new File(directory, "player-variables.yml");

			FileUtils.createFileIfNotExists(GLOBAL_FILE);
			FileUtils.createFileIfNotExists(PLAYER_FILE);

			GLOBAL_VARIABLE_CONFIG = YamlConfiguration.loadConfiguration(GLOBAL_FILE);
			PLAYER_VARIABLE_CONFIG = YamlConfiguration.loadConfiguration(PLAYER_FILE);
		}

		@Override
		public void save() {
			try {
				GLOBAL_VARIABLE_CONFIG.save(GLOBAL_FILE);
			} catch (IOException e) {
				Tooltips.warn("Failed to save global variables.");
				e.printStackTrace();
			}

			try {
				PLAYER_VARIABLE_CONFIG.save(PLAYER_FILE);
			} catch (IOException e) {
				Tooltips.warn("Failed to save player variables.");
				e.printStackTrace();
			}
		}

		@Override
		public Argument getVar(String varName) {
			return new Argument(GLOBAL_VARIABLE_CONFIG.getString(varName));
		}

		@Override
		public boolean hasVar(String varName) {
			return GLOBAL_VARIABLE_CONFIG.isSet(varName);
		}

		@Override
		public void setVar(String varName, String value) {
			GLOBAL_VARIABLE_CONFIG.set(varName, value);
		}

		@Override
		public void clearVar(String varName) {
			GLOBAL_VARIABLE_CONFIG.set(varName, null);
		}

		@Override
		public void clearAllVars() {
			for (var key : GLOBAL_VARIABLE_CONFIG.getKeys(false))
				GLOBAL_VARIABLE_CONFIG.set(key, null);
		}

		@Override
		public Argument getVar(OfflinePlayer player, String varName) {
			return new Argument(PLAYER_VARIABLE_CONFIG.getString(getPlayerVarPath(player, varName)));
		}

		@Override
		public boolean hasVar(OfflinePlayer player, String varName) {
			return PLAYER_VARIABLE_CONFIG.isSet(getPlayerVarPath(player, varName));
		}

		@Override
		public void setVar(OfflinePlayer player, String varName, String value) {
			PLAYER_VARIABLE_CONFIG.set(getPlayerVarPath(player, varName), value);
		}

		@Override
		public void clearVar(OfflinePlayer player, String varName) {
			PLAYER_VARIABLE_CONFIG.set(getPlayerVarPath(player, varName), null);
		}

		@Override
		public void clearAllVars(OfflinePlayer player) {
			PLAYER_VARIABLE_CONFIG.set(getPlayerVarPath(player, null), null);
		}

		private String getPlayerVarPath(OfflinePlayer player, String varName) {
			if (varName == null)
				return (player.getUniqueId().toString());
			return (player.getUniqueId().toString() + "." + varName);
		}

	}

	public static class Local implements VariableProvider {

		private Map<String, Argument> GLOBAL_VARIABLES = new HashMap<>();
		private Map<UUID, Arguments> PLAYER_VARIABLES = new HashMap<>();

		@Override
		public Argument getVar(String varName) {
			return GLOBAL_VARIABLES.get(varName);
		}

		@Override
		public boolean hasVar(String varName) {
			return GLOBAL_VARIABLES.containsKey(varName);
		}

		@Override
		public void setVar(String varName, String value) {
			GLOBAL_VARIABLES.put(varName, new Argument(value));
		}

		@Override
		public void clearVar(String varName) {
			GLOBAL_VARIABLES.remove(varName);
		}

		@Override
		public void clearAllVars() {
			GLOBAL_VARIABLES.clear();
		}

		@Override
		public Argument getVar(OfflinePlayer player, String varName) {
			UUID uuid = player.getUniqueId();

			if (!PLAYER_VARIABLES.containsKey(uuid)) {
				return null;
			}

			Arguments args = PLAYER_VARIABLES.get(uuid);

			if (!args.has(varName)) {
				return null;
			}

			return args.get(varName);
		}

		@Override
		public boolean hasVar(OfflinePlayer player, String varName) {
			UUID uuid = player.getUniqueId();

			if (!PLAYER_VARIABLES.containsKey(uuid)) {
				return false;
			}

			Arguments args = PLAYER_VARIABLES.get(uuid);
			return args.has(varName);
		}

		@Override
		public void setVar(OfflinePlayer player, String varName, String value) {
			UUID uuid = player.getUniqueId();
			Arguments args = null;

			if (PLAYER_VARIABLES.containsKey(uuid)) {
				args = PLAYER_VARIABLES.get(uuid);
			} else {
				args = new Arguments();
			}

			args.add(varName, new Argument(value));
			PLAYER_VARIABLES.put(uuid, args);
		}

		@Override
		public void clearVar(OfflinePlayer player, String varName) {
			UUID uuid = player.getUniqueId();

			if (!PLAYER_VARIABLES.containsKey(uuid)) {
				return;
			}

			Arguments args = PLAYER_VARIABLES.get(uuid);
			args.remove(varName);
			PLAYER_VARIABLES.put(uuid, args);
		}

		@Override
		public void clearAllVars(OfflinePlayer player) {
			UUID uuid = player.getUniqueId();
			PLAYER_VARIABLES.remove(uuid);
		}

	}

	public static interface VariableProvider {

		public Argument getVar(String varName);

		public boolean hasVar(String varName);

		public void setVar(String varName, String value);

		public void clearVar(String varName);

		public void clearAllVars();

		public Argument getVar(OfflinePlayer player, String varName);

		public boolean hasVar(OfflinePlayer player, String varName);

		public void setVar(OfflinePlayer player, String varName, String value);

		public void clearVar(OfflinePlayer player, String varName);

		public void clearAllVars(OfflinePlayer player);

	}

	private interface Saveable<T> {

		public void load(T from);

		public void save();

	}

}
