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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaHologram;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.basic.NovaHologramImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HologramManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final File file = new File(plugin.getDataFolder(), "holograms.yml");
	private YamlConfiguration configuration;
	private final List<NovaHologram> holograms = new ArrayList<>();

	/**
	 * Loads holograms
	 */
	public void load() {
		try {
			if(!file.exists() && !file.createNewFile()) {
				throw new IOException("Failed creating new file");
			}

			configuration = YamlConfiguration.loadConfiguration(file);
			int count = 0;

			for(String name : configuration.getKeys(false)) {
				NovaHologram nHologram = new NovaHologramImpl();
				Location location = RegionUtils.sectionToLocation(configuration.getConfigurationSection(name + ".location"));

				nHologram.setName(name);
				nHologram.setLocation(location);
				nHologram.setTop(configuration.getBoolean(name + ".top"));

				final List<String> lines = new ArrayList<>();
				if(nHologram.isTop()) {
					lines.add(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
					lines.addAll(plugin.getGuildManager().getTopGuilds());
				}
				else {
					lines.addAll(configuration.getStringList(name + ".lines"));
				}

				nHologram.addLine(lines);

				nHologram.create();

				holograms.add(nHologram);
				count++;
			}

			LoggerUtils.info("Finished loading holograms. (" + count + " loaded)");
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Saves the holograms
	 */
	public void save() {
		if(configuration != null) {
			for(NovaHologram hologram : holograms) {
				if(hologram.isDeleted()) {
					configuration.set(hologram.getName(), null);
				}
				else {
					if(!hologram.isTop()) {
						configuration.set(hologram.getName() + ".lines", hologram.getLines());
					}

					configuration.set(hologram.getName() + ".top", hologram.isTop());
					configuration.set(hologram.getName() + ".location.world", hologram.getLocation().getWorld().getUID().toString());
					configuration.set(hologram.getName() + ".location.x", hologram.getLocation().getX());
					configuration.set(hologram.getName() + ".location.y", hologram.getLocation().getY());
					configuration.set(hologram.getName() + ".location.z", hologram.getLocation().getZ());
				}
			}

			try {
				configuration.save(file);
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Failed saving holograms, they weren't even loaded!");
		}

		LoggerUtils.info("Finished saving holograms.");
	}

	/**
	 * Adds top hologram
	 * with a random name
	 * topX(0-999)
	 *
	 * @param location target location
	 * @return the hologram
	 */
	public NovaHologram addTopHologram(Location location) {
		NovaHologram hologram = new NovaHologramImpl();
		hologram.setLocation(location);
		hologram.addLine(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
		hologram.addLine(plugin.getGuildManager().getTopGuilds());
		hologram.setName("topX" + NumberUtils.randInt(1, 999));
		hologram.create();
		hologram.setTop(true);

		holograms.add(hologram);

		return hologram;
	}

	/**
	 * Refreshes holograms
	 */
	public void refreshTopHolograms() {
		for(NovaHologram hologram : holograms) {
			if(hologram.isTop()) {
				hologram.refresh();
			}
		}
	}

	/**
	 * Gets all the holograms
	 *
	 * @return list of holograms
	 */
	public List<NovaHologram> getHolograms() {
		return holograms;
	}

	/**
	 * Gets a hologram by its name
	 *
	 * @param name hologram name
	 * @return the hologram
	 */
	public NovaHologram getHologram(String name) {
		for(NovaHologram hologram : holograms) {
			if(hologram.getName().equalsIgnoreCase(name)) {
				return hologram;
			}
		}

		return null;
	}
}
