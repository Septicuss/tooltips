package fi.septicuss.tooltips.object.title;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class TitleBuilder {

	private ProtocolManager protocolManager;

	private WrappedChatComponent title = WrappedChatComponent.fromText("");
	private WrappedChatComponent subtitle = WrappedChatComponent.fromText("");

	private int fadeIn = 0;
	private int stay = 0;
	private int fadeOut = 0;

	public TitleBuilder(ProtocolManager protocolManager) {
		this.protocolManager = protocolManager;
	}

	public Title build() {
		return new Title(protocolManager, title, subtitle, fadeIn, stay, fadeOut);
	}

	public TitleBuilder clone() {
		TitleBuilder clone = new TitleBuilder(protocolManager);
		clone.setTitle(title);
		clone.setSubtitle(subtitle);
		clone.setFadeIn(fadeIn);
		clone.setStay(stay);
		clone.setFadeOut(fadeOut);
		return clone;
	}

	public TitleBuilder setTitleText(String text) {
		this.title = WrappedChatComponent.fromText(text);
		return this;
	}

	public TitleBuilder setSubtitleText(String text) {
		this.subtitle = WrappedChatComponent.fromText(text);
		return this;
	}

	public TitleBuilder setTitle(BaseComponent[] title) {
		String json = ComponentSerializer.toString(title);
		this.title = WrappedChatComponent.fromJson(json);
		return this;
	}

	public TitleBuilder setSubtitle(BaseComponent[] subtitle) {
		String json = ComponentSerializer.toString(subtitle);
		this.subtitle = WrappedChatComponent.fromJson(json);
		return this;
	}

	public TitleBuilder setTitle(String titleJson) {
		this.title = WrappedChatComponent.fromJson(titleJson);
		return this;
	}

	public TitleBuilder setSubtitle(String subtitleJson) {
		this.subtitle = WrappedChatComponent.fromJson(subtitleJson);
		return this;
	}

	public TitleBuilder setTitle(WrappedChatComponent title) {
		this.title = title;
		return this;
	}

	public TitleBuilder setSubtitle(WrappedChatComponent subtitle) {
		this.subtitle = subtitle;
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
