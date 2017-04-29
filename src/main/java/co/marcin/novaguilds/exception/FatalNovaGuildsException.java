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

package co.marcin.novaguilds.exception;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.manager.ListenerManager;

public class FatalNovaGuildsException extends Exception {
	public static boolean fatal;

	/**
	 * The constructor
	 */
	public FatalNovaGuildsException() {
		disable();
	}

	/**
	 * The constructor
	 *
	 * @param message exception message
	 */
	public FatalNovaGuildsException(String message) {
		super(message);
		disable();
	}

	/**
	 * The constructor
	 *
	 * @param message exception message
	 * @param cause   cause
	 */
	public FatalNovaGuildsException(String message, Throwable cause) {
		super(message, cause);
		disable();
	}

	/**
	 * Disables NovaGuilds
	 */
	private void disable() {
		fatal = true;

		if(NovaGuilds.getInstance().isEnabled()) {
			NovaGuilds.runTask(new Runnable() {
				@Override
				public void run() {
					ListenerManager.getLoggedPluginManager().disablePlugin(NovaGuilds.getInstance());
				}
			});
		}
	}
}
