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
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.api.util.IConverter;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.basic.NovaGuildIkkaImpl;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.impl.util.converter.ResourceToUUIDConverterImpl;
import co.marcin.novaguilds.impl.util.converter.StringToUUIDConverterImpl;
import co.marcin.novaguilds.impl.util.converter.ToStringConverterImpl;
import co.marcin.novaguilds.util.BannerUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import com.google.common.collect.Iterables;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ResourceManagerGuildImpl extends AbstractYAMLResourceManager<NovaGuild> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerGuildImpl(Storage storage) {
		super(storage, NovaGuild.class, "guild/");
	}

	@Override
	public List<NovaGuild> load() {
		final List<NovaGuild> list = new ArrayList<>();

		for(File guildFile : getFiles()) {
			FileConfiguration configuration = loadConfiguration(guildFile);
			boolean forceSave = false;

			if(configuration != null) {
				NovaGuild.LoadingWrapper loadingWrapper = null;
				Collection alliesList = configuration.getStringList("allies");
				Collection alliesInvitationsList = configuration.getStringList("alliesinv");
				Collection warsList = configuration.getStringList("wars");
				Collection noWarInvitationsList = configuration.getStringList("nowar");
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
					forceSave = true;
				}

				NovaGuild guild;
				if(Config.GUILD_PLAYERPOINTS.getBoolean()) {
					guild = new NovaGuildIkkaImpl(UUID.fromString(trimExtension(guildFile)), loadingWrapper);
				}
				else {
					guild = new NovaGuildImpl(UUID.fromString(trimExtension(guildFile)), loadingWrapper);
				}

				guild.setAdded();
				guild.setId(configuration.getInt("id"));
				guild.setName(configuration.getString("name"));
				guild.setTag(configuration.getString("tag"));
				guild.setLeaderName(configuration.getString("leader"));
				guild.setMoney(configuration.getDouble("money"));
				guild.setPoints(configuration.getInt("points"));
				guild.setLives(configuration.getInt("lives"));
				guild.setSlots(configuration.getInt("slots"));
				guild.setBannerMeta(BannerUtils.deserialize(configuration.getString("banner")));
				guild.setTimeRest(configuration.getLong("timerest"));
				guild.setLostLiveTime(configuration.getLong("lostlive"));
				guild.setInactiveTime(configuration.getLong("activity"));
				guild.setTimeCreated(configuration.getLong("created"));
				guild.setOpenInvitation(configuration.getBoolean("openinv"));

				//Loading wrapper
				loadingWrapper = guild.getLoadingWrapper();
				loadingWrapper.setAllies(alliesList);
				loadingWrapper.setAllyInvitations(alliesInvitationsList);
				loadingWrapper.setWars(warsList);
				loadingWrapper.setNoWarInvitations(noWarInvitationsList);

				//home
				String homeWorldString = configuration.getString("home.world");
				if(homeWorldString == null || homeWorldString.isEmpty()) {
					LoggerUtils.error("Found null or empty world (guild: " + guild.getName() + ")");
					guild.unload();

					if(Config.DELETEINVALID.getBoolean()) {
						addToRemovalQueue(guild);
					}

					continue;
				}

				World homeWorld;
				try {
					homeWorld = plugin.getServer().getWorld(UUID.fromString(homeWorldString));
				}
				catch(IllegalArgumentException e) {
					homeWorld = plugin.getServer().getWorld(homeWorldString);
				}

				if(homeWorld == null) {
					LoggerUtils.error("Found invalid world: " + homeWorldString + " (guild: " + guild.getName() + ")");
					guild.unload();

					if(Config.DELETEINVALID.getBoolean()) {
						addToRemovalQueue(guild);
					}

					continue;
				}

				int x = configuration.getInt("home.x");
				int y = configuration.getInt("home.y");
				int z = configuration.getInt("home.z");
				float yaw = (float) configuration.getDouble("home.yaw");
				Location homeLocation = new Location(homeWorld, x, y, z);
				homeLocation.setYaw(yaw);
				guild.setHome(homeLocation);

				//vault location
				if(configuration.isConfigurationSection("bankloc")) {
					World vaultWorld;
					try {
						vaultWorld = plugin.getServer().getWorld(UUID.fromString(configuration.getString("bankloc.world")));
					}
					catch(IllegalArgumentException e) {
						vaultWorld = plugin.getServer().getWorld(configuration.getString("bankloc.world"));
					}

					if(vaultWorld != null) {
						x = configuration.getInt("bankloc.x");
						y = configuration.getInt("bankloc.y");
						z = configuration.getInt("bankloc.z");
						Location vaultLocation = new Location(vaultWorld, x, y, z);
						guild.setVaultLocation(vaultLocation);
					}
				}

				guild.setUnchanged();

				//Fix slots amount
				if(guild.getSlots() <= 0) {
					guild.setSlots(Config.GUILD_SLOTS_START.getInt());
				}

				//Remove old ranks data
				if(configuration.isConfigurationSection("ranks") || forceSave) {
					addToSaveQueue(guild);
				}

				list.add(guild);
			}
		}

		return list;
	}

	@Override
	public boolean save(NovaGuild guild) {
		if(!guild.isChanged() && !isInSaveQueue(guild) || guild.isUnloaded()) {
			return false;
		}

		if(!guild.isAdded()) {
			add(guild);
		}

		FileConfiguration guildData = getData(guild);

		if(guildData == null) {
			LoggerUtils.error("Attempting to save non-existing guild. " + guild.getName());
			return false;
		}

		try {
			IConverter<NovaGuild, UUID> resourceToUUIDConverter = new ResourceToUUIDConverterImpl<>();
			IConverter<Object, String> toStringConverter = new ToStringConverterImpl();

			//set values
			guildData.set("name",      guild.getName());
			guildData.set("tag",       guild.getTag());
			guildData.set("leader",    guild.getLeader().getUUID().toString());
			guildData.set("allies",    toStringConverter.convert((List) resourceToUUIDConverter.convert(guild.getAllies())));
			guildData.set("alliesinv", toStringConverter.convert((List) resourceToUUIDConverter.convert(guild.getAllyInvitations())));
			guildData.set("wars",      toStringConverter.convert((List) resourceToUUIDConverter.convert(guild.getWars())));
			guildData.set("nowar",     toStringConverter.convert((List) resourceToUUIDConverter.convert(guild.getNoWarInvitations())));
			guildData.set("money",     guild.getMoney());
			guildData.set("points",    guild.getPoints());
			guildData.set("lives",     guild.getLives());
			guildData.set("slots",     guild.getSlots());
			guildData.set("banner",    BannerUtils.serialize(guild.getBannerMeta()));

			guildData.set("timerest",  guild.getTimeRest());
			guildData.set("lostlive",  guild.getLostLiveTime());
			guildData.set("activity",  guild.getInactiveTime());
			guildData.set("created",   guild.getTimeCreated());
			guildData.set("openinv",   guild.isOpenInvitation());

			//spawnpoint
			Location home = guild.getHome();
			guildData.set("home.world", home.getWorld().getUID().toString());
			guildData.set("home.x", home.getBlockX());
			guildData.set("home.y", home.getBlockY());
			guildData.set("home.z", home.getBlockZ());
			guildData.set("home.yaw", home.getYaw());

			//bankloc
			Location vaultLocation = guild.getVaultLocation();
			if(vaultLocation != null) {
				guildData.set("bankloc.world", vaultLocation.getWorld().getUID().toString());
				guildData.set("bankloc.x", vaultLocation.getBlockX());
				guildData.set("bankloc.y", vaultLocation.getBlockY());
				guildData.set("bankloc.z", vaultLocation.getBlockZ());
			}
			else {
				guildData.set("bankloc", null);
			}

			//Remove old ranks data
			if(guildData.isConfigurationSection("ranks")) {
				guildData.set("ranks", null);
			}

			//save
			guildData.save(getFile(guild));
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}

		return true;
	}

	@Override
	public boolean remove(NovaGuild guild) {
		if(!guild.isAdded()) {
			return false;
		}

		if(getFile(guild).delete()) {
			LoggerUtils.info("Deleted guild " + guild.getName() + "'s file.");
			return true;
		}
		else {
			LoggerUtils.error("Failed to delete guild " + guild.getName() + "'s file.");
			return false;
		}
	}

	@Override
	public File getFile(NovaGuild guild) {
		return new File(getDirectory(), guild.getUUID().toString() + ".yml");
	}
}
