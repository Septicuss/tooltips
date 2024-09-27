package fi.septicuss.tooltips.managers.tooltip;

import java.util.Arrays;

import net.md_5.bungee.api.chat.BaseComponent;

public class Tooltip {

	private BaseComponent[] components;

	public Tooltip(BaseComponent[] components) {
		this.components = components;
	}

	public BaseComponent[] getComponents() {
		return components;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(components);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Tooltip)) {
			return false;
		}
		Tooltip other = (Tooltip) obj;
		return Arrays.equals(components, other.components);
	}

	
	
}
