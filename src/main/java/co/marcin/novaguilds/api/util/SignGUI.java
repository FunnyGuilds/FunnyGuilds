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

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface SignGUI {
	/**
	 * The constructor
	 *
	 * @param player      the player
	 * @param defaultText default text string array
	 * @param response    listener implementation
	 */
	void open(Player player, String[] defaultText, SignGUIListener response);

	/**
	 * The constructor
	 *
	 * @param player         the player
	 * @param signGUIPattern pattern
	 * @param response       listener implementation
	 */
	void open(Player player, SignGUIPattern signGUIPattern, SignGUIListener response);

	/**
	 * Destroy the gui
	 */
	void destroy();

	/**
	 * Gets listeners
	 *
	 * @return the map
	 */
	Map<UUID, SignGUIListener> getListeners();

	/**
	 * Gets locations
	 *
	 * @return the map
	 */
	Map<UUID, Location> getSignLocations();

	interface SignGUIListener {
		/**
		 * Gets executed when a player is done editing
		 *
		 * @param player the player
		 * @param lines  new content (string array)
		 */
		void onSignDone(Player player, String[] lines);
	}

	interface SignGUIPattern {
		/**
		 * Gets the lines
		 *
		 * @return string array
		 */
		String[] get();

		/**
		 * Gets the index of input line
		 *
		 * @return index
		 */
		int getInputLine();
	}
}
