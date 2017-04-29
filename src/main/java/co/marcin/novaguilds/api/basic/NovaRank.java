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

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.enums.GuildPermission;

import java.util.List;

public interface NovaRank extends Resource {
	/**
	 * Gets the ID
	 *
	 * @return the ID
	 */
	int getId();

	/**
	 * Gets the name
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Gets a list of members
	 *
	 * @return the list of members
	 */
	List<NovaPlayer> getMembers();

	/**
	 * Gets a list of permissions
	 *
	 * @return the list of permissions
	 */
	List<GuildPermission> getPermissions();

	/**
	 * Gets the guild
	 *
	 * @return the guild
	 */
	NovaGuild getGuild();

	/**
	 * Returns if the rank has been cloned
	 *
	 * @return true/false
	 */
	boolean isClone();

	/**
	 * Returns if the rank is a default rank
	 *
	 * @return true/false
	 */
	boolean isDefault();

	/**
	 * Sets the ID
	 *
	 * @param id the ID
	 */
	void setId(int id);

	/**
	 * Sets the name
	 *
	 * @param name the name
	 */
	void setName(String name);

	/**
	 * Sets the list of permissions
	 *
	 * @param permissions the list of permissions
	 */
	void setPermissions(List<GuildPermission> permissions);

	/**
	 * Sets the guild
	 *
	 * @param guild the guild
	 */
	void setGuild(NovaGuild guild);

	/**
	 * Sets 'clone' flag
	 *
	 * @param clone true/false
	 */
	void setClone(boolean clone);

	/**
	 * Sets 'default' flag
	 *
	 * @param def true/false
	 */
	void setDefault(boolean def);

	/**
	 * Adds a permission
	 *
	 * @param permission the permission
	 */
	void addPermission(GuildPermission permission);

	/**
	 * Adds permissions
	 *
	 * @param list list of permissions
	 */
	void addPermissions(List<GuildPermission> list);

	/**
	 * Adds a member
	 *
	 * @param nPlayer the player
	 */
	void addMember(NovaPlayer nPlayer);

	/**
	 * Removes a permission
	 *
	 * @param permission the permission
	 */
	void removePermission(GuildPermission permission);

	/**
	 * Removes permissions
	 *
	 * @param list list of permissions
	 */
	void removePermission(List<GuildPermission> list);

	/**
	 * Removes a member
	 *
	 * @param nPlayer the player
	 */
	void removeMember(NovaPlayer nPlayer);

	/**
	 * Returns if the rank contains permission
	 *
	 * @param permission the permission
	 * @return true/false
	 */
	boolean hasPermission(GuildPermission permission);

	/**
	 * Returns if the rank is generic
	 * (loaded from config)
	 *
	 * @return true/false
	 */
	boolean isGeneric();

	/**
	 * Deletes the rank
	 */
	void delete();
}
