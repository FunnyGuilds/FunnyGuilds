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

package co.marcin.novaguilds.enums;

public enum EntityUseAction {
	ATTACK(0),
	INTERACT(1),
	INTERACT_AT(2);

	private final int id;

	/**
	 * The constructor
	 *
	 * @param id action id
	 */
	EntityUseAction(int id) {
		this.id = id;
	}

	/**
	 * Gets the ID
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}
}
