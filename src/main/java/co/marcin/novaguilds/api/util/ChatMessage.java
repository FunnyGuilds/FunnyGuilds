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
import org.bukkit.entity.Player;

import java.util.List;

public interface ChatMessage {
	/**
	 * Send a message to all players online
	 */
	void send();

	/**
	 * Sends a message to a specific player
	 *
	 * @param player the player
	 */
	void send(Player player);

	/**
	 * Sends a message to a specific player
	 *
	 * @param nPlayer the player
	 */
	void send(NovaPlayer nPlayer);

	/**
	 * Sends a message to all players of a guild
	 *
	 * @param guild the guild
	 */
	void send(NovaGuild guild);

	/**
	 * Sends a message to all players in a list of guilds
	 *
	 * @param guildList the list of NovaGuild
	 */
	void sendToGuilds(List<NovaGuild> guildList);

	/**
	 * Sends a message to all players in a list
	 *
	 * @param playerList the list of Player
	 */
	void sendToPlayers(List<Player> playerList);

	/**
	 * Sends a message to all players in a list
	 *
	 * @param playerList the list of NovaPlayer
	 */
	void sendToNovaPlayers(List<NovaPlayer> playerList);

	/**
	 * Gets the player
	 *
	 * @return the player
	 */
	Player getPlayer();

	/**
	 * Gets the message
	 *
	 * @return the message
	 */
	String getMessage();

	/**
	 * Gets the format
	 *
	 * @return the format string
	 */
	String getFormat();

	/**
	 * Gets the tag
	 *
	 * @return PreparedTag instance
	 */
	PreparedTag getTag();

	/**
	 * Returns the ReportToConsole flag
	 *
	 * @return boolean
	 */
	boolean isReportToConsole();

	/**
	 * Sets the message
	 *
	 * @param message the message
	 */
	void setMessage(String message);

	/**
	 * Sets the format
	 *
	 * @param format the format string
	 */
	void setFormat(String format);

	/**
	 * Sets the tag
	 *
	 * @param tag PreparedTag instance
	 */
	void setTag(PreparedTag tag);

	/**
	 * Sets the ReportToConsole flag
	 *
	 * @param reportToConsole boolean
	 */
	void setReportToConsole(boolean reportToConsole);

	/**
	 * Reports the message to the console
	 */
	void report();
}
