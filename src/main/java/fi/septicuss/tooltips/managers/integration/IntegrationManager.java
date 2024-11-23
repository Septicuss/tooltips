package fi.septicuss.tooltips.managers.integration;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.integration.impl.axgens.AxGensIntegration;
import fi.septicuss.tooltips.managers.integration.impl.crucible.CrucibleFurnitureProvider;
import fi.septicuss.tooltips.managers.integration.impl.itemsadder.ItemsAdderFurnitureProvider;
import fi.septicuss.tooltips.managers.integration.impl.nexo.NexoFurnitureProvider;
import fi.septicuss.tooltips.managers.integration.impl.oraxen.OraxenFurnitureProvider;
import fi.septicuss.tooltips.managers.integration.impl.packetevents.PacketEventsPacketProvider;
import fi.septicuss.tooltips.managers.integration.impl.papi.TooltipsExpansion;
import fi.septicuss.tooltips.managers.integration.impl.protocollib.ProtocolLibPacketProvider;
import fi.septicuss.tooltips.managers.integration.impl.worldguard.WorldGuardAreaProvider;
import fi.septicuss.tooltips.managers.integration.providers.AreaProvider;
import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.providers.PacketProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Optional;

public class IntegrationManager {

    private final HashMap<String, FurnitureProvider> furnitureProviders = new HashMap<>();
    private final HashMap<String, AreaProvider> areaProviders = new HashMap<>();
    private PacketProvider packetProvider;

    public IntegrationManager(Tooltips plugin) {

    }

    public void registerDefaultIntegrations() {

        if (isPresent("packetevents")) {
            this.setPacketProvider(new PacketEventsPacketProvider());
        } else if (isPresent("ProtocolLib")) {
            this.setPacketProvider(new ProtocolLibPacketProvider());
        }

        if (isPresent("Nexo")) {
            this.addFurnitureProvider(new NexoFurnitureProvider());
        }

        if (isPresent("Oraxen")) {
            this.addFurnitureProvider(new OraxenFurnitureProvider());
        }

        if (isPresent("ItemsAdder")) {
            this.addFurnitureProvider(new ItemsAdderFurnitureProvider());
        }

        if (isPresent("MythicCrucible")) {
            this.addFurnitureProvider(new CrucibleFurnitureProvider());
        }

        if (isPresent("WorldGuard")) {
            this.addAreaProvider(new WorldGuardAreaProvider());
        }

        if (isPresent("AxGens")) {
            AxGensIntegration.registerIntegration();
        }

        if (isPresent("PlaceholderAPI")) {
            TooltipsExpansion expansion = new TooltipsExpansion();
            if (expansion.isRegistered())
                expansion.unregister();
            expansion.register();
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

    private boolean isPresent(String plugin) {
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

    public PacketProvider getPacketProvider() {
        return this.packetProvider;
    }

    public void setPacketProvider(PacketProvider packetProvider) {
        this.packetProvider = packetProvider;
    }

}
