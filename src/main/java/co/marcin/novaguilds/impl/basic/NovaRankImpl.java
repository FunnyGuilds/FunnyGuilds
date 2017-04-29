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
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.enums.GuildPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NovaRankImpl extends AbstractResource implements NovaRank {
	private int id;
	private String name;
	private NovaGuild guild;
	private boolean def;
	private boolean clone;
	private final List<GuildPermission> permissions = new ArrayList<>();
	private final List<NovaPlayer> members = new ArrayList<>();

	/**
	 * The constructor
	 *
	 * @param uuid uuid
	 */
	public NovaRankImpl(UUID uuid) {
		super(uuid);
	}

	/**
	 * The constructor
	 *
	 * @param name rank's name
	 */
	public NovaRankImpl(String name) {
		this(UUID.randomUUID());
		this.name = name;
	}

	/**
	 * The constructor
	 * Clones a rank
	 * Creates new UUID
	 * Doesn't assign a guild
	 *
	 * @param rank rank to clone
	 */
	public NovaRankImpl(final NovaRank rank) {
		this(rank.getName());
		setClone(rank.isGeneric());
		setName(rank.getName());
		setPermissions(rank.getPermissions());
	}

	@Override
	public int getId() {
		if(id <= 0) {
			throw new UnsupportedOperationException("This rank might have been loaded from FLAT and has 0 (or negative) ID");
		}

		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<NovaPlayer> getMembers() {
		return members;
	}

	@Override
	public List<GuildPermission> getPermissions() {
		return permissions;
	}

	@Override
	public NovaGuild getGuild() {
		return guild;
	}

	@Override
	public boolean isClone() {
		return clone;
	}

	@Override
	public boolean isDefault() {
		return def;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		setChanged();
	}

	@Override
	public void setPermissions(List<GuildPermission> permissions) {
		this.permissions.clear();
		this.permissions.addAll(permissions);
		setChanged();
	}

	@Override
	public void setGuild(NovaGuild guild) {
		if(guild != null) {
			guild.addRank(this);
		}

		this.guild = guild;
		setChanged();
	}

	@Override
	public void setClone(boolean clone) {
		this.clone = clone;
		setChanged();
	}

	@Override
	public void setDefault(boolean def) {
		this.def = def;
		setChanged();
	}

	@Override
	public void addPermission(GuildPermission permission) {
		if(!permissions.contains(permission)) {
			permissions.add(permission);
			setChanged();
		}
	}

	@Override
	public void addPermissions(List<GuildPermission> list) {
		for(GuildPermission guildPermission : list) {
			addPermission(guildPermission);
		}
	}

	@Override
	public void addMember(NovaPlayer nPlayer) {
		if(!members.contains(nPlayer)) {
			members.add(nPlayer);
			setChanged();
		}
	}

	@Override
	public void removePermission(GuildPermission permission) {
		if(permissions.contains(permission)) {
			permissions.remove(permission);
			setChanged();
		}
	}

	@Override
	public void removePermission(List<GuildPermission> list) {
		for(GuildPermission guildPermission : list) {
			removePermission(guildPermission);
		}
	}

	@Override
	public void removeMember(NovaPlayer nPlayer) {
		if(members.contains(nPlayer)) {
			members.remove(nPlayer);
			setChanged();
		}
	}

	@Override
	public boolean hasPermission(GuildPermission permission) {
		return permissions.contains(permission);
	}

	@Override
	public boolean isGeneric() {
		return this instanceof GenericRankImpl;
	}

	@Override
	public void delete() {
		setDefault(false);
		for(NovaPlayer nPlayer : new ArrayList<>(getMembers())) {
			nPlayer.setGuildRank(getGuild().getDefaultRank());
		}

		NovaGuilds.getInstance().getRankManager().delete(this);
	}
}
