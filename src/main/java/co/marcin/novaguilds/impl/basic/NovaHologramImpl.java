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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaHologram;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.ItemStackUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NovaHologramImpl extends AbstractResource implements NovaHologram {
	private String name;
	private Location location;
	private final List<String> lines = new ArrayList<>();
	private Hologram hologram;
	private boolean isTop = false;
	private boolean deleted = false;

	/**
	 * The constructor
	 */
	public NovaHologramImpl() {
		super(null);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public List<String> getLines() {
		return lines;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public void addLine(String line) {
		lines.add(line);
	}

	@Override
	public void clearLines() {
		lines.clear();
	}

	@Override
	public void addLine(List<String> lines) {
		this.lines.addAll(lines);
	}

	@Override
	public void refresh() {
		hologram.clearLines();

		if(isTop()) {
			clearLines();
			addLine(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
			addLine(NovaGuilds.getInstance().getGuildManager().getTopGuilds());
		}

		for(String line : lines) {
			if(line.startsWith("[ITEM]")) {
				String ItemStackLine = line.substring(6);
				ItemStack is = ItemStackUtils.stringToItemStack(ItemStackLine);

				if(is != null) {
					hologram.appendItemLine(is);
				}
			}
			else {
				hologram.appendTextLine(line);
			}
		}
	}

	@Override
	public void teleport(Location location) {
		hologram.teleport(location);
		setLocation(location);
	}

	@Override
	public void create() {
		hologram = HologramsAPI.createHologram(NovaGuilds.getInstance(), location);
		refresh();
	}

	@Override
	public void delete() {
		hologram.delete();
		NovaGuilds.getInstance().getHologramManager().getHolograms().remove(this);
		location.getWorld().playEffect(location, Effect.POTION_SWIRL, 1000);
		deleted = true;
	}

	@Override
	public boolean isTop() {
		return isTop;
	}

	@Override
	public boolean isDeleted() {
		return deleted || hologram.isDeleted();
	}

	@Override
	public void setTop(boolean top) {
		this.isTop = top;
	}
}
