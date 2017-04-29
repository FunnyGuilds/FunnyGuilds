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
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.basic.NovaPlayerImpl;
import co.marcin.novaguilds.impl.util.converter.ResourceToUUIDConverterImpl;
import co.marcin.novaguilds.impl.util.converter.ToStringConverterImpl;
import co.marcin.novaguilds.impl.util.converter.UUIDOrNameToGuildConverterImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ResourceManagerPlayerImpl extends AbstractYAMLResourceManager<NovaPlayer> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerPlayerImpl(Storage storage) {
		super(storage, NovaPlayer.class, "player/");
	}

	@Override
	public List<NovaPlayer> load() {
		final List<NovaPlayer> list = new ArrayList<>();
		final List<UUID> uuids = new ArrayList<>();

		for(File playerFile : getFiles()) {
			FileConfiguration configuration = loadConfiguration(playerFile);

			if(configuration != null) {
				UUID uuid = UUID.fromString(trimExtension(playerFile));
				NovaPlayer nPlayer = new NovaPlayerImpl(uuid);
				nPlayer.setAdded();
				nPlayer.setName(configuration.getString("name"));

				List<String> invitedToStringList = configuration.getStringList("invitedto");
				Collection<NovaGuild> invitedToList = new UUIDOrNameToGuildConverterImpl().convert(invitedToStringList);

				if(!invitedToStringList.isEmpty() && !StringUtils.isUUID(invitedToStringList.get(0))) {
					LoggerUtils.debug("Migrating invited list for player " + nPlayer.getUUID().toString());
					addToSaveQueue(nPlayer);
				}

				if(invitedToStringList.size() != invitedToList.size()) {
					LoggerUtils.debug("Invited to size differs for player " + nPlayer.getUUID().toString());
					addToSaveQueue(nPlayer);
				}

				nPlayer.setInvitedTo(invitedToList);
				nPlayer.setPoints(configuration.getInt("points"));
				nPlayer.setKills(configuration.getInt("kills"));
				nPlayer.setDeaths(configuration.getInt("deaths"));

				//Remove if doubled
				if(uuids.contains(nPlayer.getUUID())) {
					nPlayer.unload();

					if(Config.DELETEINVALID.getBoolean()) {
						addToRemovalQueue(nPlayer);
						LoggerUtils.info("Removed doubled player: " + nPlayer.getName());
					}
					else {
						LoggerUtils.error("Doubled player: " + nPlayer.getName());
					}

					continue;
				}

				//Set the guild
				String guildString = configuration.getString("guild");
				if(configuration.contains("guild") && !guildString.isEmpty()) {
					NovaGuild guild;
					try {
						guild = GuildManager.getGuild(UUID.fromString(guildString));
					}
					catch(IllegalArgumentException e) {
						guild = GuildManager.getGuildByName(guildString);
						addToSaveQueue(nPlayer);
					}

					if(guild != null) {
						guild.addPlayer(nPlayer);
					}
				}

				nPlayer.setUnchanged();

				list.add(nPlayer);
				uuids.add(nPlayer.getUUID());
			}
		}

		return list;
	}

	@Override
	public boolean save(NovaPlayer nPlayer) {
		if(!nPlayer.isChanged() && !isInSaveQueue(nPlayer) || nPlayer.isUnloaded()) {
			return false;
		}

		if(!nPlayer.isAdded()) {
			add(nPlayer);
		}

		FileConfiguration playerData = getData(nPlayer);

		if(playerData != null) {
			try {
				//set values
				playerData.set("name", nPlayer.getName());
				playerData.set("guild", nPlayer.hasGuild() ? nPlayer.getGuild().getUUID().toString() : null);
				playerData.set("invitedto", new ToStringConverterImpl().convert((List) new ResourceToUUIDConverterImpl<NovaGuild>().convert(nPlayer.getInvitedTo())));
				playerData.set("points", nPlayer.getPoints());
				playerData.set("kills", nPlayer.getKills());
				playerData.set("deaths", nPlayer.getDeaths());

				if(playerData.contains("uuid")) {
					playerData.set("uuid", null);
				}

				//save
				playerData.save(getFile(nPlayer));
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing player. " + nPlayer.getName());
		}

		return true;
	}

	@Override
	public boolean remove(NovaPlayer nPlayer) {
		if(!nPlayer.isAdded()) {
			return false;
		}

		if(getFile(nPlayer).delete()) {
			LoggerUtils.info("Deleted player " + nPlayer.getName() + "'s file.");
			return true;
		}
		else {
			LoggerUtils.error("Failed to delete player " + nPlayer.getName() + "'s file.");
			return false;
		}
	}

	@Override
	public File getFile(NovaPlayer nPlayer) {
		return new File(getDirectory(), nPlayer.getUUID().toString() + ".yml");
	}
}
