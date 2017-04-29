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

package co.marcin.novaguilds.api.util;


import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.TagColor;

public interface PreparedTag {
	/**
	 * Gets the guild
	 *
	 * @return the guild
	 */
	NovaGuild getGuild();

	/**
	 * Gets the color
	 *
	 * @return the color enum
	 */
	TagColor getColor();

	/**
	 * Returns if the leader prefix is being displayed
	 *
	 * @return true if the prefix is displayed
	 */
	boolean isLeaderPrefix();

	/**
	 * Returns if the tag is hidden
	 *
	 * @return true if hidden
	 */
	boolean isHidden();

	/**
	 * Sets hidden flag
	 *
	 * @param hidden flag boolean
	 */
	void setHidden(boolean hidden);

	/**
	 * Sets the leader prefix flag
	 *
	 * @param leaderPrefix flag boolean
	 */
	void setLeaderPrefix(boolean leaderPrefix);

	/**
	 * Sets the color
	 *
	 * @param color the color enum
	 */
	void setColor(TagColor color);

	/**
	 * Sets the tag up for a specific player
	 *
	 * @param nPlayer the owner of the tag
	 */
	void setUpFor(NovaPlayer nPlayer);

	/**
	 * Sets tag color for a player
	 *
	 * @param nPlayer player which will receive the message
	 */
	void setTagColorFor(NovaPlayer nPlayer);

	/**
	 * Gets the tag
	 *
	 * @return the tag string
	 */
	String get();
}
