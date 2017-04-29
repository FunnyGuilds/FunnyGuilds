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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Minecraft 1.8 Title
 *
 * @author Maxim Van de Wynckel
 * @version 1.0.4
 */
public interface Title {
	/**
	 * Set title text
	 *
	 * @param title Title
	 */
	void setTitle(String title);

	/**
	 * Get title text
	 *
	 * @return Title text
	 */
	String getTitle();

	/**
	 * Set subtitle text
	 *
	 * @param subtitle Subtitle text
	 */
	void setSubtitle(String subtitle);

	/**
	 * Get subtitle text
	 *
	 * @return Subtitle text
	 */
	String getSubtitle();

	/**
	 * Get title color
	 *
	 * @return color
	 */
	ChatColor getTitleColor();

	/**
	 * Get subtitle color
	 *
	 * @return color
	 */
	ChatColor getSubtitleColor();

	/**
	 * Get fade in time
	 *
	 * @return time
	 */
	int getFadeInTime();

	/**
	 * Get fade out time
	 *
	 * @return time
	 */
	int getFadeOutTime();

	/**
	 * Get stay time
	 *
	 * @return time
	 */
	int getStayTime();

	/**
	 * Get ticks
	 *
	 * @return ticks boolean
	 */
	boolean getTicks();

	/**
	 * Set the title color
	 *
	 * @param color Chat color
	 */
	void setTitleColor(ChatColor color);

	/**
	 * Set the subtitle color
	 *
	 * @param color Chat color
	 */
	void setSubtitleColor(ChatColor color);

	/**
	 * Set title fade in time
	 *
	 * @param time Time
	 */
	void setFadeInTime(int time);

	/**
	 * Set title fade out time
	 *
	 * @param time Time
	 */
	void setFadeOutTime(int time);

	/**
	 * Set title stay time
	 *
	 * @param time Time
	 */
	void setStayTime(int time);

	/**
	 * Set timings to ticks
	 */
	void setTimingsToTicks();

	/**
	 * Set timings to seconds
	 */
	void setTimingsToSeconds();

	/**
	 * Send the title to a player
	 *
	 * @param player Player
	 */
	void send(Player player);

	/**
	 * Broadcast the title to all players
	 */
	void broadcast();

	/**
	 * Clear the title
	 *
	 * @param player Player
	 */
	void clearTitle(Player player);

	/**
	 * Reset the title settings
	 *
	 * @param player Player
	 */
	void resetTitle(Player player);
}
