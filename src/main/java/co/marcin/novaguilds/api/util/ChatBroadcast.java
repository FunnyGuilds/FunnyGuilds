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

import co.marcin.novaguilds.api.basic.MessageWrapper;

public interface ChatBroadcast {
	/**
	 * Broadcasts the message
	 */
	void send();

	/**
	 * Sets the tag
	 *
	 * @param index       index
	 * @param preparedTag tag instance
	 */
	void setTag(Integer index, PreparedTag preparedTag);

	/**
	 * Gets a tag
	 *
	 * @param index index
	 * @return the tag
	 */
	PreparedTag getTag(Integer index);

	/**
	 * Gets the message
	 *
	 * @return the wrapper
	 */
	MessageWrapper getMessage();
}
