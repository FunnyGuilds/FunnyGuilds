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

package co.marcin.novaguilds.impl.util.guiinventory.guild;

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;

import java.util.Collection;
import java.util.HashSet;

public class GUIInventoryGuildJoin extends AbstractGUIInventory {
	private final Collection<NovaGuild> guildList = new HashSet<>();

	/**
	 * The constructor
	 *
	 * @param guilds list of guilds
	 */
	public GUIInventoryGuildJoin(Collection<NovaGuild> guilds) {
		super(ChestGUIUtils.getChestSize(guilds.size()), Message.INVENTORY_GUI_JOIN_TITLE);
		guildList.addAll(guilds);
	}

	@Override
	public void generateContent() {
		for(final NovaGuild guild : guildList) {
			MessageWrapper msg = Message.INVENTORY_GUI_JOIN_ROWITEM.clone()
					.setVar(VarKey.GUILD_NAME, guild.getName())
					.setVar(VarKey.TAG, guild.getTag())
					.setVar(VarKey.PLAYER_NAME, guild.getLeader().getName())
					.setVar(VarKey.GUILD_POINTS, guild.getPoints())
					.setVar(VarKey.GUILD_LIVES, guild.getLives());
			registerAndAdd(new CommandExecutor(msg, "novaguilds:guild join " + guild.getName(), true));
		}
	}
}
