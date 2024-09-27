package fi.septicuss.tooltips;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.septicuss.tooltips.api.TooltipsAPI;
import fi.septicuss.tooltips.commands.TooltipsCommand;
import fi.septicuss.tooltips.commands.subcommands.DebugCommand;
import fi.septicuss.tooltips.commands.subcommands.EvalCommand;
import fi.septicuss.tooltips.commands.subcommands.ListVarsCommand;
import fi.septicuss.tooltips.commands.subcommands.ReloadCommand;
import fi.septicuss.tooltips.commands.subcommands.SendPresetCommand;
import fi.septicuss.tooltips.commands.subcommands.SendThemeCommand;
import fi.septicuss.tooltips.commands.subcommands.VarsCommand;
import fi.septicuss.tooltips.integrations.AreaProvider;
import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.integrations.IntegratedPlugin;
import fi.septicuss.tooltips.integrations.PacketProvider;
import fi.septicuss.tooltips.integrations.crucible.CrucibleFurnitureProvider;
import fi.septicuss.tooltips.integrations.itemsadder.ItemsAdderFurnitureProvider;
import fi.septicuss.tooltips.integrations.oraxen.OraxenFurnitureProvider;
import fi.septicuss.tooltips.integrations.packetevents.PacketEventsPacketProvider;
import fi.septicuss.tooltips.integrations.papi.TooltipsExpansion;
import fi.septicuss.tooltips.integrations.protocollib.ProtocolLibPacketProvider;
import fi.septicuss.tooltips.integrations.worldguard.WorldGuardAreaProvider;
import fi.septicuss.tooltips.listener.PlayerConnectionListener;
import fi.septicuss.tooltips.listener.PlayerInteractListener;
import fi.septicuss.tooltips.listener.PlayerMovementListener;
import fi.septicuss.tooltips.managers.icon.IconManager;
import fi.septicuss.tooltips.managers.preset.PresetManager;
import fi.septicuss.tooltips.managers.condition.ConditionManager;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.impl.BlockNbtEquals;
import fi.septicuss.tooltips.managers.condition.impl.BlockStateEquals;
import fi.septicuss.tooltips.managers.condition.impl.Compare;
import fi.septicuss.tooltips.managers.condition.impl.Day;
import fi.septicuss.tooltips.managers.condition.impl.EntityNbtEquals;
import fi.septicuss.tooltips.managers.condition.impl.Equipped;
import fi.septicuss.tooltips.managers.condition.impl.Gamemode;
import fi.septicuss.tooltips.managers.condition.impl.InCuboid;
import fi.septicuss.tooltips.managers.condition.impl.ItemNbtEquals;
import fi.septicuss.tooltips.managers.condition.impl.Location;
import fi.septicuss.tooltips.managers.condition.impl.LookingAtBlock;
import fi.septicuss.tooltips.managers.condition.impl.LookingAtCitizen;
import fi.septicuss.tooltips.managers.condition.impl.LookingAtEntity;
import fi.septicuss.tooltips.managers.condition.impl.LookingAtFurniture;
import fi.septicuss.tooltips.managers.condition.impl.LookingAtMythicMob;
import fi.septicuss.tooltips.managers.condition.impl.Night;
import fi.septicuss.tooltips.managers.condition.impl.Op;
import fi.septicuss.tooltips.managers.condition.impl.Permission;
import fi.septicuss.tooltips.managers.condition.impl.Region;
import fi.septicuss.tooltips.managers.condition.impl.Sneaking;
import fi.septicuss.tooltips.managers.condition.impl.StandingOn;
import fi.septicuss.tooltips.managers.condition.impl.TileEntityNbtEquals;
import fi.septicuss.tooltips.managers.condition.impl.Time;
import fi.septicuss.tooltips.managers.condition.impl.World;
import fi.septicuss.tooltips.managers.schema.SchemaManager;
import fi.septicuss.tooltips.managers.theme.ThemeManager;
import fi.septicuss.tooltips.managers.title.TitleManager;
import fi.septicuss.tooltips.pack.PackGenerator;
import fi.septicuss.tooltips.pack.impl.IconGenerator;
import fi.septicuss.tooltips.pack.impl.LineGenerator;
import fi.septicuss.tooltips.pack.impl.SpaceGenerator;
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
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import fi.septicuss.tooltips.utils.placeholder.impl.SimplePlaceholderParser;
import fi.septicuss.tooltips.utils.variable.Variables;

