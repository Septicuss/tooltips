package fi.septicuss.tooltips;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.septicuss.tooltips.api.event.ConditionRegisterEvent;
import fi.septicuss.tooltips.commands.TooltipsCommand;
import fi.septicuss.tooltips.commands.subcommands.EvalCommand;
import fi.septicuss.tooltips.commands.subcommands.ReloadCommand;
import fi.septicuss.tooltips.commands.subcommands.SendPresetCommand;
import fi.septicuss.tooltips.commands.subcommands.SendThemeCommand;
import fi.septicuss.tooltips.commands.subcommands.VarsCommand;
import fi.septicuss.tooltips.integrations.AreaProvider;
import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.integrations.IntegratedPlugin;
import fi.septicuss.tooltips.integrations.crucible.CrucibleFurnitureProvider;
import fi.septicuss.tooltips.integrations.itemsadder.ItemsAdderFurnitureProvider;
import fi.septicuss.tooltips.integrations.oraxen.OraxenFurnitureProvider;
import fi.septicuss.tooltips.integrations.worldguard.WorldGuardAreaProvider;
import fi.septicuss.tooltips.listener.PlayerConnectionListener;
import fi.septicuss.tooltips.listener.PlayerInteractListener;
import fi.septicuss.tooltips.listener.PlayerMovementListener;
import fi.septicuss.tooltips.object.icon.IconManager;
import fi.septicuss.tooltips.object.preset.PresetManager;
import fi.septicuss.tooltips.object.preset.condition.ConditionManager;
import fi.septicuss.tooltips.object.preset.condition.impl.Compare;
import fi.septicuss.tooltips.object.preset.condition.impl.Day;
import fi.septicuss.tooltips.object.preset.condition.impl.EntityNbtEquals;
import fi.septicuss.tooltips.object.preset.condition.impl.Equipped;
import fi.septicuss.tooltips.object.preset.condition.impl.Gamemode;
import fi.septicuss.tooltips.object.preset.condition.impl.ItemNbtEquals;
import fi.septicuss.tooltips.object.preset.condition.impl.Location;
import fi.septicuss.tooltips.object.preset.condition.impl.LookingAtBlock;
import fi.septicuss.tooltips.object.preset.condition.impl.LookingAtCitizen;
import fi.septicuss.tooltips.object.preset.condition.impl.LookingAtEntity;
import fi.septicuss.tooltips.object.preset.condition.impl.LookingAtFurniture;
import fi.septicuss.tooltips.object.preset.condition.impl.Night;
import fi.septicuss.tooltips.object.preset.condition.impl.Op;
import fi.septicuss.tooltips.object.preset.condition.impl.Region;
import fi.septicuss.tooltips.object.preset.condition.impl.Sneaking;
import fi.septicuss.tooltips.object.preset.condition.impl.StandingOn;
import fi.septicuss.tooltips.object.preset.condition.impl.TileEntityNbtEquals;
import fi.septicuss.tooltips.object.preset.condition.impl.Time;
import fi.septicuss.tooltips.object.preset.condition.impl.World;
import fi.septicuss.tooltips.object.theme.ThemeManager;
import fi.septicuss.tooltips.pack.PackGenerator;
import fi.septicuss.tooltips.pack.impl.IconGenerator;
import fi.septicuss.tooltips.pack.impl.SchemaGenerator;
import fi.septicuss.tooltips.pack.impl.SpacesGenerator;
import fi.septicuss.tooltips.pack.impl.TextureGenerator;
import fi.septicuss.tooltips.pack.impl.ThemeGenerator;
import fi.septicuss.tooltips.tooltip.TooltipManager;
import fi.septicuss.tooltips.tooltip.building.text.TextLine;
import fi.septicuss.tooltips.tooltip.runnable.TooltipRunnableManager;
import fi.septicuss.tooltips.utils.FileSetup;
import fi.septicuss.tooltips.utils.FileUtils;
import fi.septicuss.tooltips.utils.Messaging;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureCache;
import fi.septicuss.tooltips.utils.cache.player.LookingAtCache;
import fi.septicuss.tooltips.utils.cache.tooltip.TooltipCache;
import fi.septicuss.tooltips.utils.font.Widths;
import fi.septicuss.tooltips.utils.font.Widths.SizedChar;
import fi.septicuss.tooltips.utils.placeholder.Placeholder;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import fi.septicuss.tooltips.utils.variable.Variables;

