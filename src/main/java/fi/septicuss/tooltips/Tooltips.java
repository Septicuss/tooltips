package fi.septicuss.tooltips;

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
import fi.septicuss.tooltips.commands.subcommands.StopDialogueCommand;
import fi.septicuss.tooltips.commands.subcommands.VarsCommand;
import fi.septicuss.tooltips.listener.PlayerConnectionListener;
import fi.septicuss.tooltips.listener.PlayerInteractListener;
import fi.septicuss.tooltips.listener.PlayerMovementListener;
import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.ConditionManager;
import fi.septicuss.tooltips.managers.condition.impl.Compare;
import fi.septicuss.tooltips.managers.condition.impl.equals.BlockNbtEquals;
import fi.septicuss.tooltips.managers.condition.impl.equals.BlockStateEquals;
import fi.septicuss.tooltips.managers.condition.impl.equals.EntityNbtEquals;
import fi.septicuss.tooltips.managers.condition.impl.equals.ItemNbtEquals;
import fi.septicuss.tooltips.managers.condition.impl.lookingat.LookingAtBlock;
import fi.septicuss.tooltips.managers.condition.impl.lookingat.LookingAtCitizen;
import fi.septicuss.tooltips.managers.condition.impl.lookingat.LookingAtEntity;
import fi.septicuss.tooltips.managers.condition.impl.lookingat.LookingAtFurniture;
import fi.septicuss.tooltips.managers.condition.impl.lookingat.LookingAtMythicMob;
import fi.septicuss.tooltips.managers.condition.impl.player.Equipped;
import fi.septicuss.tooltips.managers.condition.impl.player.Gamemode;
import fi.septicuss.tooltips.managers.condition.impl.player.Op;
import fi.septicuss.tooltips.managers.condition.impl.player.Permission;
import fi.septicuss.tooltips.managers.condition.impl.player.Sneaking;
import fi.septicuss.tooltips.managers.condition.impl.player.StandingOn;
import fi.septicuss.tooltips.managers.condition.impl.world.Day;
import fi.septicuss.tooltips.managers.condition.impl.world.InCuboid;
import fi.septicuss.tooltips.managers.condition.impl.world.Location;
import fi.septicuss.tooltips.managers.condition.impl.world.Night;
import fi.septicuss.tooltips.managers.condition.impl.world.Region;
import fi.septicuss.tooltips.managers.condition.impl.world.Time;
import fi.septicuss.tooltips.managers.condition.impl.world.World;
import fi.septicuss.tooltips.managers.icon.IconManager;
import fi.septicuss.tooltips.managers.integration.IntegrationManager;
import fi.septicuss.tooltips.managers.integration.impl.axgens.LookingAtAxGen;
import fi.septicuss.tooltips.managers.preset.PresetManager;
import fi.septicuss.tooltips.managers.preset.animation.AnimationProvider;
import fi.septicuss.tooltips.managers.preset.animation.Animations;
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
import fi.septicuss.tooltips.managers.preset.functions.impl.StaticFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.StripFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.UppercaseFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.variable.HasPVarFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.variable.HasVarFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.variable.PVarFunction;
import fi.septicuss.tooltips.managers.preset.functions.impl.variable.VarFunction;
import fi.septicuss.tooltips.managers.schema.SchemaManager;
import fi.septicuss.tooltips.managers.theme.ThemeManager;
import fi.septicuss.tooltips.managers.title.TitleManager;
import fi.septicuss.tooltips.managers.tooltip.TooltipManager;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.pack.PackGenerator;
import fi.septicuss.tooltips.pack.impl.IconGenerator;
import fi.septicuss.tooltips.pack.impl.LineGenerator;
import fi.septicuss.tooltips.pack.impl.SpaceGenerator;
import fi.septicuss.tooltips.pack.impl.TextureGenerator;
import fi.septicuss.tooltips.pack.impl.ThemeGenerator;
import fi.septicuss.tooltips.utils.AdventureUtils;
import fi.septicuss.tooltips.utils.FileSetup;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureCache;
import fi.septicuss.tooltips.utils.cache.tooltip.TooltipCache;
import fi.septicuss.tooltips.utils.font.Widths;
import fi.septicuss.tooltips.utils.variable.Variables;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.logging.Logger;

public class Tooltips extends JavaPlugin {

	public static Gson GSON = new GsonBuilder().create();
	public static HashSet<String> WARNINGS = new HashSet<>();

	private static Tooltips INSTANCE;
	private static Logger LOGGER;
	private static boolean USE_SPACES;
	private static boolean USE_SHADOWS;


	static {
		boolean useShadowsTemp = true;
		try {
			// Try to load the problematic class/method
			Class.forName("net.kyori.adventure.text.minimessage.tag.standard.StandardTags")
					.getMethod("shadowColor");
		} catch (Throwable t) {
			useShadowsTemp = false;
		}
		USE_SHADOWS = useShadowsTemp;
	}

