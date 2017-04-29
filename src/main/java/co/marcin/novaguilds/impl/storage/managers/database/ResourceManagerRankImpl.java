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
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaRankImpl;
import co.marcin.novaguilds.impl.util.converter.EnumToNameConverterImpl;
import co.marcin.novaguilds.impl.util.converter.ResourceToUUIDConverterImpl;
import co.marcin.novaguilds.impl.util.converter.UUIDOrNameToPlayerConverterImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.json.JSONArray;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ResourceManagerRankImpl extends AbstractDatabaseResourceManager<NovaRank> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerRankImpl(Storage storage) {
		super(storage, NovaRank.class, "ranks");
	}

	@Override
	public List<NovaRank> load() {
		getStorage().connect();
		final List<NovaRank> list = new ArrayList<>();

		try {
			PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.RANKS_SELECT);

			ResultSet res = statement.executeQuery();
			while(res.next()) {
				boolean updateUUID = false;

				UUID rankUUID;
				try {
					rankUUID = UUID.fromString(res.getString("uuid"));
				}
				catch(IllegalArgumentException e) {
					rankUUID = UUID.randomUUID();
					updateUUID = true;
				}

				NovaRank rank = new NovaRankImpl(rankUUID);
				rank.setId(res.getInt("id"));
				rank.setAdded();

				NovaGuild guild;
				try {
					guild = GuildManager.getGuild(UUID.fromString(res.getString("guild")));
				}
				catch(IllegalArgumentException e) {
					guild = GuildManager.getGuildByName(res.getString("guild"));
					addToSaveQueue(rank);
				}

				if(updateUUID) {
					addToUpdateUUIDQueue(rank);
				}

				if(guild == null) {
					LoggerUtils.error("Failed to find guild: " + res.getString("guild"));
					rank.unload();

					if(Config.DELETEINVALID.getBoolean()) {
						addToRemovalQueue(rank);
					}

					continue;
				}

				rank.setName(res.getString("name"));
				rank.setGuild(guild);

				for(String permName : StringUtils.jsonToList(res.getString("permissions"))) {
					rank.addPermission(GuildPermission.valueOf(permName));
				}

				List<String> memberStringList = StringUtils.jsonToList(res.getString("members"));
				Collection<NovaPlayer> memberList = new UUIDOrNameToPlayerConverterImpl().convert(memberStringList);

				if(memberList.size() != memberStringList.size()) {
					addToSaveQueue(rank);
				}

				for(NovaPlayer nPlayer : memberList) {
					nPlayer.setGuildRank(rank);
				}

				rank.setDefault(res.getBoolean("def"));
				rank.setClone(res.getBoolean("clone"));
				rank.setUnchanged();

				list.add(rank);
			}
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return list;
	}

	@Override
	public boolean save(NovaRank rank) {
		if(!rank.isChanged() && !isInSaveQueue(rank) || rank.isUnloaded() || isInRemovalQueue(rank)) {
			return false;
		}

		if(!rank.isAdded()) {
			add(rank);
			return true;
		}

		getStorage().connect();

		final Collection<UUID> memberNamesList = new ResourceToUUIDConverterImpl<NovaPlayer>().convert(rank.getMembers());
		final Collection<String> permissionNamesList = new EnumToNameConverterImpl<GuildPermission>().convert(rank.getPermissions());

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.RANKS_UPDATE);
			preparedStatement.setString( 1, rank.getName());                                //rank name
			preparedStatement.setString( 2, rank.getGuild().getUUID().toString());          //guild uuid
			preparedStatement.setString( 3, new JSONArray(permissionNamesList).toString()); //permissions
			preparedStatement.setString( 4, new JSONArray(memberNamesList).toString());     //members
			preparedStatement.setBoolean(5, rank.isDefault());                              //default
			preparedStatement.setBoolean(6, rank.isClone());                                //clone

			preparedStatement.setString( 7, rank.getUUID().toString());                     //rank uuid
			preparedStatement.execute();

			rank.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return true;
	}

	@Override
	public void add(NovaRank rank) {
		getStorage().connect();

		try {
			Collection<UUID> memberNamesList = new ResourceToUUIDConverterImpl<NovaPlayer>().convert(rank.getMembers());
			Collection<String> permissionNamesList = new EnumToNameConverterImpl<GuildPermission>().convert(rank.getPermissions());

			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.RANKS_INSERT);
			preparedStatement.setString( 1, rank.getUUID().toString());                     //UUID
			preparedStatement.setString( 2, rank.getName());                                //rank name
			preparedStatement.setString( 3, rank.getGuild().getUUID().toString());          //guild name
			preparedStatement.setString( 4, new JSONArray(permissionNamesList).toString()); //permissions
			preparedStatement.setString( 5, new JSONArray(memberNamesList).toString());     //members
			preparedStatement.setBoolean(6, rank.isDefault());                              //default
			preparedStatement.setBoolean(7, rank.isClone());                                //clone
			preparedStatement.execute();

			rank.setId(getStorage().returnGeneratedKey(preparedStatement));
			rank.setUnchanged();
			rank.setAdded();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public boolean remove(NovaRank rank) {
		if(!rank.isAdded()) {
			return false;
		}

		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.RANKS_DELETE);
			preparedStatement.setString(1, rank.getUUID().toString());
			preparedStatement.execute();
			return true;
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
			return false;
		}
	}

	@Override
	protected void updateUUID(NovaRank resource) {
		updateUUID(resource, resource.getId());
	}
}
