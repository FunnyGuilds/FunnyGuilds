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

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.impl.util.AbstractChangeable;

import java.util.UUID;

public abstract class AbstractResource extends AbstractChangeable implements Resource {
	private boolean added;
	private boolean unloaded;
	protected final UUID uuid;

	/**
	 * The constructor
	 *
	 * @param uuid the uuid
	 */
	public AbstractResource(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public final UUID getUUID() {
		return uuid;
	}

	@Override
	public final boolean isAdded() {
		return added;
	}

	@Override
	public final void setAdded() {
		added = true;
	}

	@Override
	public final void setNotAdded() {
		added = false;
	}

	@Override
	public boolean isUnloaded() {
		return unloaded;
	}

	@Override
	public void unload() {
		unloaded = true;
	}
}