public class Tooltips extends JavaPlugin {

	public static Gson GSON = new GsonBuilder().create();
	public static boolean SUPPORT_DISPLAY_ENTITIES;
	public static List<EntityType> FURNITURE_ENTITIES;
	private static Tooltips INSTANCE;
	private static Logger LOGGER;
	private static boolean USE_SPACES;

	private TitleManager titleManager;
	private SchemaManager schemaManager;
	private IconManager iconManager;
	private ThemeManager themeManager;
	private PresetManager presetManager;
	private ConditionManager conditionManager;
	private TooltipManager tooltipManager;
	private TooltipRunnableManager runnableManager;

	private IntegratedPlugin packetPlugin;
	private PacketProvider packetProvider;
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
			FURNITURE_ENTITIES.add(EntityType.BLOCK_DISPLAY);
			FURNITURE_ENTITIES.add(EntityType.INTERACTION);
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
	public void onLoad() {
		registerDefaultConditions();
	}
	
	@Override
	public void onEnable() {
		FileSetup.performMigration(this);
		FileSetup.setupFiles(this);

		loadVariables();
		loadIntegrations();
		loadListeners();

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			TooltipsExpansion expansion = new TooltipsExpansion();
			if (expansion.isRegistered())
				expansion.unregister();
			expansion.register();
		}

		titleManager = new TitleManager(this);
		conditionManager = new ConditionManager();

