package fi.septicuss.tooltips;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.septicuss.tooltips.commands.TooltipsCommand;
import fi.septicuss.tooltips.commands.subcommands.DebugCommand;
import fi.septicuss.tooltips.commands.subcommands.EvalCommand;
import fi.septicuss.tooltips.commands.subcommands.ListVarsCommand;
import fi.septicuss.tooltips.commands.subcommands.ReloadCommand;
import fi.septicuss.tooltips.commands.subcommands.SendPresetCommand;
import fi.septicuss.tooltips.commands.subcommands.SendThemeCommand;
import fi.septicuss.tooltips.commands.subcommands.VarsCommand;
import fi.septicuss.tooltips.listener.PlayerConnectionListener;
import fi.septicuss.tooltips.listener.PlayerInteractListener;
import fi.septicuss.tooltips.listener.PlayerMovementListener;
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
import fi.septicuss.tooltips.managers.icon.IconManager;
import fi.septicuss.tooltips.managers.integration.IntegrationManager;
import fi.septicuss.tooltips.managers.integration.impl.axgens.LookingAtAxGen;
import fi.septicuss.tooltips.managers.preset.PresetManager;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import fi.septicuss.tooltips.managers.preset.functions.impl.DataFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.HasDataFunction;
import fi.septicuss.tooltips.managers.schema.SchemaManager;
import fi.septicuss.tooltips.managers.theme.ThemeManager;
import fi.septicuss.tooltips.managers.title.TitleManager;
import fi.septicuss.tooltips.managers.tooltip.TooltipManager;
import fi.septicuss.tooltips.pack.PackGenerator;
import fi.septicuss.tooltips.pack.impl.IconGenerator;
import fi.septicuss.tooltips.pack.impl.LineGenerator;
import fi.septicuss.tooltips.pack.impl.SpaceGenerator;
import fi.septicuss.tooltips.pack.impl.TextureGenerator;
import fi.septicuss.tooltips.pack.impl.ThemeGenerator;
import fi.septicuss.tooltips.utils.FileSetup;
import fi.septicuss.tooltips.utils.Messaging;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureCache;
import fi.septicuss.tooltips.utils.cache.tooltip.TooltipCache;
import fi.septicuss.tooltips.utils.font.Widths;
import fi.septicuss.tooltips.utils.font.Widths.SizedChar;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import fi.septicuss.tooltips.utils.placeholder.impl.SimplePlaceholderParser;
import fi.septicuss.tooltips.utils.variable.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class Tooltips extends JavaPlugin {

	public static Gson GSON = new GsonBuilder().create();
	public static boolean SUPPORT_DISPLAY_ENTITIES;
	public static List<EntityType> FURNITURE_ENTITIES;
	private static Tooltips INSTANCE;
	private static Logger LOGGER;
	private static boolean USE_SPACES;

	private IntegrationManager integrationManager;
	private TitleManager titleManager;
	private SchemaManager schemaManager;
	private IconManager iconManager;
	private ThemeManager themeManager;
	private PresetManager presetManager;
	private ConditionManager conditionManager;
	private TooltipManager tooltipManager;

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
	public void onEnable() {
		FileSetup.performMigration(this);
		FileSetup.setupFiles(this);

		titleManager = new TitleManager(this);
		conditionManager = new ConditionManager();

		this.loadIntegrations();
		this.reload();
		this.loadVariables();
		this.registerDefaultContent();
		this.loadListeners();
		this.loadCommands();
	}

	@Override
	public void onDisable() {
		if (this.tooltipManager != null)
			this.tooltipManager.stopTasks();

		Variables.PERSISTENT.save();
	}

	// ------------------------------------------------------

	private void registerDefaultContent() {
		this.registerConditions();
		this.registerLocalPlaceholders();
		this.registerFunctions();
	}

	private void registerConditions() {
		this.conditionManager.register("day", new Day());
		this.conditionManager.register("night", new Night());
		this.conditionManager.register("world", new World());
		this.conditionManager.register("gamemode", new Gamemode());
		this.conditionManager.register("sneaking", new Sneaking());
		this.conditionManager.register("compare", new Compare());
		this.conditionManager.register("lookingatblock", new LookingAtBlock());
		this.conditionManager.register("lookingatfurniture", new LookingAtFurniture());
		this.conditionManager.register("lookingatentity", new LookingAtEntity());
		this.conditionManager.register("lookingatmythicmob", new LookingAtMythicMob());
		this.conditionManager.register("region", new Region());
		this.conditionManager.register("incuboid", new InCuboid());
		this.conditionManager.register("location", new Location());
		this.conditionManager.register("standingon", new StandingOn());
		this.conditionManager.register("itemnbtequals", new ItemNbtEquals());
		this.conditionManager.register("entitynbtequals", new EntityNbtEquals());
		this.conditionManager.register("tileentitynbtequals", new TileEntityNbtEquals());
		this.conditionManager.register("blocknbtequals", new BlockNbtEquals());
		this.conditionManager.register("blockstateequals", new BlockStateEquals());
		this.conditionManager.register("time", new Time());
		this.conditionManager.register("equipped", new Equipped());
		this.conditionManager.register("op", new Op());
		this.conditionManager.register("lookingatcitizen", new LookingAtCitizen());
		this.conditionManager.register("permission", new Permission());
		this.conditionManager.register("lookingataxgen", new LookingAtAxGen());
	}

	private void registerFunctions() {
		Functions.add("data", new DataFunction(this.presetManager));
		Functions.add("hasdata", new HasDataFunction(this.presetManager));
	}

	private void registerLocalPlaceholders() {

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
	
	private void loadVariables() {
		final File variablesDirectory = new File(getDataFolder(), ".data/variables");
		Variables.PERSISTENT.load(variablesDirectory);
	}

	private void loadIntegrations() {
		this.integrationManager = new IntegrationManager(this);
		this.integrationManager.registerDefaultIntegrations();
	}

	private void loadListeners() {
		PluginManager pluginManager = Bukkit.getPluginManager();

		if (!this.integrationManager.getAreaProviders().isEmpty()) {
			pluginManager.registerEvents(new PlayerMovementListener(this.integrationManager), this);
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

		// Stop already running tasks
		if (this.tooltipManager != null) {
			this.tooltipManager.stopTasks();
		}

		this.reloadConfig();

		clearCache();

		FileSetup.setupFiles(this);

		USE_SPACES = this.getConfig().getBoolean("use-spaces", true);

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

		this.tooltipManager.runTasks();

		if (this.playerInteractListener != null)
			playerInteractListener.setTooltipManager(this.tooltipManager);

	}

	private void clearCache() {
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

	// ------------------------------------------------------

	public static Tooltips get() {
		return INSTANCE;
	}

	public IntegrationManager getIntegrationManager() {
		return integrationManager;
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

	public SchemaManager getSchemaManager() {
		return schemaManager;
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
	
	public int getCheckFrequency() {
		return this.getConfig().getInt("condition-check-frequency", 3);
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
