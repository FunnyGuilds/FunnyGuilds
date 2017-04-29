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

import java.sql.SQLException;
import java.util.List;

public interface DatabaseAnalyzer {
	enum ModificationType {
		/**
		 * Adds a column at the end of a table
		 */
		ADD,

		/**
		 * Adds a column in the middle of a table
		 */
		ADD_INSIDE,

		/**
		 * Removes a column
		 */
		REMOVE,

		/**
		 * Moves a column and pushes all columns after
		 */
		MOVE,

		/**
		 * Renames a column
		 */
		RENAME,

		/**
		 * Changes column type
		 */
		CHANGETYPE
	}

	/**
	 * Analyzes a table
	 *
	 * @param table table name
	 * @param sql   target table create code
	 * @throws SQLException when anything bad happens
	 */
	void analyze(String table, String sql) throws SQLException;

	/**
	 * Updates the database
	 *
	 * @throws SQLException when anything bad happens
	 */
	void update() throws SQLException;

	/**
	 * Gets missmatches
	 *
	 * @return missmatch list
	 */
	List<Missmatch> getMissmatches();

	interface Missmatch {
		/**
		 * Gets column index
		 *
		 * @return the index
		 */
		int getIndex();

		/**
		 * Gets modification type
		 *
		 * @return modification type
		 */
		ModificationType getModificationType();

		/**
		 * Gets column name
		 *
		 * @return column name
		 */
		String getColumnName();

		/**
		 * Gets column to be put in 'AFTER' query
		 * For ADD_INSIDE
		 *
		 * @return name
		 */
		String getPreviousColumn();

		/**
		 * Gets column type
		 *
		 * @return column type
		 */
		String getColumnType();

		/**
		 * Gets table name
		 *
		 * @return table name
		 */
		String getTableName();

		/**
		 * Gets constraints
		 *
		 * @return constraints string
		 */
		String getConstraints();
	}
}
