/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.api.basic.ConfigWrapper;
import co.marcin.novaguilds.api.basic.MessageWrapper;

import java.util.ArrayList;
import java.util.List;

public enum ChatMode {
	NORMAL(Message.CHAT_GUILD_CHATMODE_NAMES_NORMAL),
	GUILD(Message.CHAT_GUILD_CHATMODE_NAMES_GUILD, Config.CHAT_GUILD_ENABLED),
	ALLY(Message.CHAT_GUILD_CHATMODE_NAMES_ALLY, Config.CHAT_ALLY_ENABLED);

	private final ConfigWrapper enabledConfig;
	private final MessageWrapper name;

	/**
	 * Constructor with 'enabled' config
	 *
	 * @param name   mode's lang name
	 * @param config config enum to check if mode is enabled
	 */
	ChatMode(MessageWrapper name, ConfigWrapper config) {
		this.name = name;
		this.enabledConfig = config;
	}

	/**
	 * Constructor for normal mode
	 * Enabled by default
	 *
	 * @param name mode's lang name
	 */
	ChatMode(MessageWrapper name) {
		this.name = name;
		this.enabledConfig = null;
	}

	/**
	 * Checks if mode is enabled in config
	 *
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		return enabledConfig == null || enabledConfig.getBoolean();
	}

	/**
	 * Gets Message wrapper of mode's name
	 *
	 * @return the Message
	 */
	public MessageWrapper getName() {
		return name;
	}

	/**
	 * Gets next enabled chat mode
	 *
	 * @return chat mode
	 */
	public ChatMode next() {
		boolean n = false;
		for(ChatMode mode : values()) {
			if(!mode.isEnabled()) {
				continue;
			}

			if(n) {
				return mode;
			}

			n = mode == this;
		}

		return NORMAL;
	}

	/**
	 * Gets chat mode from string
	 *
	 * @param name the string
	 * @return the chat mode
	 */
	public static ChatMode fromString(String name) {
		try {
			return valueOf(name.toUpperCase());
		}
		catch(IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Gets all enabled chat modes
	 *
	 * @return ChatMode array
	 */
	public static ChatMode[] valuesEnabled() {
		final List<ChatMode> list = new ArrayList<>();

		for(ChatMode mode : values()) {
			if(mode.isEnabled()) {
				list.add(mode);
			}
		}

		return list.toArray(new ChatMode[list.size()]);
	}
}
