/*
 *     FunnyGuilds - Bukkit plugin
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

public class StorageConnectionFailedException extends Exception {
	/**
	 * The constructor
	 */
	public StorageConnectionFailedException() {

	}

	/**
	 * The constructor
	 *
	 * @param message exception message
	 */
	public StorageConnectionFailedException(String message) {
		super(message);
	}

	/**
	 * The constructor
	 *
	 * @param message exception message
	 * @param cause   cause
	 */
	public StorageConnectionFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
