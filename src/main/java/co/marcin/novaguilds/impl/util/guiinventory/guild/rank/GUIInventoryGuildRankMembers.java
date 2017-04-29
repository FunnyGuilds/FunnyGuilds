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

package co.marcin.novaguilds.impl.util.guiinventory.guild.rank;


import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.impl.util.guiinventory.guild.player.GUIInventoryGuildPlayersList;

import java.util.ArrayList;
import java.util.List;

public class GUIInventoryGuildRankMembers extends GUIInventoryGuildPlayersList {
	private final NovaRank rank;

	/**
	 * The constructor
	 *
	 * @param guild the guild
	 * @param rank  the rank
	 */
	public GUIInventoryGuildRankMembers(NovaGuild guild, NovaRank rank) {
		super(guild);
		this.rank = rank;
	}

	@Override
	public void generateContent() {
		final List<NovaPlayer> list = new ArrayList<>();

		if(rank.isGeneric()) {
			list.addAll(getMembers(guild, rank));
		}
		else {
			list.addAll(rank.getMembers());
		}

		generateContent(list);
	}

	/**
	 * Gets members of a rank from a guild
	 *
	 * @param guild the guild
	 * @param rank  the rank
	 * @return list of NovaPlayers
	 */
	public static List<NovaPlayer> getMembers(NovaGuild guild, NovaRank rank) {
		final List<NovaPlayer> list = new ArrayList<>();

		for(NovaPlayer nPlayer : rank.getMembers()) {
			if(guild.isMember(nPlayer)) {
				list.add(nPlayer);
			}
		}

		return list;
	}
}
