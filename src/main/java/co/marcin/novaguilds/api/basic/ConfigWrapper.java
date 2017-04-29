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

package co.marcin.novaguilds.api.basic;

import co.marcin.novaguilds.api.util.VarKeyApplicable;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ConfigWrapper extends VarKeyApplicable<ConfigWrapper>, Cloneable {
	interface Typed<T> extends ConfigWrapper {
		/**
		 * Gets a typed value
		 *
		 * @return typed value
		 */
		T get();
	}

	/**
	 * Gets the name
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Gets the path
	 *
	 * @return the path
	 */
	String getPath();

	/**
	 * Gets fix colors flag
	 *
	 * @return boolean
	 */
	boolean isFixColors();

	/**
	 * Checks if the value is empty
	 * In default implementation
	 * string equal to 'none'
	 *
	 * @return true if empty
	 */
	boolean isEmpty();

	/**
	 * Checks if the value is a list
	 *
	 * @return true if list
	 */
	boolean isList();

	/**
	 * Gets a string
	 *
	 * @return the string
	 */
	String getString();

	/**
	 * Gets string list
	 *
	 * @return the list
	 */
	List<String> getStringList();

	/**
	 * Gets ItemStack list
	 *
	 * @return the list
	 */
	List<ItemStack> getItemStackList();

	/**
	 * Gets material list
	 *
	 * @return the list
	 */
	List<Material> getMaterialList();

	/**
	 * Gets a long
	 *
	 * @return long
	 */
	long getLong();

	/**
	 * Gets a double
	 *
	 * @return double
	 */
	double getDouble();

	/**
	 * Gets an int
	 *
	 * @return int
	 */
	int getInt();

	/**
	 * Gets a boolean
	 *
	 * @return boolean
	 */
	boolean getBoolean();

	/**
	 * Gets time in seconds
	 *
	 * @return seconds
	 */
	int getSeconds();

	/**
	 * Gets an ItemStack
	 *
	 * @return itemstack
	 */
	ItemStack getItemStack();

	/**
	 * Gets a material
	 *
	 * @return material
	 */
	Material getMaterial();

	/**
	 * Gets material data (durability)
	 *
	 * @return byte
	 */
	byte getMaterialData();

	/**
	 * Gets percents
	 *
	 * @return double value (%)
	 */
	double getPercent();

	/**
	 * Gets configuration section
	 *
	 * @return the section
	 */
	ConfigurationSection getConfigurationSection();

	/**
	 * Sets a value
	 *
	 * @param obj the value
	 */
	void set(Object obj);

	/**
	 * Sets the path
	 *
	 * @param path new path
	 */
	void setPath(String path);

	/**
	 * Sets fix colors flag
	 *
	 * @param b the flag
	 */
	void setFixColors(boolean b);

	/**
	 * Converts the value to an enum
	 *
	 * @param clazz enum class
	 * @param <E>   enum class
	 * @return enum value
	 */
	<E extends Enum> E toEnum(Class<E> clazz);

	/**
	 * Clones the wrapper
	 *
	 * @return cloned wrapper
	 */
	ConfigWrapper clone();
}
