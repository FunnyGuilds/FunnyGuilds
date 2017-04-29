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

package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.api.basic.NovaGuild;

public class GenericRankImpl extends NovaRankImpl {
	/**
	 * The constructor
	 *
	 * @param name rank name
	 */
	public GenericRankImpl(String name) {
		super(name);
	}

	@Override
	public void setClone(boolean clone) {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}

	@Override
	public void delete() {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}

	@Override
	public void setName(String name) {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}

	@Override
	public void setId(int id) {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}

	@Override
	public boolean isClone() {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}

	@Override
	public void setGuild(NovaGuild guild) {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}

	@Override
	public int getId() {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}

	@Override
	public NovaGuild getGuild() {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}

	@Override
	public void setDefault(boolean def) {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}

	@Override
	public boolean isDefault() {
		throw new IllegalArgumentException("Not allowed for generic ranks");
	}
}
