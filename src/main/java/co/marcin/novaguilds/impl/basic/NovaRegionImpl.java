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
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class NovaRegionImpl extends AbstractResource implements NovaRegion {
	private final Location[] corners = new Location[2];
	private int id;
	private World world;
	private NovaGuild guild;
	private Integer index;

	/**
	 * The constructor
	 *
	 * @param uuid region UUID
	 */
	public NovaRegionImpl(UUID uuid) {
		super(uuid);
	}

	/**
	 * Create from RegionSelection
	 *
	 * @param uuid      region UUID
	 * @param selection the selection
	 */
	public NovaRegionImpl(UUID uuid, RegionSelection selection) {
		this(uuid);
		setCorner(0, selection.getCorner(0));
		setCorner(1, selection.getCorner(1));
		setWorld(selection.getCorner(0).getWorld());
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public int getId() {
		if(id <= 0) {
			throw new UnsupportedOperationException("This rank might have been loaded from FLAT and has 0 (or negative) ID");
		}

		return id;
	}

	@Override
	public NovaGuild getGuild() {
		return guild;
	}

	@Override
	public Location getCorner(int index) {
		if(index != 0 && index != 1) {
			throw new IllegalArgumentException("Index can be either 0 or 1");
		}

		return corners[index];
	}

	@Override
	public int getWidth() {
		return Math.abs(getCorner(0).getBlockX() - getCorner(1).getBlockX()) + 1;
	}

	@Override
	public int getLength() {
		return Math.abs(getCorner(0).getBlockZ() - getCorner(1).getBlockZ()) + 1;
	}

	@Override
	public int getDiagonal() {
		int sumSquare = (int) (Math.pow(getWidth(), 2) + Math.pow(getLength(), 2));
		return Math.round((int) Math.sqrt(sumSquare));
	}

	@Override
	public int getSurface() {
		return getLength() * getWidth();
	}

	@Override
	public Location getCenter() {
		return RegionUtils.getCenterLocation(getCorner(0), getCorner(1));
	}

	@Override
	public Integer getIndex() {
		return index;
	}

	@Override
	public void setWorld(World world) {
		this.world = world;
		setChanged();
	}

	@Override
	public void setId(int id) {
		this.id = id;
		setChanged();
	}

	@Override
	public void setGuild(NovaGuild guild) {
		this.guild = guild;
		setChanged();
	}

	@Override
	public void setCorner(int index, Location location) {
		if(index != 0 && index != 1) {
			throw new IllegalArgumentException("Index can be either 0 or 1");
		}

		Location corner = null;
		if(location != null) {
			corner = location.clone();
			corner.setY(0);
		}

		corners[index] = corner;
		setChanged();
	}

	@Override
	public void setIndex(Integer index) {
		this.index = index;
	}
}
