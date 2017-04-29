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

package co.marcin.novaguilds.impl.storage.managers.file.yaml;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerRegionImpl extends AbstractYAMLResourceManager<NovaRegion> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerRegionImpl(Storage storage) {
		super(storage, NovaRegion.class, "region/");
	}

	@Override
	public List<NovaRegion> load() {
		final List<NovaRegion> list = new ArrayList<>();

		for(File regionFile : getFiles()) {
			FileConfiguration configuration = loadConfiguration(regionFile);
			String guildName = trimExtension(regionFile);
			NovaGuild guild;
			UUID regionUUID;
			boolean forceSave = false;

			if(configuration == null || configuration.getKeys(true).isEmpty()) {
				LoggerUtils.error("Null or empty configuration for region " + trimExtension(regionFile));
				continue;
			}

			World world;
			try {
				world = plugin.getServer().getWorld(UUID.fromString(configuration.getString("world")));
			}
			catch(IllegalArgumentException e) {
				world = plugin.getServer().getWorld(configuration.getString("world"));
			}

			if(world == null) {
				LoggerUtils.error("Null world for region " + trimExtension(regionFile));
				continue;
			}

			try {
				String guildUUIDString = configuration.getString("guild", "");
				guild = GuildManager.getGuild(UUID.fromString(guildUUIDString));
			}
			catch(IllegalArgumentException e) {
				guild = GuildManager.getGuildByName(guildName);
				forceSave = true;
			}

			if(guild == null) {
				LoggerUtils.error("There's no guild matching region " + guildName);
				continue;
			}

			try {
				regionUUID = UUID.fromString(trimExtension(regionFile));
			}
			catch(IllegalArgumentException e) {
				regionUUID = UUID.randomUUID();
				forceSave = true;
			}

			NovaRegion region = new NovaRegionImpl(regionUUID);
			region.setAdded();

			Location corner1 = new Location(world, configuration.getInt("corner1.x"), 0, configuration.getInt("corner1.z"));
			Location corner2 = new Location(world, configuration.getInt("corner2.x"), 0, configuration.getInt("corner2.z"));

			region.setCorner(0, corner1);
			region.setCorner(1, corner2);
			region.setWorld(world);
			guild.addRegion(region);
			region.setUnchanged();

			list.add(region);

			if(forceSave) {
				addToSaveQueue(region);
			}
		}

		return list;
	}

	@Override
	public boolean save(NovaRegion region) {
		if(!region.isChanged() && !isInSaveQueue(region) || region.isUnloaded()) {
			return false;
		}

		if(!region.isAdded()) {
			add(region);
		}

		FileConfiguration regionData = getData(region);

		if(regionData != null) {
			try {
				//set values
				regionData.set("world", region.getWorld().getUID().toString());
				regionData.set("guild", region.getGuild().getUUID().toString());

				//corners
				regionData.set("corner1.x", region.getCorner(0).getBlockX());
				regionData.set("corner1.z", region.getCorner(0).getBlockZ());

				regionData.set("corner2.x", region.getCorner(1).getBlockX());
				regionData.set("corner2.z", region.getCorner(1).getBlockZ());

				//save
				regionData.save(getFile(region));
				region.setUnchanged();
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing region. " + region.getGuild().getName());
		}

		return true;
	}

	@Override
	public boolean remove(NovaRegion region) {
		if(!region.isAdded()) {
			return false;
		}

		if(getFile(region).delete()) {
			return true;
		}
		else {
			LoggerUtils.error("Failed to delete region " + region.getUUID() + " file.");
			return false;
		}
	}

	@Override
	public File getFile(NovaRegion region) {
		File file = new File(getDirectory(), region.getUUID().toString() + ".yml");

		if(!file.exists()) {
			File nameFile = new File(getDirectory(), region.getGuild().getName() + ".yml");
			if(nameFile.exists() && !nameFile.renameTo(file)) {
				LoggerUtils.error("Failed to rename file " + nameFile.getName() + " to " + file.getName());
			}
		}

		return file;
	}
}