		addLocalPlaceholders();
		loadCommands();
		reload();
	}

	@Override
	public void onDisable() {
		if (this.runnableManager != null)
			this.runnableManager.stop();

		Variables.PERSISTENT.save();
	}

	// ------------------------------------------------------

	private void registerDefaultConditions() {
		TooltipsAPI.registerCondition("day", new Day());
		TooltipsAPI.registerCondition("night", new Night());
		TooltipsAPI.registerCondition("world", new World());
		TooltipsAPI.registerCondition("gamemode", new Gamemode());
		TooltipsAPI.registerCondition("sneaking", new Sneaking());
		TooltipsAPI.registerCondition("compare", new Compare());
		TooltipsAPI.registerCondition("lookingatblock", new LookingAtBlock());
		TooltipsAPI.registerCondition("lookingatfurniture", new LookingAtFurniture());
		TooltipsAPI.registerCondition("lookingatentity", new LookingAtEntity());
		TooltipsAPI.registerCondition("lookingatmythicmob", new LookingAtMythicMob());
		TooltipsAPI.registerCondition("region", new Region());
		TooltipsAPI.registerCondition("incuboid", new InCuboid());
		TooltipsAPI.registerCondition("location", new Location());
		TooltipsAPI.registerCondition("standingon", new StandingOn());
		TooltipsAPI.registerCondition("itemnbtequals", new ItemNbtEquals());
		TooltipsAPI.registerCondition("entitynbtequals", new EntityNbtEquals());
		TooltipsAPI.registerCondition("tileentitynbtequals", new TileEntityNbtEquals());
		TooltipsAPI.registerCondition("blocknbtequals", new BlockNbtEquals());
		TooltipsAPI.registerCondition("blockstateequals", new BlockStateEquals());
		TooltipsAPI.registerCondition("time", new Time());
		TooltipsAPI.registerCondition("equipped", new Equipped());
		TooltipsAPI.registerCondition("op", new Op());
		TooltipsAPI.registerCondition("lookingatcitizen", new LookingAtCitizen());
		TooltipsAPI.registerCondition("permission", new Permission());
	}
	
	private void loadVariables() {
		final File variablesDirectory = new File(getDataFolder(), ".data/variables");
		Variables.PERSISTENT.load(variablesDirectory);
	}

	private void loadIntegrations() {

		PluginManager pluginManager = Bukkit.getPluginManager();

		// For each IntegratedPlugin, check if enabled on the server
		for (IntegratedPlugin integratedPlugin : IntegratedPlugin.values()) {

			final var bukkitPlugin = pluginManager.getPlugin(integratedPlugin.getName());
			final boolean required = integratedPlugin.isRequired();
			final boolean enabled = bukkitPlugin != null;

			// Required plugin not present
			if (!enabled && required) {
				log(integratedPlugin.getName() + " is required to run Tooltips");
				pluginManager.disablePlugin(this);
				return;
			}

			integratedPlugin.setEnabled(enabled);
		}

		final String preferredFurniturePlugin = getConfig().getString("furniture-plugin", "automatic").toLowerCase();
		final boolean chooseAutomatically = (preferredFurniturePlugin.equals("auto")
				|| preferredFurniturePlugin.equals("automatic"));

		// Load providers for appropriate furniture plugins
		for (IntegratedPlugin furniturePlugin : IntegratedPlugin.FURNITURE_PLUGINS) {

			if (!furniturePlugin.isEnabled()) {
				continue;
			}

			if (chooseAutomatically || preferredFurniturePlugin.equalsIgnoreCase(furniturePlugin.getName())) {

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

		// Load area provider for appropriate area plugins
		for (IntegratedPlugin areaPlugin : IntegratedPlugin.AREA_PLUGINS) {

			if (!areaPlugin.isEnabled()) {
				continue;
			}

			this.areaProvider = switch (areaPlugin) {
			case WORLDGUARD -> new WorldGuardAreaProvider();
			default -> null;
			};

		}
		
		// Load providers for appropriate packet plugins
		for (IntegratedPlugin packetPlugin : IntegratedPlugin.PACKET_PLUGINS) {
			
			if (!packetPlugin.isEnabled()) {
				continue;
			}

			
			this.packetProvider = switch (packetPlugin) {
			case PACKETEVENTS -> new PacketEventsPacketProvider();
			case PROTOCOLLIB -> new ProtocolLibPacketProvider();
			default -> null;
			};
			
			if (this.packetProvider != null) {
				this.packetPlugin = packetPlugin;
				break;
			}
			
		}
		
		if (this.packetProvider == null) {
			log("No supported packet plugin found! Tooltips requires either ProtocolLib or PacketEvents.");
			pluginManager.disablePlugin(this);
			return;
		}
		

	}

	private void loadListeners() {
		PluginManager pluginManager = Bukkit.getPluginManager();

		if (this.areaProvider != null) {
			pluginManager.registerEvents(new PlayerMovementListener(this.areaProvider), this);
		}

		this.playerInteractListener = new PlayerInteractListener();
		pluginManager.registerEvents(this.playerInteractListener, this);
		pluginManager.registerEvents(new PlayerConnectionListener(), this);
	}

	private void loadCommands() {
		TooltipsCommand tooltipsCommand = new TooltipsCommand(this);
		tooltipsCommand.register("sendtheme", new SendThemeCommand(this));
		tooltipsCommand.register("sendpreset", new SendPresetCommand(this));
		tooltipsCommand.register("reload", new ReloadCommand(this));
		tooltipsCommand.register("eval", new EvalCommand(this));
		tooltipsCommand.register("vars", new VarsCommand());
		tooltipsCommand.register("listvars", new ListVarsCommand());
		tooltipsCommand.register("debug", new DebugCommand(this));

		PluginCommand tooltipsPluginCommand = getCommand("tooltips");
		tooltipsPluginCommand.setExecutor(tooltipsCommand);
		tooltipsPluginCommand.setTabCompleter(tooltipsCommand);
	}

	public void reload() {

		// Stop possible previous tooltip runnable
		if (this.runnableManager != null) {
			this.runnableManager.stop();
		}

		this.reloadConfig();

		clearCache();
		fillCache();

		FileSetup.setupFiles(this);

		USE_SPACES = this.getConfig().getBoolean("use-spaces", true);
		final int checkFrequency = this.getConfig().getInt("condition-check-frequency", 3);

		this.schemaManager = new SchemaManager();
		this.iconManager = new IconManager();
		this.themeManager = new ThemeManager();
		this.presetManager = new PresetManager();
		this.tooltipManager = new TooltipManager(this);

		schemaManager.loadFrom(new File(getDataFolder(), ".data/schemas"));
		iconManager.loadFrom(new File(getDataFolder(), "icons"));
		themeManager.loadFrom(new File(getDataFolder(), "themes"));
		presetManager.loadFrom(this, new File(getDataFolder(), "presets"));

		Widths.loadCustomWidths(new File(getDataFolder(), ".data/widths.yml"));
		
		addSpaceCharWidth(USE_SPACES);

		PackGenerator packGenerator = new PackGenerator(this);
		packGenerator.registerGenerator(new SpaceGenerator(USE_SPACES));
		packGenerator.registerGenerator(new ThemeGenerator(themeManager));
		packGenerator.registerGenerator(new LineGenerator(schemaManager));
		packGenerator.registerGenerator(new IconGenerator(iconManager));
		packGenerator.registerGenerator(new TextureGenerator());
		packGenerator.generate();

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

	private void clearCache() {
		TextLine.clearReplaceables();
		TooltipCache.clear();
		FurnitureCache.clear();
	}

	private void addSpaceCharWidth(boolean useSpaces) {

		SizedChar space = new SizedChar(' ');

		if (useSpaces) {
			space.setHeight(1);
			space.setAbsoluteWidth(1);
			space.setImageHeight(1);
		} else {
			space.setHeight(1);
			space.setAbsoluteWidth(1);
			space.setImageHeight(1);
		}

		Widths.add(space);

	}

	private void addLocalPlaceholders() {

		if (furnitureProvider != null) {
			Placeholders.addLocal("furniture", new SimplePlaceholderParser((p, s) -> {
				if (!s.equalsIgnoreCase("furniture_id") && !s.equalsIgnoreCase("furniture_name")) {
					return null;
				}

				final boolean name = s.equalsIgnoreCase("furniture_name");

				// Already cached by LookingAtFurniture condition
				if (LookingAtCache.contains(p)) {
					final String cachedId = LookingAtCache.get(p);
					return (name ? Utils.getFurnitureDisplayName(furnitureProvider, cachedId) : cachedId);
				}

				Predicate<Block> blockPredicate = (block -> {
					if (block == null)
						return false;
					return furnitureProvider.isFurniture(block);
				});

				Predicate<Entity> entityFilter = (entity -> {
					if (entity == null)
						return false;
					if (entity.equals(p))
						return false;
					return Tooltips.FURNITURE_ENTITIES.contains(entity.getType());
				});

				var rayTrace = Utils.getRayTrace(p, 10, blockPredicate, entityFilter);

				if (rayTrace == null)
					return null;

				if (rayTrace.getHitBlock() != null) {
					final Block hitBlock = rayTrace.getHitBlock();
					final String id = furnitureProvider.getFurnitureId(hitBlock);
					return (name ? Utils.getFurnitureDisplayName(furnitureProvider, id) : id);
				}

				if (rayTrace.getHitEntity() != null) {
					final Entity hitEntity = rayTrace.getHitEntity();
					final String id = furnitureProvider.getFurnitureId(hitEntity);
					return (name ? Utils.getFurnitureDisplayName(furnitureProvider, id) : id);
				}

				return null;
			}));
		}

		Placeholders.addLocal("var", new SimplePlaceholderParser((p, s) -> {
			if (!s.startsWith("var_"))
				return null;
			boolean global = s.startsWith("var_global_");
			int cutIndex = (global ? 11 : 4);

			String variableName = s.substring(cutIndex);
			variableName = Placeholders.replacePlaceholders(p, variableName);

			Argument returnArgument = null;

			if (global) {
				returnArgument = Variables.LOCAL.getVar(variableName);
			} else {
				returnArgument = Variables.LOCAL.getVar(p, variableName);
			}

			if (returnArgument == null || returnArgument.getAsString() == null)
				return "0";

			return returnArgument.getAsString();
		}));

		Placeholders.addLocal("persistentvar", new SimplePlaceholderParser((p, s) -> {
			if (!s.startsWith("persistentvar_"))
				return null;

			boolean global = s.startsWith("persistentvar_global_");
			int cutIndex = (global ? 21 : 14);

			String variableName = s.substring(cutIndex);
			variableName = Placeholders.replacePlaceholders(p, variableName);

			Argument returnArgument = null;

			if (global) {
				returnArgument = Variables.PERSISTENT.getVar(variableName);
			} else {
				returnArgument = Variables.PERSISTENT.getVar(p, variableName);
			}

			if (returnArgument == null || returnArgument.getAsString() == null)
				return "0";

			return returnArgument.getAsString();
		}));

	}

	// ------------------------------------------------------

	public static Tooltips get() {
		return INSTANCE;
	}
	
	public TitleManager getTitleManager() {
		return titleManager;
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
	
	public PacketProvider getPacketProvider() {
		return packetProvider;
	}
	
	public IntegratedPlugin getPacketPlugin() {
		return packetPlugin;
	}

	public FurnitureProvider getFurnitureProvider() {
		return furnitureProvider;
	}

	public static Logger logger() {
		return LOGGER;
	}
	
	public boolean isUseSpaces() {
		return USE_SPACES;
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
