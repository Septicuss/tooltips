package fi.septicuss.tooltips.managers.title;

import fi.septicuss.tooltips.managers.integration.providers.PacketProvider;
import fi.septicuss.tooltips.utils.AdventureUtils;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class TitleBuilder {

	private final TitleManager titleManager;

	private String titleJson = "\"\"";
	private String subtitleJson = "\"\"";

	private int fadeIn = 0;
	private int stay = 0;
	private int fadeOut = 0;

	public TitleBuilder(TitleManager titleManager) {
		this.titleManager = titleManager;
	}

	public Optional<Title<? extends PacketProvider>> build() {
		return titleManager.newTitle(titleJson, subtitleJson, fadeIn, stay, fadeOut);
	}

	public TitleBuilder clone() {
		TitleBuilder clone = new TitleBuilder(titleManager);
		clone.setTitleJson(titleJson);
		clone.setSubtitleJson(subtitleJson);
		clone.setFadeIn(fadeIn);
		clone.setStay(stay);
		clone.setFadeOut(fadeOut);
		return clone;
	}

	public TitleBuilder setTitle(Component title) {
		this.titleJson = AdventureUtils.GSONSERIALIZER.serialize(title);
		return this;
	}

	public TitleBuilder setSubtitle(Component subtitle) {
		this.subtitleJson = AdventureUtils.GSONSERIALIZER.serialize(subtitle);
		return this;
	}

	public TitleBuilder setTitleJson(String titleJson) {
		this.titleJson = titleJson;
		return this;
	}

	public TitleBuilder setSubtitleJson(String subtitleJson) {
		this.subtitleJson = subtitleJson;
		return this;
	}

	public TitleBuilder setFadeIn(int fadeIn) {
		this.fadeIn = fadeIn;
		return this;
	}

	public TitleBuilder setStay(int stay) {
		this.stay = stay;
		return this;
	}

	public TitleBuilder setFadeOut(int fadeOut) {
		this.fadeOut = fadeOut;
		return this;
	}

}
