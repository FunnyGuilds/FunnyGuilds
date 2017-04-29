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
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaPlayerImpl;
import co.marcin.novaguilds.impl.util.converter.ResourceToUUIDConverterImpl;
import co.marcin.novaguilds.impl.util.converter.UUIDOrNameToGuildConverterImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ResourceManagerPlayerImpl extends AbstractDatabaseResourceManager<NovaPlayer> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerPlayerImpl(Storage storage) {
		super(storage, NovaPlayer.class, "players");
	}

	@Override
	public List<NovaPlayer> load() {
		getStorage().connect();
		final List<NovaPlayer> list = new ArrayList<>();
		final List<UUID> uuids = new ArrayList<>();

		try {
			ResultSet res = getStorage().getPreparedStatement(PreparedStatements.PLAYERS_SELECT).executeQuery();
			while(res.next()) {
				String playerName = res.getString("name");

				UUID uuid = UUID.fromString(res.getString("uuid"));
				NovaPlayer nPlayer = new NovaPlayerImpl(uuid);
				nPlayer.setAdded();

				String invitedTo = res.getString("invitedto");
				List<String> invitedToStringList = StringUtils.semicolonToList(invitedTo);
				Collection<NovaGuild> invitedToList = new UUIDOrNameToGuildConverterImpl().convert(invitedToStringList);

				if(!invitedToStringList.isEmpty() && !StringUtils.isUUID(invitedToStringList.get(0))) {
					addToSaveQueue(nPlayer);
				}

				if(invitedToStringList.size() != invitedToList.size()) {
					addToSaveQueue(nPlayer);
				}

				nPlayer.setId(res.getInt("id"));
				nPlayer.setName(playerName);
				nPlayer.setInvitedTo(invitedToList);

				nPlayer.setPoints(res.getInt("points"));
				nPlayer.setKills(res.getInt("kills"));
				nPlayer.setDeaths(res.getInt("deaths"));

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
				String guildString = res.getString("guild");
				if(!guildString.isEmpty()) {
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
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return list;
	}

	@Override
	public boolean save(NovaPlayer nPlayer) {
		if(!nPlayer.isChanged() && !isInSaveQueue(nPlayer) || nPlayer.isUnloaded() || isInRemovalQueue(nPlayer)) {
			return false;
		}

		if(!nPlayer.isAdded()) {
			add(nPlayer);
			return true;
		}

		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.PLAYERS_UPDATE);

			String invitedTo = StringUtils.joinSemicolon(new ResourceToUUIDConverterImpl<NovaGuild>().convert(nPlayer.getInvitedTo()));
			String guildUUID = nPlayer.hasGuild() ? nPlayer.getGuild().getUUID().toString() : "";

			//prepare and save
			preparedStatement.setString(1, invitedTo);           //invited to list
			preparedStatement.setString(2, guildUUID);           //guild uuid
			preparedStatement.setInt(   3, nPlayer.getPoints()); //points
			preparedStatement.setInt(   4, nPlayer.getKills());  //kills
			preparedStatement.setInt(   5, nPlayer.getDeaths()); //deaths

			preparedStatement.setString(6, nPlayer.getUUID().toString());
			preparedStatement.executeUpdate();
			nPlayer.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return true;
	}

	@Override
	public void add(NovaPlayer nPlayer) {
		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.PLAYERS_INSERT);

			String invitedTo = StringUtils.joinSemicolon(new ResourceToUUIDConverterImpl<NovaGuild>().convert(nPlayer.getInvitedTo()));
			String guildUUID = nPlayer.hasGuild() ? nPlayer.getGuild().getUUID().toString() : "";

			//Prepare and execute
			preparedStatement.setString(1, nPlayer.getUUID().toString()); //UUID
			preparedStatement.setString(2, nPlayer.getName());            //name
			preparedStatement.setString(3, guildUUID);                    //guild uuid
			preparedStatement.setString(4, invitedTo);                    //list of guild invitations
			preparedStatement.setInt(   5, nPlayer.getPoints());          //points
			preparedStatement.setInt(   6, nPlayer.getKills());           //kills
			preparedStatement.setInt(   7, nPlayer.getDeaths());          //deaths
			preparedStatement.executeUpdate();

			nPlayer.setId(getStorage().returnGeneratedKey(preparedStatement));
			nPlayer.setUnchanged();
			nPlayer.setAdded();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public boolean remove(NovaPlayer nPlayer) {
		if(!nPlayer.isAdded()) {
			return false;
		}

		getStorage().connect();

		try {
			PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.PLAYERS_DELETE);
			statement.setString(1, nPlayer.getUUID().toString());
			statement.executeUpdate();
			return true;
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
			return false;
		}
	}

	@Override
	protected void updateUUID(NovaPlayer resource) {
		updateUUID(resource, resource.getId());
	}
}
