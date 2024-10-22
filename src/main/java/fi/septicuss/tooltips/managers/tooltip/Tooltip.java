package fi.septicuss.tooltips.managers.tooltip;

import net.kyori.adventure.text.Component;

public class Tooltip {

	private Component component;

	public Tooltip(Component component) {
		this.component = component;
	}

	public Component getComponent() {
		return component;
	}

	@Override
	public int hashCode() {
		return component.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return component.equals(obj);
	}

	
	
}
