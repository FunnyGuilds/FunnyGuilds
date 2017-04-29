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

package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.api.util.DatabaseAnalyzer;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAnalyzerImpl implements DatabaseAnalyzer {
	private final Connection connection;
	private final Map<String, String> sqlStructure = new HashMap<>();
	private final Map<Integer, String> sqlNames = new HashMap<>();
	private final Map<String, String> tableStructure = new HashMap<>();
	private final Map<Integer, String> tableNames = new HashMap<>();
	private final List<Missmatch> missmatches = new ArrayList<>();
	private final Map<String, String> sqlConstraints = new HashMap<>();

	/**
	 * The constructor
	 *
	 * @param connection connection
	 */
	public DatabaseAnalyzerImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void analyze(String table, String sql) throws SQLException {
		if(!existsTable(table)) {
			addTable(sql);
		}

		getSqlStructure(sql);
		getTableStructure(table);

		//ADD_INSIDE
		if(tableStructure.size() < sqlNames.size()) {
			int shift = 0;

			for(int index = 0; index + shift < sqlNames.size(); index++) {
				String n1 = tableNames.get(index);
				String n2 = sqlNames.get(index + shift);

				if(n2.equalsIgnoreCase(n1)) {
					continue;
				}

				String after = tableNames.get(index - 1);
				shift++;
				Missmatch missmatch = new MissmatchImpl(ModificationType.ADD_INSIDE, table, index, n2, sqlStructure.get(n2), after, sqlConstraints.get(n2));
				missmatches.add(missmatch);
				LoggerUtils.info(" ADD_INSIDE: " + n2 + " (" + sqlStructure.get(n2) + ") after " + after, false);
			}
		}

		for(String columnName : tableNames.values()) {
			String typeTable = tableStructure.get(columnName);
			String typeSQL = sqlStructure.get(columnName);

			if(typeSQL.contains("(")) {
				StringBuilder buf = new StringBuilder(typeSQL);
				int start = typeSQL.indexOf("(");
				int end = typeSQL.indexOf(")") + 1;
				buf.replace(start, end, "");
				typeSQL = buf.toString();
			}

			if(typeSQL.equalsIgnoreCase(typeTable)) {
				continue;
			}

			Missmatch missmatch = new MissmatchImpl(ModificationType.CHANGETYPE, table, 0, columnName, sqlStructure.get(columnName), sqlConstraints.get(columnName));
			missmatches.add(missmatch);
			LoggerUtils.info(" CHANGETYPE: " + columnName + ": " + typeTable + " -> " + typeSQL, false);
		}
	}

	@Override
	public void update() throws SQLException {
		sort();

		for(Missmatch missmatch : missmatches) {
			switch(missmatch.getModificationType()) {
				case ADD_INSIDE:
					addColumn(missmatch);
					break;
				case CHANGETYPE:
					changeType(missmatch);
					break;
			}
		}
	}

	@Override
	public List<Missmatch> getMissmatches() {
		return missmatches;
	}

	/**
	 * Adds a column
	 * (also inside a table)
	 *
	 * @param missmatch missmatch instance
	 * @throws SQLException when something goes wrong
	 */
	private void addColumn(Missmatch missmatch) throws SQLException {
		String sql = "ALTER TABLE `" + missmatch.getTableName() + "` ADD COLUMN `" + missmatch.getColumnName() + "` " + missmatch.getColumnType() + " " + missmatch.getConstraints() + " AFTER `" + missmatch.getPreviousColumn() + "`;";
		Statement statement = connection.createStatement();
		statement.execute(sql);
		LoggerUtils.info("Added new column " + missmatch.getColumnName() + " after " + missmatch.getPreviousColumn() + " to table " + missmatch.getTableName());
	}

	/**
	 * Changes table type
	 *
	 * @param missmatch missmatch instance
	 * @throws SQLException when something goes wrong
	 */
	private void changeType(Missmatch missmatch) throws SQLException {
		String sql = "ALTER TABLE `" + missmatch.getTableName() + "` MODIFY `" + missmatch.getColumnName() + "` " + missmatch.getColumnType() + ";";
		Statement statement = connection.createStatement();
		statement.execute(sql);
		LoggerUtils.info("Changed column " + missmatch.getColumnName() + " type to " + missmatch.getColumnType());
	}

	/**
	 * Adds a table
	 *
	 * @param sql table create SQL
	 * @throws SQLException when something goes wrong
	 */
	private void addTable(String sql) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute(sql);
		LoggerUtils.info("Added new table");
	}

	/**
	 * Sorts missmatches by index
	 */
	private void sort() {
		Collections.sort(missmatches, new Comparator<Missmatch>() {
			public int compare(Missmatch o1, Missmatch o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});
	}

	/**
	 * Gets table structure from
	 * table create code
	 * Fills tableNames with index and name
	 * Fills tableStructure with name and type
	 *
	 * @param sql table create SQL
	 */
	private void getSqlStructure(String sql) {
		sqlNames.clear();
		sqlStructure.clear();
		int i = 0;

		for(String c : StringUtils.split(sql, ",\r\n")) {
			if(c.startsWith("  `")) {
				String[] split = StringUtils.split(c, ' ');
				String name = StringUtils.replace(split[0], "`", "");
				String type = split[1];
				String constraints = StringUtils.replace(c, "  " + split[0] + " " + split[1], "");

				if(split[2].equalsIgnoreCase("unsigned")) {
					type += " " + split[2];
				}

				sqlStructure.put(name, type);
				sqlNames.put(i, name);
				sqlConstraints.put(name, constraints);
				i++;
			}
		}
	}

	/**
	 * Gets table structure from the database
	 * Fills tableNames with index and name
	 * Fills tableStructure with name and type
	 *
	 * @param table table name
	 * @throws SQLException when something goes wrong
	 */
	private void getTableStructure(String table) throws SQLException {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet columns = databaseMetaData.getColumns(null, null, table, null);
		tableNames.clear();
		tableStructure.clear();
		int i = 0;

		while(columns.next()) {
			String columnName = columns.getString("COLUMN_NAME");
			String columnType = columns.getString("TYPE_NAME");

			tableNames.put(i, columnName);
			tableStructure.put(columnName, columnType);
			i++;
		}

		columns.close();
	}

	/**
	 * Checks if a table exists
	 *
	 * @param table table name
	 * @return true if the table exists
	 * @throws SQLException when something goes wrong
	 */
	private boolean existsTable(String table) throws SQLException {
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);

		while(rs.next()) {
			if(rs.getString(3).equalsIgnoreCase(table)) {
				return true;
			}
		}

		return false;
	}

	public class MissmatchImpl implements DatabaseAnalyzer.Missmatch {
		private final int index;
		private final String table;
		private final String columnName;
		private final String columnType;
		private final String previousColumn;
		private final String constraints;
		private final ModificationType modificationType;

		/**
		 * The constructor
		 *
		 * @param modificationType modification type enum
		 * @param table            table name
		 * @param index            index
		 * @param columnName       column name
		 * @param columnType       column type
		 * @param previousColumn   previous column (AFTER)
		 * @param constraints      constraints
		 */
		public MissmatchImpl(ModificationType modificationType, String table, int index, String columnName, String columnType, String previousColumn, String constraints) {
			this.modificationType = modificationType;
			this.table = table;
			this.index = index;
			this.columnName = columnName;
			this.columnType = columnType;
			this.previousColumn = previousColumn;
			this.constraints = constraints;
		}

		/**
		 * The constructor
		 *
		 * @param modificationType modification type enum
		 * @param table            table name
		 * @param index            index
		 * @param columnName       column name
		 * @param columnType       column type
		 * @param constraints      constraints
		 */
		public MissmatchImpl(ModificationType modificationType, String table, int index, String columnName, String columnType, String constraints) {
			this(modificationType, table, index, columnName, columnType, "", constraints);
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public ModificationType getModificationType() {
			return modificationType;
		}

		@Override
		public String getColumnName() {
			return columnName;
		}

		@Override
		public String getColumnType() {
			return columnType;
		}

		@Override
		public String getPreviousColumn() {
			return previousColumn;
		}

		@Override
		public String getTableName() {
			return table;
		}

		@Override
		public String getConstraints() {
			return constraints;
		}
	}
}
