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

import co.marcin.novaguilds.api.storage.Resource;

public interface CommandExecutorHandler extends Runnable {
	enum State {
		WAITING,
		CANCELED,
		CONFIRMED
	}

	/**
	 * Executes the command
	 */
	void execute();

	/**
	 * Cancels the command
	 */
	void cancel();

	/**
	 * Sets command status as confirmed
	 * and executes it
	 */
	void confirm();

	/**
	 * Gets the command
	 *
	 * @return the command enum
	 */
	CommandWrapper getCommand();

	/**
	 * Gets execution status
	 *
	 * @return get the state
	 */
	State getState();

	/**
	 * Gets executor variable
	 *
	 * @return the object
	 */
	Resource getExecutorVariable();

	/**
	 * Sets executor variable
	 *
	 * @param executorVariable the object
	 */
	void executorVariable(Resource executorVariable);
}
