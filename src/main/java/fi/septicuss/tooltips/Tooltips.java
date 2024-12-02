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
import fi.septicuss.tooltips.managers.preset.animation.AnimationProvider;
import fi.septicuss.tooltips.managers.preset.animation.Animations;
import fi.septicuss.tooltips.managers.preset.animation.impl.StaticAnimationProvider;
import fi.septicuss.tooltips.managers.preset.animation.impl.TypewriterAnimationProvider;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import fi.septicuss.tooltips.managers.preset.functions.impl.AnimationFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.CapitalizeFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.ContextFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.DataFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.HasContextFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.HasDataFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.IfFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.LowercaseFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.ParseFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.PreprocessFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.UppercaseFunction;
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
import fi.septicuss.tooltips.utils.placeholder.impl.PersistentVariablePlaceholder;
import fi.septicuss.tooltips.utils.placeholder.impl.VariablePlaceholder;
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
		this.registerDefaultContent();
		this.reload();
		this.loadVariables();
		this.loadListeners();
		this.loadCommands();
	}

	@Override
	public void onDisable() {
		if (this.tooltipManager != null)
			this.tooltipManager.stopTasks();

		if (this.integrationManager != null)
			this.integrationManager.disable();

		Variables.PERSISTENT.save();
	}

	// ------------------------------------------------------

	private void registerDefaultContent() {
		this.registerConditions();
		this.registerLocalPlaceholders();
		this.registerFunctions();
		this.registerAnimations();
	}

	private void registerConditions() {
		this.conditionManager.register(new Day());
		this.conditionManager.register(new Night());
		this.conditionManager.register(new World());
		this.conditionManager.register(new Gamemode());
		this.conditionManager.register(new Sneaking());
		this.conditionManager.register(new Compare());
		this.conditionManager.register(new LookingAtBlock());
		this.conditionManager.register(new LookingAtFurniture(this.integrationManager));
		this.conditionManager.register(new LookingAtEntity());
		this.conditionManager.register(new LookingAtMythicMob());
		this.conditionManager.register(new Region());
		this.conditionManager.register(new InCuboid());
		this.conditionManager.register(new Location());
		this.conditionManager.register(new StandingOn());
		this.conditionManager.register(new ItemNbtEquals());
		this.conditionManager.register(new EntityNbtEquals());
		this.conditionManager.register(new BlockNbtEquals());
		this.conditionManager.register(new BlockStateEquals());
		this.conditionManager.register(new Time());
		this.conditionManager.register(new Equipped());
		this.conditionManager.register(new Op());
		this.conditionManager.register(new LookingAtCitizen());
		this.conditionManager.register(new Permission());
		this.conditionManager.register(new LookingAtAxGen());
	}

	private void registerFunctions() {
		Functions.add("data", new DataFunction(this));
		Functions.add("hasdata", new HasDataFunction(this));
		Functions.add("context", new ContextFunction(this));
		Functions.add("hascontext", new HasContextFunction(this));
		Functions.add("capitalize", new CapitalizeFunction());
		Functions.add("lowercase", new LowercaseFunction());
		Functions.add("uppercase", new UppercaseFunction());
		Functions.add("if", new IfFunction());
		Functions.add("parse", new ParseFunction());
		Functions.add("tta", new AnimationFunction());
		Functions.add("preprocess", new PreprocessFunction());
	}

	private void registerAnimations() {
		final AnimationProvider typewriter = new TypewriterAnimationProvider();
		Animations.addProvider("typewriter", typewriter);
		Animations.addProvider("tw", typewriter);
		final AnimationProvider staticProvider = new StaticAnimationProvider();
		Animations.addProvider("static", staticProvider);
		Animations.addProvider("s", staticProvider);
	}

	private void registerLocalPlaceholders() {
		Placeholders.addLocal("var", new VariablePlaceholder());
		Placeholders.addLocal("persistentvar", new PersistentVariablePlaceholder());
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

		pluginManager.registerEvents(new PlayerInteractListener(this), this);
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

		Bukkit.getScheduler().runTaskLater(this, () -> this.tooltipManager.runTasks(), 10L);
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
