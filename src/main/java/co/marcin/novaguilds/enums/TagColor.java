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

public enum TagColor {
	NEUTRAL(Config.CHAT_TAGCOLORS_NEUTRAL),
	ALLY(Config.CHAT_TAGCOLORS_ALLY),
	WAR(Config.CHAT_TAGCOLORS_WAR),
	GUILD(Config.CHAT_TAGCOLORS_GUILD);

	private final ConfigWrapper config;

	/**
	 * The constructor
	 *
	 * @param config color character from config
	 */
	TagColor(ConfigWrapper config) {
		this.config = config;
	}

	/**
	 * Gets the config enum
	 *
	 * @return the enum
	 */
	public ConfigWrapper getConfig() {
		return config;
	}
}
