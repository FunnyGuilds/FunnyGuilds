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
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.api.util.IConverter;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaGuildIkkaImpl;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.impl.util.converter.ResourceToUUIDConverterImpl;
import co.marcin.novaguilds.impl.util.converter.StringToUUIDConverterImpl;
import co.marcin.novaguilds.util.BannerUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import com.google.common.collect.Iterables;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ResourceManagerGuildImpl extends AbstractDatabaseResourceManager<NovaGuild> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerGuildImpl(Storage storage) {
		super(storage, NovaGuild.class, "guilds");
	}

	@Override
	public List<NovaGuild> load() {
		getStorage().connect();
		final List<NovaGuild> list = new ArrayList<>();

		try {
			PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.GUILDS_SELECT);
			ResultSet res = statement.executeQuery();

			while(res.next()) {
				boolean forceSave = false;
				boolean updateUUID = false;
				String homeCoordinates = res.getString("spawn");

				Location homeLocation = null;
				if(!homeCoordinates.isEmpty()) {
					String[] homeSplit = org.apache.commons.lang.StringUtils.split(homeCoordinates, ';');
					if(homeSplit.length == 5) {
						String worldString = homeSplit[0];
						World world;
						try {
							world = plugin.getServer().getWorld(UUID.fromString(worldString));
						}
						catch(IllegalArgumentException e) {
							world = plugin.getServer().getWorld(worldString);
							forceSave = true;
						}

						if(world != null) {
							int x = Integer.parseInt(homeSplit[1]);
							int y = Integer.parseInt(homeSplit[2]);
							int z = Integer.parseInt(homeSplit[3]);
							float yaw = Float.parseFloat(homeSplit[4]);
							homeLocation = new Location(world, x, y, z);
							homeLocation.setYaw(yaw);
						}
					}
				}

				String vaultLocationString = res.getString("bankloc");
				Location vaultLocation = null;
				if(!vaultLocationString.isEmpty()) {
					String[] vaultLocationSplit = vaultLocationString.split(";");

					if(vaultLocationSplit.length == 5) { //LENGTH
						String worldString = vaultLocationSplit[0];
						World world;
						try {
							world = plugin.getServer().getWorld(UUID.fromString(worldString));
						}
						catch(IllegalArgumentException e) {
							world = plugin.getServer().getWorld(worldString);
							forceSave = true;
						}

						if(world != null) {
							int x = Integer.parseInt(vaultLocationSplit[1]);
							int y = Integer.parseInt(vaultLocationSplit[2]);
							int z = Integer.parseInt(vaultLocationSplit[3]);
							vaultLocation = new Location(world, x, y, z);
						}
					}
				}

				//load guild only if there is a spawnpoint.
				//error protection if a world has been deleted
				if(homeLocation == null) {
					LoggerUtils.info("Failed loading guild " + res.getString("name") + ", world does not exist");
				}

				//Loading wrapper
				NovaGuild.LoadingWrapper loadingWrapper = null;
				Collection alliesList = StringUtils.semicolonToList(res.getString("allies"));
				Collection alliesInvitationsList = StringUtils.semicolonToList(res.getString("alliesinv"));
				Collection warsList = StringUtils.semicolonToList(res.getString("war"));
				Collection noWarInvitationsList = StringUtils.semicolonToList(res.getString("nowarinv"));
				Collection migrationList = alliesList.isEmpty()
						? alliesInvitationsList.isEmpty()
								? warsList.isEmpty()
										? noWarInvitationsList
										: warsList
								: alliesInvitationsList
						: alliesList;

				if(migrationList.isEmpty() || StringUtils.isUUID((String) Iterables.getFirst(migrationList, null))) { //UUID
					IConverter<String, UUID> converter = new StringToUUIDConverterImpl();
					alliesList = converter.convert(alliesList);
					alliesInvitationsList = converter.convert(alliesInvitationsList);
					warsList = converter.convert(warsList);
					noWarInvitationsList = converter.convert(noWarInvitationsList);
				}
				else { //name
					loadingWrapper = new NovaGuildImpl.LoadingWrapper37MigrationImpl();
				}

				UUID guildUUID;
				try {
					guildUUID = UUID.fromString(res.getString("uuid"));
				}
				catch(IllegalArgumentException e) {
					guildUUID = UUID.randomUUID();
					updateUUID = true;
				}

				NovaGuild guild;
				if(Config.GUILD_PLAYERPOINTS.getBoolean()) {
					guild = new NovaGuildIkkaImpl(guildUUID, loadingWrapper);
				}
				else {
					guild = new NovaGuildImpl(guildUUID, loadingWrapper);
				}

				guild.setAdded();
				guild.setId(res.getInt("id"));
				guild.setMoney(res.getDouble("money"));
				guild.setPoints(res.getInt("points"));
				guild.setName(res.getString("name"));
				guild.setTag(res.getString("tag"));
				guild.setLeaderName(res.getString("leader"));
				guild.setLives(res.getInt("lives"));
				guild.setTimeRest(res.getLong("timerest"));
				guild.setLostLiveTime(res.getLong("lostlive"));
				guild.setHome(homeLocation);
				guild.setVaultLocation(vaultLocation);
				guild.setSlots(res.getInt("slots"));
				guild.setBannerMeta(BannerUtils.deserialize(res.getString("banner")));
				guild.setInactiveTime(res.getLong("activity"));
				guild.setTimeCreated(res.getLong("created"));
				guild.setOpenInvitation(res.getBoolean("openinv"));

				loadingWrapper = guild.getLoadingWrapper();
				loadingWrapper.setAllies(alliesList);
				loadingWrapper.setAllyInvitations(alliesInvitationsList);
				loadingWrapper.setWars(warsList);
				loadingWrapper.setNoWarInvitations(noWarInvitationsList);

				//set unchanged
				guild.setUnchanged();

				//Fix slots amount
				if(guild.getSlots() <= 0) {
					guild.setSlots(Config.GUILD_SLOTS_START.getInt());
				}

				if(guild.getId() == 0) {
					LoggerUtils.info("Failed to load guild " + res.getString("name") + ". Invalid ID");
					guild.unload();
					continue;
				}

				if(forceSave) {
					addToSaveQueue(guild);
				}

				if(updateUUID) {
					addToUpdateUUIDQueue(guild);
				}

				list.add(guild);
			}
		}
		catch(SQLException e) {
			LoggerUtils.info("An error occurred while loading guilds!");
			LoggerUtils.exception(e);
		}

		return list;
	}

	@Override
	public boolean save(NovaGuild guild) {
		if(!guild.isChanged() && !isInSaveQueue(guild) || guild.isUnloaded() || isInRemovalQueue(guild)) {
			return false;
		}

		if(!guild.isAdded()) {
			add(guild);
			return true;
		}

		getStorage().connect();

		try {
			String homeCoordinates = StringUtils.parseDBLocation(guild.getHome());
			String vaultLocationString = StringUtils.parseDBLocation(guild.getVaultLocation());
			IConverter<NovaGuild, UUID> cvt = new ResourceToUUIDConverterImpl<>();

			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.GUILDS_UPDATE);

			preparedStatement.setString( 1,  guild.getTag());                                                      //tag
			preparedStatement.setString( 2,  guild.getName());                                                     //name
			preparedStatement.setString( 3,  guild.getLeader().getUUID().toString());                              //leader uuid
			preparedStatement.setString( 4,  homeCoordinates);                                                     //home location
			preparedStatement.setString( 5,  StringUtils.joinSemicolon(cvt.convert(guild.getAllies())));           //allies
			preparedStatement.setString( 6,  StringUtils.joinSemicolon(cvt.convert(guild.getAllyInvitations())));  //ally invitations
			preparedStatement.setString( 7,  StringUtils.joinSemicolon(cvt.convert(guild.getWars())));             //wars
			preparedStatement.setString( 8,  StringUtils.joinSemicolon(cvt.convert(guild.getNoWarInvitations()))); //no war invitations
			preparedStatement.setDouble( 9,  guild.getMoney());                                                    //money
			preparedStatement.setInt(    10, guild.getPoints());                                                   //points
			preparedStatement.setInt(    11, guild.getLives());                                                    //lives amount
			preparedStatement.setLong(   12, guild.getTimeRest());                                                 //rest time
			preparedStatement.setLong(   13, guild.getLostLiveTime());                                             //lost live time
			preparedStatement.setLong(   14, guild.getInactiveTime());                                             //inactive time
			preparedStatement.setString( 15, vaultLocationString);                                                 //vault location
			preparedStatement.setInt(    16, guild.getSlots());                                                    //slots
			preparedStatement.setBoolean(17, guild.isOpenInvitation());                                            //open invitation
			preparedStatement.setString( 18, BannerUtils.serialize(guild.getBannerMeta()));                        //banner

			preparedStatement.setString( 19, guild.getUUID().toString());                                          //guild UUID

			preparedStatement.executeUpdate();
			guild.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.info("SQLException while saving a guild.");
			LoggerUtils.exception(e);
		}

		return true;
	}

	@Override
	public void add(NovaGuild guild) {
		getStorage().connect();

		try {
			String homeLocationString = StringUtils.parseDBLocation(guild.getHome());
			String vaultLocationString = StringUtils.parseDBLocation(guild.getVaultLocation());
			IConverter<NovaGuild, UUID> cvt = new ResourceToUUIDConverterImpl<>();

			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.GUILDS_INSERT);
			preparedStatement.setString( 1,  guild.getUUID().toString());                                          //uuid
			preparedStatement.setString( 2,  guild.getTag());                                                      //tag
			preparedStatement.setString( 3,  guild.getName());                                                     //name
			preparedStatement.setString( 4,  guild.getLeader().getUUID().toString());                              //leader uuid
			preparedStatement.setString( 5,  homeLocationString);                                                  //home location
			preparedStatement.setString( 6,  StringUtils.joinSemicolon(cvt.convert(guild.getAllies())));           //allies
			preparedStatement.setString( 7,  StringUtils.joinSemicolon(cvt.convert(guild.getAllyInvitations())));  //ally invitations
			preparedStatement.setString( 8,  StringUtils.joinSemicolon(cvt.convert(guild.getWars())));             //wars
			preparedStatement.setString( 9,  StringUtils.joinSemicolon(cvt.convert(guild.getNoWarInvitations()))); //no war invitations
			preparedStatement.setDouble( 10, guild.getMoney());                                                    //money
			preparedStatement.setInt(    11, guild.getPoints());                                                   //points
			preparedStatement.setInt(    12, guild.getLives());                                                    //lives amount
			preparedStatement.setLong(   13, guild.getTimeRest());                                                 //rest time
			preparedStatement.setLong(   14, guild.getLostLiveTime());                                             //open invitation
			preparedStatement.setLong(   15, guild.getInactiveTime());                                             //lost live time
			preparedStatement.setLong(   16, guild.getTimeCreated());                                              //created time
			preparedStatement.setString( 17, vaultLocationString);                                                 //vault location
			preparedStatement.setInt(    18, guild.getSlots());                                                    //slots
			preparedStatement.setBoolean(19, guild.isOpenInvitation());                                            //open invitation
			preparedStatement.setString( 20, BannerUtils.serialize(guild.getBannerMeta()));                        //banner

			preparedStatement.execute();

			guild.setId(getStorage().returnGeneratedKey(preparedStatement));
			guild.setUnchanged();
			guild.setAdded();
		}
		catch(SQLException e) {
			LoggerUtils.info("SQLException while adding a guild!");
			LoggerUtils.exception(e);
		}
	}

	@Override
	public boolean remove(NovaGuild guild) {
		if(!guild.isAdded()) {
			return false;
		}

		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.GUILDS_DELETE);
			preparedStatement.setString(1, guild.getUUID().toString());
			preparedStatement.executeUpdate();
			return true;
		}
		catch(SQLException e) {
			LoggerUtils.info("SQLException while deleting a guild.");
			LoggerUtils.exception(e);
			return false;
		}
	}

	@Override
	protected void updateUUID(NovaGuild resource) {
		updateUUID(resource, resource.getId());
	}
}
