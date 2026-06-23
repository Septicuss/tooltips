package fi.septicuss.tooltips.managers.integration;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.integration.impl.axgens.LookingAtAxGen;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.BetonQuestIntegration;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationIO;
import fi.septicuss.tooltips.managers.integration.impl.craftengine.CraftEngineFurnitureProvider;
import fi.septicuss.tooltips.managers.integration.impl.crucible.CrucibleFurnitureProvider;
import fi.septicuss.tooltips.managers.integration.impl.itemsadder.ItemsAdderBlockProvider;
import fi.septicuss.tooltips.managers.integration.impl.itemsadder.ItemsAdderFurnitureProvider;
import fi.septicuss.tooltips.managers.integration.impl.nexo.NexoBlockProvider;
import fi.septicuss.tooltips.managers.integration.impl.nexo.NexoFurnitureProvider;
import fi.septicuss.tooltips.managers.integration.impl.nexo.NexoListener;
import fi.septicuss.tooltips.managers.integration.impl.oraxen.OraxenFurnitureProvider;
import fi.septicuss.tooltips.managers.integration.impl.packetevents.PacketEventsPacketProvider;
import fi.septicuss.tooltips.managers.integration.impl.papi.TooltipsExpansion;
import fi.septicuss.tooltips.managers.integration.impl.protocollib.ProtocolLibPacketProvider;
import fi.septicuss.tooltips.managers.integration.impl.vanilla.VanillaBlockProvider;
import fi.septicuss.tooltips.managers.integration.impl.worldguard.WorldGuardAreaProvider;
import fi.septicuss.tooltips.managers.integration.providers.AreaProvider;
import fi.septicuss.tooltips.managers.integration.providers.BlockProvider;
import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.providers.PacketProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class IntegrationManager {

    private final Tooltips plugin;

    private final HashMap<String, FurnitureProvider> furnitureProviders = new HashMap<>();
    private final HashMap<String, AreaProvider> areaProviders = new HashMap<>();
    private final HashMap<String, BlockProvider> blockProviders = new HashMap<>();
    private final Set<String> registeredDependencies = new HashSet<>();
    private PacketProvider packetProvider;

    public IntegrationManager(Tooltips plugin) {
        this.plugin = plugin;
    }

    public void registerDefaultIntegrations() {

        // Register vanilla Minecraft providers
        this.addBlockProvider(new VanillaBlockProvider());

        if (isPresent("packetevents")) {
            this.setPacketProvider(new PacketEventsPacketProvider());
            registeredDependencies.add("packetevents");
        } else if (isPresent("ProtocolLib")) {
            this.setPacketProvider(new ProtocolLibPacketProvider());
            registeredDependencies.add("ProtocolLib");
        }

        if (isPresent("Nexo")) {
            this.addFurnitureProvider(new NexoFurnitureProvider());
            this.addBlockProvider(new NexoBlockProvider());
            this.plugin.getServer().getPluginManager().registerEvents(new NexoListener(plugin), plugin);
            registeredDependencies.add("Nexo");
        }

        if (isPresent("Oraxen")) {
            this.addFurnitureProvider(new OraxenFurnitureProvider());
            registeredDependencies.add("Oraxen");
        }

        if (isPresent("ItemsAdder")) {
            this.addFurnitureProvider(new ItemsAdderFurnitureProvider());
            this.addBlockProvider(new ItemsAdderBlockProvider());
            registeredDependencies.add("ItemsAdder");
        }

        if (isPresent("MythicCrucible")) {
            this.addFurnitureProvider(new CrucibleFurnitureProvider());
            registeredDependencies.add("MythicCrucible");
        }

        if (isPresent("CraftEngine")) {
            if (areClassesPresent(
                    "net.momirealms.craftengine.core.entity.furniture.setting.FurnitureSettingsModifierFactory"
            )) {
                this.addFurnitureProvider(new CraftEngineFurnitureProvider());
                registeredDependencies.add("CraftEngine");
            } else {
                Tooltips.warn("This version of Tooltips is not compatible with your CraftEngine version. Integration not loaded.");
            }
        }

        if (isPresent("WorldGuard")) {
            this.addAreaProvider(new WorldGuardAreaProvider());
            registeredDependencies.add("WorldGuard");
        }

        if (isPresent("AxGens")) {
            Tooltips.get().getConditionManager().register(new LookingAtAxGen());
        }

        if (isPresent("PlaceholderAPI")) {
            TooltipsExpansion expansion = new TooltipsExpansion();
            if (expansion.isRegistered())
                expansion.unregister();
            expansion.register();
            registeredDependencies.add("PlaceholderAPI");
        }

        if (isPresent("BetonQuest")) {
            if (areClassesPresent(
                    "org.betonquest.betonquest.conversation.ConversationIOFactory",
                    "org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry",
                    "org.betonquest.betonquest.api.profile.OnlineProfile"
            )) {
                BetonQuestIntegration.initialize(plugin);
                registeredDependencies.add("BetonQuest");
            } else {
                Tooltips.warn("This version of Tooltips is not compatible with your BetonQuest version. Integration not loaded.");
            }

        }

    }

    public void disable() {

        if (registeredDependencies.contains("BetonQuest")) {
            BetonQuestIntegration.disable();
        }

    }

    public Optional<FurnitureWrapper> getFurniture(final @Nonnull Block block) {
        for (FurnitureProvider provider : this.furnitureProviders.values()) {
            final FurnitureWrapper furniture = provider.getFurniture(block);

            if (furniture == null) {
                continue;
            }

            return Optional.ofNullable(provider.getFurniture(block));
        }
        return Optional.empty();
    }

    public Optional<FurnitureWrapper> getFurniture(final @Nonnull Entity entity) {
        for (FurnitureProvider provider : this.furnitureProviders.values()) {
            final FurnitureWrapper furniture = provider.getFurniture(entity);

            if (furniture == null) {
                continue;
            }

            return Optional.ofNullable(provider.getFurniture(entity));
        }
        return Optional.empty();
    }

    public boolean areClassesPresent(String... classes) {
        for (String className : classes) {
            try {
                Class.forName(className, false, getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    public boolean isPresent(String plugin) {
        return (Bukkit.getPluginManager().getPlugin(plugin) != null);
    }

    public FurnitureProvider getFurnitureProvider(String id) {
        return this.furnitureProviders.get(id);
    }

    public HashMap<String, FurnitureProvider> getFurnitureProviders() {
        return furnitureProviders;
    }

    public void addFurnitureProvider(FurnitureProvider furnitureProvider) {
        this.furnitureProviders.put(furnitureProvider.identifier(), furnitureProvider);
    }

    public void removeFurnitureProvider(String id) {
        this.furnitureProviders.remove(id);
    }

    public AreaProvider getAreaProvider(String id) {
        return this.areaProviders.get(id);
    }

    public HashMap<String, AreaProvider> getAreaProviders() {
        return areaProviders;
    }

    public void addAreaProvider(AreaProvider areaProvider) {
        this.areaProviders.put(areaProvider.identifier(), areaProvider);
    }

    public void removeAreaProvider(String id) {
        this.areaProviders.remove(id);
    }

    public BlockProvider getVanillaBlockProvider() {
        return this.blockProviders.get("minecraft");
    }

    public BlockProvider getBlockProvider(String id) {
        return this.blockProviders.get(id);
    }

    public Collection<BlockProvider> getBlockProviders() {
        return Collections.unmodifiableCollection(this.blockProviders.values());
    }

    public void addBlockProvider(BlockProvider blockProvider) {
        this.blockProviders.put(blockProvider.identifier(), blockProvider);
    }

    public void removeBlockProvider(String id) {
        this.blockProviders.remove(id);
    }

    public PacketProvider getPacketProvider() {
        return this.packetProvider;
    }

    public void setPacketProvider(PacketProvider packetProvider) {
        this.packetProvider = packetProvider;
    }

}