public class Tooltips extends JavaPlugin implements Listener {

	public static Gson GSON = new GsonBuilder().create();
	public static boolean SUPPORT_DISPLAY_ENTITIES;
	public static List<EntityType> FURNITURE_ENTITIES;
	private static Tooltips INSTANCE;
	private static Logger LOGGER;

	private ProtocolManager protocolManager;
	private IconManager iconManager;
	private ThemeManager themeManager;
	private PresetManager presetManager;
	private ConditionManager conditionManager;
	private TooltipManager tooltipManager;
	private PackGenerator packGenerator;
	private TooltipRunnableManager runnableManager;

	private FurnitureProvider furnitureProvider;
	private AreaProvider areaProvider;

	private PlayerInteractListener playerInteractListener;

	// ------------------------------------------------------

	public Tooltips() {
		INSTANCE = this;
		SUPPORT_DISPLAY_ENTITIES = checkIfSupportsDisplayEntities();
		LOGGER = getLogger();

		FURNITURE_ENTITIES = Lists.newArrayList();
		FURNITURE_ENTITIES.add(EntityType.ITEM_FRAME);
		FURNITURE_ENTITIES.add(EntityType.ARMOR_STAND);

		if (SUPPORT_DISPLAY_ENTITIES) {
			FURNITURE_ENTITIES.add(EntityType.ITEM_DISPLAY);
		}

	}

