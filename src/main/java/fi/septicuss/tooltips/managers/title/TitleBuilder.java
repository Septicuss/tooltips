package fi.septicuss.tooltips.managers.title;

import fi.septicuss.tooltips.integrations.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class TitleBuilder {

	private TitleManager titleManager;

	private String titleJson = "\"\"";
	private String subtitleJson = "\"\"";

	private int fadeIn = 0;
	private int stay = 0;
	private int fadeOut = 0;

	public TitleBuilder(TitleManager titleManager) {
		this.titleManager = titleManager;
	}

	public Title build() {
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

	public TitleBuilder setTitle(BaseComponent[] title) {
		this.titleJson = ComponentSerializer.toString(title);
		return this;
	}

	public TitleBuilder setSubtitle(BaseComponent[] subtitle) {
		this.subtitleJson = ComponentSerializer.toString(subtitle);
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
