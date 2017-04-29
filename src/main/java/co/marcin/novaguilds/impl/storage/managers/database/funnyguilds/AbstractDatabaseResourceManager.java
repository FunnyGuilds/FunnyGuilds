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

package co.marcin.novaguilds.impl.storage.managers.database.funnyguilds;

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.Storage;

public abstract class AbstractDatabaseResourceManager<T extends Resource> extends co.marcin.novaguilds.impl.storage.managers.database.AbstractDatabaseResourceManager<T> {
	/**
	 * The constructor
	 *
	 * @param storage    the storage
	 * @param clazz      type class
	 * @param tableName  table name in the database
	 */
	protected AbstractDatabaseResourceManager(Storage storage, Class<T> clazz, String tableName) {
		super(storage, clazz, tableName);
	}

	@Override
	protected void updateUUID(T resource, int id) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	protected final void updateUUID(T resource) {
		throw new IllegalArgumentException("Not supported");
	}
}
