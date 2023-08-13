package fi.septicuss.tooltips.pack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonObject;

import fi.septicuss.tooltips.object.NamespacedPath;
import fi.septicuss.tooltips.utils.Utils;

public class PackData {

	private EnumMap<ProviderType, Set<JsonObject>> providerMap;
	private Set<NamespacedPath> usedTextures;
	private Set<Integer> usedAscents;
	
	public PackData() {
		this.providerMap = new EnumMap<>(ProviderType.class);
		this.usedTextures = new HashSet<>();
		this.usedAscents = new HashSet<>();
	}

	public void addProviders(ProviderType providerType, JsonObject... providers) {
		boolean containsType = providerMap.containsKey(providerType);
		Set<JsonObject> objectSet = ((containsType) ? providerMap.get(providerType) : new HashSet<>());
		
		for (var provider : providers)
			objectSet.add(provider);
		
		providerMap.put(providerType, objectSet);
	}
	
	public void addUsedTexture(NamespacedPath path) {
		usedTextures.add(path);
	}
	
	public Set<NamespacedPath> getUsedTextures() {
		return Collections.unmodifiableSet(usedTextures);
	}
	
	public void addUsedAscent(int ascent) {
		usedAscents.add(ascent);
	}
	
	public Set<Integer> getUsedAscents() {
		return Collections.unmodifiableSet(usedAscents);
	}
	
	public Set<JsonObject> getProviders(ProviderType providerType) {
		Set<JsonObject> providers = providerMap.get(providerType);
		return Collections.unmodifiableSet(Utils.getJsonSetCopy(providers));
	}
	
	
	

	public enum ProviderType {
		GLOBAL, // Included in every font file
		REGULAR, // Included in regular font lines
		OFFSET; // Included in offset font lines
	}

}