	private static boolean checkIfSupportsDisplayEntities() {
		try {
			Class.forName("org.bukkit.entity.ItemDisplay");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	// ------------------------------------------------------

	@Override
	public void onEnable() {
		protocolManager = ProtocolLibrary.getProtocolManager();

		// Set up files (export them from the jar)
		FileSetup.setup(this);

		// Load persistent variables
		loadVariables();

		// Load integrations
		loadIntegrations();

		// Load required listeners
		loadListeners();

		// Load conditionmanager now, because it has to register conditions once
		conditionManager = new ConditionManager();

		// Load local placeholders
		addLocalPlaceholders();

		// Register commands
		loadCommands();

		// Reload the plugin
		reload();

	}

	@Override
	public void onDisable() {
		if (this.runnableManager != null)
			this.runnableManager.stop();
	}

	// ------------------------------------------------------

	private void loadVariables() {
		File variablesDirectory = new File(getDataFolder(), "data/variables");
		Variables.PERSISTENT.load(variablesDirectory);
	}

	private void loadIntegrations() {

		PluginManager pluginManager = Bukkit.getPluginManager();
		String preferredPlugin = getConfig().getString("furniture-plugin", "automatic");

		boolean chooseAutomatically = preferredPlugin.equalsIgnoreCase("auto")
				|| preferredPlugin.equalsIgnoreCase("automatic");

		for (IntegratedPlugin plugin : IntegratedPlugin.values()) {
			if (pluginManager.getPlugin(plugin.getName()) != null) {
				plugin.setEnabled(true);
			} else if (plugin.isRequired()) {
				log(plugin.getName() + " is required to run Tooltips");
				pluginManager.disablePlugin(this);
				return;
			} else {
				plugin.setEnabled(false);
			}
		}

		for (IntegratedPlugin furniturePlugin : IntegratedPlugin.FURNITURE_PLUGINS) {
			if (!furniturePlugin.isEnabled())
				continue;
			if (chooseAutomatically || preferredPlugin.equalsIgnoreCase(furniturePlugin.getName())) {
				this.furnitureProvider = switch (furniturePlugin) {
				case ORAXEN -> new OraxenFurnitureProvider();
				case ITEMSADDER -> new ItemsAdderFurnitureProvider();
				case CRUCIBLE -> new CrucibleFurnitureProvider();
				default -> null;
				};

				if (this.furnitureProvider != null) {
					log("Used furniture plugin: " + this.furnitureProvider.getClass().getSimpleName());
					break;
				}
			}
		}

		for (IntegratedPlugin areaPlugin : IntegratedPlugin.AREA_PLUGINS) {
			if (!areaPlugin.isEnabled())
				continue;
			this.areaProvider = switch (areaPlugin) {
			case WORLDGUARD -> new WorldGuardAreaProvider();
			default -> null;
			};
		}

	}

	private void loadListeners() {
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(this, this);

		if (areaProvider != null)
			pluginManager.registerEvents(new PlayerMovementListener(areaProvider), this);

		playerInteractListener = new PlayerInteractListener();

		pluginManager.registerEvents(playerInteractListener, this);
		pluginManager.registerEvents(new PlayerConnectionListener(), this);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(ConditionRegisterEvent event) {
		event.register("day", new Day());
		event.register("night", new Night());
		event.register("world", new World());
		event.register("gamemode", new Gamemode());
		event.register("sneaking", new Sneaking());
		event.register("compare", new Compare());
		event.register("lookingatblock", new LookingAtBlock());
		event.register("lookingatfurniture", new LookingAtFurniture(this.furnitureProvider));
		event.register("lookingatentity", new LookingAtEntity());
		event.register("region", new Region());
//		event.register("incuboid", new InCuboid());
		event.register("location", new Location());
		event.register("standingon", new StandingOn());
		event.register("itemnbtequals", new ItemNbtEquals());
		event.register("entitynbtequals", new EntityNbtEquals());
		event.register("tileentitynbtequals", new TileEntityNbtEquals());
//		event.register("blocknbtequals", new BlockNbtEquals());
		event.register("time", new Time());
		event.register("equipped", new Equipped());
		event.register("op", new Op());
		event.register("lookingatcitizen", new LookingAtCitizen());
	}

	private void loadCommands() {
		TooltipsCommand tooltipsCommand = new TooltipsCommand(this);
		tooltipsCommand.register("sendtheme", new SendThemeCommand(this));
		tooltipsCommand.register("sendpreset", new SendPresetCommand(this));
		tooltipsCommand.register("reload", new ReloadCommand(this));
		tooltipsCommand.register("eval", new EvalCommand(this));
		tooltipsCommand.register("vars", new VarsCommand());

		PluginCommand tooltipsPluginCommand = getCommand("tooltips");
		tooltipsPluginCommand.setExecutor(tooltipsCommand);
		tooltipsPluginCommand.setTabCompleter(tooltipsCommand);
	}

	public void reload() {

		// So that new icons may be added
		TextLine.replaceables = null;

		// Stop possible previous tooltip runnable
		if (this.runnableManager != null)
			this.runnableManager.stop();

		this.reloadConfig();

		TooltipCache.clear();
		FurnitureCache.clear();
		fillCache();

		FileSetup.setup(this);

		File defaultFonts = new File(getDataFolder(), "data/schemas/font/default-fonts.yml");
		Widths.loadFromSchemas(getDataFolder(), YamlConfiguration.loadConfiguration(defaultFonts));

		final boolean useSpaces = this.getConfig().getBoolean("use-spaces", true);
		final int checkFrequency = this.getConfig().getInt("condition-check-frequency", 3);

		SizedChar space = new SizedChar(' ');

		if (useSpaces) {
			space.setHeight(1);
			space.setAbsoluteWidth(1);
			space.setImageHeight(1);
		} else {
			space.setHeight(2);
			space.setAbsoluteWidth(2);
			space.setImageHeight(2);
		}

		Widths.add(space);

		this.iconManager = new IconManager();
		this.themeManager = new ThemeManager();
		this.presetManager = new PresetManager();
		this.tooltipManager = new TooltipManager(this);

		iconManager.loadFrom(this, FileUtils.getAllConfigsFrom(this, "icons"));
		themeManager.loadFrom(FileUtils.getAllConfigsFrom(this, "themes"));
		presetManager.loadFrom(this, FileUtils.getAllConfigsFrom(this, "presets"));

		this.packGenerator = new PackGenerator(this);
		this.packGenerator.registerGenerator(new SpacesGenerator(packGenerator, useSpaces));
		this.packGenerator.registerGenerator(new SchemaGenerator(this, packGenerator));
		this.packGenerator.registerGenerator(new ThemeGenerator(packGenerator, themeManager, useSpaces));
		this.packGenerator.registerGenerator(new IconGenerator(packGenerator, iconManager));
		this.packGenerator.registerGenerator(new TextureGenerator(packGenerator));
		this.packGenerator.generate();

		this.runnableManager = new TooltipRunnableManager(this);
		this.runnableManager.run(this, checkFrequency);

		if (this.playerInteractListener != null)
			playerInteractListener.setRunnableManager(this.runnableManager);

	}

	private void fillCache() {
		Bukkit.getScheduler().runTaskLater(this, () -> {
			if (furnitureProvider != null) {
				FurnitureCache.cacheAll(furnitureProvider.getAllFurniture());
			}
		}, 40L);
	}

	private void addLocalPlaceholders() {

		if (furnitureProvider != null) {
			Placeholders.addLocal("furniture_id", new Placeholder((p, s) -> {
				Block block = p.getTargetBlockExact(10);

				if (block != null && furnitureProvider.isFurniture(block)) {
					if (LookingAtCache.contains(p)) {
						return LookingAtCache.get(p);
					}

					String id = furnitureProvider.getFurnitureId(block);
					LookingAtCache.put(p, id);

					return id;
				}

				Entity entity = Utils.getEntityPlayerIsLookingAt(p, 10, 0, FURNITURE_ENTITIES);

				if (entity != null && furnitureProvider.isFurniture(entity)) {
					if (LookingAtCache.contains(p)) {
						return LookingAtCache.get(p);
					}

					String id = furnitureProvider.getFurnitureId(entity);
					LookingAtCache.put(p, id);

					return id;
				}

				return "None";
			}));

			Placeholders.addLocal("furniture_name", new Placeholder((p, s) -> {
				Block block = p.getTargetBlockExact(10);

				if (block != null && furnitureProvider.isFurniture(block)) {
					return ChatColor.stripColor(
							FurnitureCache.getFurniture(furnitureProvider.getFurnitureId(block)).displayName());
				}

				Entity entity = Utils.getEntityPlayerIsLookingAt(p, 10, 0, FURNITURE_ENTITIES);

				if (entity != null && furnitureProvider.isFurniture(entity)) {
					return ChatColor.stripColor(
							FurnitureCache.getFurniture(furnitureProvider.getFurnitureId(entity)).displayName());
				}

				return "None";
			}));
		}

	}

	// ------------------------------------------------------

	public static Tooltips get() {
		return INSTANCE;
	}

	public ProtocolManager getProtocolManager() {
		return protocolManager;
	}

	public ThemeManager getThemeManager() {
		return themeManager;
	}

	public PresetManager getPresetManager() {
		return presetManager;
	}

	public ConditionManager getConditionManager() {
		return conditionManager;
	}

	public IconManager getIconManager() {
		return iconManager;
	}

	public TooltipManager getTooltipManager() {
		return tooltipManager;
	}

	public FurnitureProvider getFurnitureProvider() {
		return furnitureProvider;
	}

	public static Logger logger() {
		return LOGGER;
	}

	public static void warn(String message) {
		Messaging.send(Bukkit.getConsoleSender(), ChatColor.RED + "[Tooltips] WARNING: " + message);
	}

	public static void log(String message) {
		Messaging.send(Bukkit.getConsoleSender(), "[Tooltips] " + message);
	}

	public static File getPackAssetsFolder() {
		return new File(INSTANCE.getDataFolder(), "pack/assets");
	}

}
