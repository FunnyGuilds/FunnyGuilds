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

package co.marcin.novaguilds.impl.storage.managers.database;

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.storage.AbstractDatabaseStorage;
import co.marcin.novaguilds.impl.storage.managers.AbstractResourceManager;
import co.marcin.novaguilds.util.LoggerUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDatabaseResourceManager<T extends Resource> extends AbstractResourceManager<T> {
	protected final String tableName;
	private final Collection<T> updateUUIDQueue = new HashSet<>();

	/**
	 * The constructor
	 *
	 * @param storage    the storage
	 * @param clazz      type class
	 * @param tableName  table name in the database
	 */
	protected AbstractDatabaseResourceManager(Storage storage, Class<T> clazz, String tableName) {
		super(storage, clazz);
		this.tableName = tableName;
	}

	@Override
	protected final AbstractDatabaseStorage getStorage() {
		return (AbstractDatabaseStorage) super.getStorage();
	}

	@Override
	public int executeSave() {
		long startTime = System.nanoTime();
		int count = executeUpdateUUID();
		LoggerUtils.info("UUIDs updated in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " resources)");

		return super.executeSave();
	}

	/**
	 * Gets column name
	 * as it is in the database
	 *
	 * @return column name
	 */
	public final String getTableName() {
		return tableName;
	}

	/**
	 * Updates resource's UUID in the database
	 *
	 * @param resource resource instance
	 * @param id       resource's ID
	 */
	protected void updateUUID(T resource, int id) {
		try {
			String sql = "UPDATE `" + Config.MYSQL_PREFIX.getString() + getTableName() + "` SET `uuid`=? WHERE `id`=?";
			PreparedStatement statement = getStorage().getConnection().prepareStatement(sql);
			statement.setString(1, resource.getUUID().toString()); //UUID
			statement.setInt(   2, id);                            //id
			statement.executeUpdate();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Updates resource's UUID in the database
	 * It's supposed to execute updateUUID(T, int)
	 *
	 * @param resource resource instance
	 */
	protected abstract void updateUUID(T resource);

	/**
	 * Adds an object to update UUID queue
	 *
	 * @param t instance
	 */
	public void addToUpdateUUIDQueue(T t) {
		if(!isInUpdateUUIDQueue(t)) {
			updateUUIDQueue.add(t);
		}
	}

	/**
	 * Checks if an object is in save queue
	 *
	 * @param t instance
	 * @return boolean
	 */
	public boolean isInUpdateUUIDQueue(T t) {
		return updateUUIDQueue.contains(t);
	}

	/**
	 * Executes UUID update
	 *
	 * @return changed rows
	 */
	public int executeUpdateUUID() {
		int count = 0;

		for(T t : updateUUIDQueue) {
			updateUUID(t);
			count++;
		}

		updateUUIDQueue.clear();

		return count;
	}
}
