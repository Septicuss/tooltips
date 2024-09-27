package fi.septicuss.tooltips.managers.tooltip.building.element;

import java.util.List;

import net.md_5.bungee.api.chat.BaseComponent;

public interface TooltipElement {

	public List<BaseComponent> getParts();

	public int getWidth();

}
