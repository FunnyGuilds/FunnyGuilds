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

package co.marcin.novaguilds.api.util.reflect;

import java.lang.reflect.Field;

public interface FieldAccessor<T> {
	/**
	 * Gets the field object
	 *
	 * @return field object
	 */
	Field getField();

	/**
	 * Gets a field
	 *
	 * @param target target object
	 * @return field value
	 */
	T get(Object target);

	/**
	 * Gets a value of a static field
	 *
	 * @return the value
	 */
	T get();

	/**
	 * Gets field name
	 *
	 * @return field name
	 */
	String getName();

	/**
	 * Sets a value to a field
	 *
	 * @param target target object
	 * @param value  value
	 */
	void set(Object target, T value);

	/**
	 * Sets a value to a static field
	 *
	 * @param value value
	 */
	void set(T value);

	/**
	 * Checks if object has specified field
	 *
	 * @param target target object
	 * @return boolean
	 */
	boolean hasField(Object target);

	/**
	 * Sets the field as not final
	 */
	void setNotFinal();
}
