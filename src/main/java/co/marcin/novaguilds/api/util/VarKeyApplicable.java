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

import co.marcin.novaguilds.enums.VarKey;

import java.util.Map;

public interface VarKeyApplicable<T> extends Cloneable {
	/**
	 * Gets the map of variables
	 *
	 * @return The Map
	 */
	Map<VarKey, String> getVars();

	/**
	 * Sets the vars
	 *
	 * @param vars Map of variables
	 * @return Message instance
	 */
	T vars(Map<VarKey, String> vars);

	/**
	 * Set a var
	 *
	 * @param varKey key enum
	 * @param value  the value
	 * @return message instance
	 */
	T setVar(VarKey varKey, String value);

	/**
	 * Set a var
	 *
	 * @param varKey key enum
	 * @param value  the value
	 * @return message instance
	 */
	T setVar(VarKey varKey, Integer value);

	/**
	 * Set a var
	 *
	 * @param varKey key enum
	 * @param value  the value
	 * @return message instance
	 */
	T setVar(VarKey varKey, Double value);

	/**
	 * Checks if vars have been changed
	 *
	 * @return true if
	 */
	boolean isChanged();
}
