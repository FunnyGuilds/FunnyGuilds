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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Thanks to these guys for publishing so great
 * database classes that I've been using for 1 year!
 * I added the code in the 2'nd commit and will
 * remove it 984 commits later.
 *
 * @author -_Husky_-
 * @author tips48
 */
public interface Database {
	/**
	 * Opens a connection with the database
	 *
	 * @throws SQLException           if the connection can not be opened
	 * @throws ClassNotFoundException if the driver cannot be found
	 */
	void openConnection() throws SQLException, ClassNotFoundException;

	/**
	 * Checks if a connection is open with the database
	 *
	 * @return true if the connection is open
	 * @throws SQLException if the connection cannot be checked
	 */
	boolean checkConnection() throws SQLException;

	/**
	 * Gets the connection with the database
	 *
	 * @return Connection with the database, null if none
	 */
	Connection getConnection();

	/**
	 * Closes the connection with the database
	 *
	 * @return true if successful
	 * @throws SQLException if the connection cannot be closed
	 */
	boolean closeConnection() throws SQLException;
}
