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

package co.marcin.novaguilds.api.storage;

public interface Migrant {
	/**
	 * Gets the storage data is exported from
	 *
	 * @return storage instance
	 */
	Storage getFromStorage();

	/**
	 * Gets the storage data is imported to
	 *
	 * @return storage instance
	 */
	Storage getToStorage();

	/**
	 * Migrates data between storages
	 */
	void migrate();

	/**
	 * Executes the save of new data
	 */
	void save();
}
