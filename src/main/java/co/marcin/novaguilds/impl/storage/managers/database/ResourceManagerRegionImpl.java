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

package co.marcin.novaguilds.impl.storage.managers.database;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.RegionUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerRegionImpl extends AbstractDatabaseResourceManager<NovaRegion> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerRegionImpl(Storage storage) {
		super(storage, NovaRegion.class, "regions");
	}

	@Override
	public List<NovaRegion> load() {
		getStorage().connect();
		final List<NovaRegion> list = new ArrayList<>();

		try {
			PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.REGIONS_SELECT);

			ResultSet res = statement.executeQuery();
			while(res.next()) {
				boolean forceSave = false;
				boolean updateUUID = false;
				World world;

				//Find the world
				try {
					world = plugin.getServer().getWorld(UUID.fromString(res.getString("world")));
				}
				catch(IllegalArgumentException e) {
					world = plugin.getServer().getWorld(res.getString("world"));
					forceSave = true;
				}

				//Find the guild
				String guildString = res.getString("guild");
				NovaGuild guild;

				try {
					guild = GuildManager.getGuild(UUID.fromString(guildString));
				}
				catch(IllegalArgumentException e) {
					guild = GuildManager.getGuildByName(guildString);
					forceSave = true;
				}

				UUID regionUUID;
				String regionUUIDString = res.getString("uuid");

				//Get region UUID or generate a new one
				if(regionUUIDString != null && !regionUUIDString.isEmpty()) {
					regionUUID = UUID.fromString(regionUUIDString);
				}
				else {
					regionUUID = UUID.randomUUID();
					updateUUID = true;
				}

				NovaRegion region = new NovaRegionImpl(regionUUID);

				Location corner1 = RegionUtils.deserializeLocation2D(res.getString("loc_1"));
				Location corner2 = RegionUtils.deserializeLocation2D(res.getString("loc_2"));
				corner1.setWorld(world);
				corner2.setWorld(world);

				region.setAdded();
				region.setCorner(0, corner1);
				region.setCorner(1, corner2);
				region.setWorld(world);
				region.setId(res.getInt("id"));

				if(forceSave) {
					addToSaveQueue(region);
				}

				if(updateUUID) {
					addToUpdateUUIDQueue(region);
				}

				if(guild == null) {
					LoggerUtils.error("There's no guild matching region " + guildString);

					if(Config.DELETEINVALID.getBoolean()) {
						addToRemovalQueue(region);
					}

					continue;
				}

				if(world == null) {
					LoggerUtils.info("Failed loading region for guild " + guildString + ", world does not exist.");

					if(Config.DELETEINVALID.getBoolean()) {
						addToRemovalQueue(region);
					}

					continue;
				}

				guild.addRegion(region);
				region.setUnchanged();
				list.add(region);
			}
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return list;
	}

	@Override
	public boolean save(NovaRegion region) {
		if(!region.isChanged() && !isInSaveQueue(region) || region.isUnloaded() || isInRemovalQueue(region)) {
			return false;
		}

		if(!region.isAdded()) {
			add(region);
			return true;
		}

		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.REGIONS_UPDATE);

			String loc1 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(0));
			String loc2 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(1));

			preparedStatement.setString(1, loc1);                                   //corner 1
			preparedStatement.setString(2, loc2);                                   //corner 2
			preparedStatement.setString(3, region.getGuild().getUUID().toString()); //guild uuid
			preparedStatement.setString(4, region.getWorld().getUID().toString());  //world uuid
			preparedStatement.setString(5, region.getUUID().toString());            //region UUID
			preparedStatement.executeUpdate();

			region.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return true;
	}

	@Override
	public void add(NovaRegion region) {
		getStorage().connect();

		try {
			String loc1 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(0));
			String loc2 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(1));

			if(region.getWorld() == null) {
				region.setWorld(Bukkit.getWorlds().get(0));
			}

			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.REGIONS_INSERT);
			preparedStatement.setString(1, region.getUUID().toString());            //region uuid
			preparedStatement.setString(2, loc1);                                   //corner 1
			preparedStatement.setString(3, loc2);                                   //corner 2
			preparedStatement.setString(4, region.getGuild().getUUID().toString()); //guild uuid
			preparedStatement.setString(5, region.getWorld().getUID().toString());  //world uuid
			preparedStatement.executeUpdate();

			region.setId(getStorage().returnGeneratedKey(preparedStatement));
			region.setUnchanged();
			region.setAdded();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public boolean remove(NovaRegion region) {
		if(!region.isAdded()) {
			return false;
		}

		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.REGIONS_DELETE);
			preparedStatement.setString(1, region.getUUID().toString());
			preparedStatement.executeUpdate();
			return true;
		}
		catch(SQLException e) {
			LoggerUtils.info("An error occurred while deleting a guild's region (" + region.getGuild().getName() + ")");
			LoggerUtils.exception(e);
			return false;
		}
	}

	@Override
	protected void updateUUID(NovaRegion resource) {
		updateUUID(resource, resource.getId());
	}
}
