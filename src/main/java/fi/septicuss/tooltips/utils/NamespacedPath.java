package fi.septicuss.tooltips.utils;

import fi.septicuss.tooltips.Tooltips;

public class NamespacedPath {

	private String namespace = "minecraft";
	private String relativePath;
	private String fullPath;
	private String category;
	private String namespacedPath;

	/**
	 * @param namespacedPath minecraft:relative_path/texture.png
	 * @param category       textures, font
	 */
	public NamespacedPath(String namespacedPath, String category) {
		this.namespacedPath = namespacedPath;
		this.category = category;

		String[] splitPath = namespacedPath.split(":");

		if (splitPath.length == 1) {
			this.relativePath = splitPath[0];
		} else if (splitPath.length == 2) {
			this.namespace = splitPath[0];
			this.relativePath = splitPath[1];
		} else {
			Tooltips.warn(String.format("Failed loading path \"%s\". Two namespaces?", namespacedPath));
			return;
		}

		this.fullPath = namespace + "/textures/" + relativePath;
	}

	/**
	 * @param fullPath Must be inside .../assets/ | For example
	 *                 "tooltips/textures/font/accented.png"
	 */
	public NamespacedPath(String fullPath) {
		this.fullPath = fullPath;

		int namespaceBreakIndex = fullPath.indexOf('/');

		this.namespace = fullPath.substring(0, namespaceBreakIndex);
		String path = fullPath.substring(namespaceBreakIndex + 1);

		int categoryBreakIndex = path.indexOf('/');

		this.category = path.substring(0, categoryBreakIndex);
		this.relativePath = path.substring(categoryBreakIndex + 1);

		this.namespacedPath = namespace + ":" + relativePath;
	}

	/**
	 * @return minecraft
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return (Inside of assets/namespace/category/) custom/texture.png
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * @return (Inside of assets/) minecraft/textures/custom/texture.png
	 */
	public String getFullPath() {
		return fullPath;
	}

	/**
	 * @return (Inside of assets/namespace/) textures
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return minecraft:custom/texture.png
	 */
	public String getNamespacedPath() {
		return namespacedPath;
	}

}
