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

package co.marcin.novaguilds.api.basic;

import java.util.List;

public interface NovaRaid {
	enum Result {
		DURING,
		TIMEOUT,
		SUCCESS,
		DESTROYED
	}

	/**
	 * Gets the attacking guild
	 *
	 * @return the guild
	 */
	NovaGuild getGuildAttacker();

	/**
	 * Gets the defender guild
	 *
	 * @return the guild
	 */
	NovaGuild getGuildDefender();

	/**
	 * Gets the time when the raid started (unixtime)
	 *
	 * @return unixtime
	 */
	long getStartTime();

	/**
	 * Gets the amount of kills by attackers
	 *
	 * @return the amount
	 */
	int getKillsAttacker();

	/**
	 * Gets the amount of kills by defenders
	 *
	 * @return the amount
	 */
	int getKillsDefender();

	/**
	 * Gets the progress
	 *
	 * @return the amount
	 */
	float getProgress();

	/**
	 * Gets the list of attackers on defenders region
	 *
	 * @return the list
	 */
	List<NovaPlayer> getPlayersOccupying();

	/**
	 * Gets raid result
	 *
	 * @return result
	 */
	Result getResult();

	/**
	 * Gets the unixtime of last activity on defenders region
	 *
	 * @return unixtime
	 */
	long getInactiveTime();

	/**
	 * Sets the attacker guild
	 *
	 * @param guild the guild
	 */
	void setGuildAttacker(NovaGuild guild);

	/**
	 * Sets the defender guild
	 *
	 * @param guild the guild
	 */
	void setGuildDefender(NovaGuild guild);

	/**
	 * Adds a kill to attackers count
	 */
	void addKillAttacker();

	/**
	 * Adds a kill to defenders count
	 */
	void addKillDefender();

	/**
	 * Resets the progress
	 */
	void resetProgress();

	/**
	 * Returns whether the progress is 100%
	 *
	 * @return boolean
	 */
	boolean isProgressFinished();

	/**
	 * Adds some progress
	 *
	 * @param progress progress
	 */
	void addProgress(float progress);

	/**
	 * Sets the result
	 *
	 * @param result boolean
	 */
	void setResult(Result result);

	/**
	 * Updates inactive time
	 */
	void updateInactiveTime();

	/**
	 * Adds a player to occupying list
	 *
	 * @param nPlayer the player
	 */
	void addPlayerOccupying(NovaPlayer nPlayer);

	/**
	 * removes a player from occupying list
	 *
	 * @param nPlayer the player
	 */
	void removePlayerOccupying(NovaPlayer nPlayer);
}