	private BukkitAudiences adventure;
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
		LOGGER = super.getLogger();
	}

	// ------------------------------------------------------

	@Override
	public void onEnable() {
		FileSetup.setupFiles(this);

		this.adventure = BukkitAudiences.create(this);

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

		if(this.adventure != null) {
			this.adventure.close();
			this.adventure = null;
		}

		Variables.PERSISTENT.save();
	}

	// ------------------------------------------------------

	private void registerDefaultContent() {
		this.registerConditions();
		this.registerFunctions();
		this.registerAnimations();
	}

	private void registerConditions() {
		this.conditionManager.register(
				new Day(),
				new Night(),
				new World(),
				new Gamemode(),
				new Sneaking(),
				new Compare(),
				new LookingAtBlock(),
				new LookingAtFurniture(this.integrationManager),
				new LookingAtEntity(),
				new LookingAtMythicMob(),
				new Region(),
				new InCuboid(),
				new Location(),
				new StandingOn(),
				new ItemNbtEquals(),
				new EntityNbtEquals(),
				new BlockNbtEquals(),
				new BlockStateEquals(),
				new Time(),
				new Equipped(),
				new Op(),
				new LookingAtCitizen(),
				new Permission(),
				new LookingAtAxGen()
		);

		// Register conditions registered via the API
		if (!TooltipsAPI.getConditionQueue().isEmpty()) {
			this.conditionManager.register(TooltipsAPI.getConditionQueue().toArray(new Condition[0]));
		}

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
		Functions.add("strip", new StripFunction());
		Functions.add("static", new StaticFunction());

		// Variables
		Functions.add("var", new VarFunction());
		Functions.add("hasvar", new HasVarFunction());
		Functions.add("pvar", new PVarFunction());
		Functions.add("haspvar", new HasPVarFunction());
	}

	private void registerAnimations() {
		final AnimationProvider typewriter = new TypewriterAnimationProvider();
		Animations.addProvider("typewriter", typewriter);
		Animations.addProvider("tw", typewriter);
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
		final TooltipsCommand tooltipsCommand = new TooltipsCommand(this);
		tooltipsCommand.register("sendtheme", new SendThemeCommand(this));
		tooltipsCommand.register("sendpreset", new SendPresetCommand(this));
		tooltipsCommand.register("reload", new ReloadCommand(this));
		tooltipsCommand.register("eval", new EvalCommand(this));
		tooltipsCommand.register("vars", new VarsCommand());
		tooltipsCommand.register("listvars", new ListVarsCommand());
		tooltipsCommand.register("debug", new DebugCommand(this));
		tooltipsCommand.register("stopdialogue", new StopDialogueCommand());

		final PluginCommand tooltipsPluginCommand = Objects.requireNonNull(super.getCommand("tooltips"));
		tooltipsPluginCommand.setExecutor(tooltipsCommand);
		tooltipsPluginCommand.setTabCompleter(tooltipsCommand);
	}

	public void reload() {

		WARNINGS.clear();

		// Stop already running tasks
		if (this.tooltipManager != null) {
			this.tooltipManager.stopTasks();
		}

		this.reloadConfig();
		this.clearCache();

		FileSetup.setupFiles(this);

		USE_SPACES = this.getConfig().getBoolean("use-spaces", true);
		USE_SHADOWS = this.getConfig().getBoolean("use-shadows", false);

		this.schemaManager = new SchemaManager();
		this.iconManager = new IconManager();
		this.themeManager = new ThemeManager();
		this.presetManager = new PresetManager();
		this.tooltipManager = new TooltipManager(this);

		schemaManager.loadFrom(new File(getDataFolder(), ".data/schemas"));
		iconManager.loadFrom(new File(getDataFolder(), "icons"));
		themeManager.loadFrom(new File(getDataFolder(), "themes"));
		presetManager.loadFrom(this, new File(getDataFolder(), "presets"));

		Widths.loadOverridingWidths(new File(getDataFolder(), ".data/widths.yml"));
		
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

	// ------------------------------------------------------

	public static Tooltips get() {
		return INSTANCE;
	}

	public static PlayerTooltipData getPlayerTooltipData(Player player) {
		return INSTANCE.getTooltipManager().getPlayerTooltipData(player);
	}

	public BukkitAudiences getAdventure() {
		if(this.adventure == null) {
			throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
		}
		return this.adventure;
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

	public boolean isUseShadows() {
		return USE_SHADOWS;
	}
	
	public boolean isUseSpaces() {
		return USE_SPACES;
	}

	public static void warn(String message) {
		AdventureUtils.sendMessage(Bukkit.getConsoleSender(), ChatColor.RED + "[Tooltips] WARNING: " + message);
	}

	public static void warn(String key, String message) {
		if (WARNINGS.contains(key)) return;
		AdventureUtils.sendMessage(Bukkit.getConsoleSender(), ChatColor.RED + "[Tooltips] WARNING: " + message);
		WARNINGS.add(key);
	}

	public static void log(String message) {
		AdventureUtils.sendMessage(Bukkit.getConsoleSender(), "[Tooltips] " + message);
	}

	public static File getPackAssetsFolder() {
		return new File(INSTANCE.getDataFolder(), "pack/assets");
	}

}
