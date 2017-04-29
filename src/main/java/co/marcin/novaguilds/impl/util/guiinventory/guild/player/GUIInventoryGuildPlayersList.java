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

package co.marcin.novaguilds.impl.util.guiinventory.guild.player;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;

import java.util.List;

public class GUIInventoryGuildPlayersList extends AbstractGUIInventory {
	protected final NovaGuild guild;

	/**
	 * The constructor
	 * Displays the list of players
	 * in a guild
	 *
	 * @param guild the guild
	 */
	public GUIInventoryGuildPlayersList(NovaGuild guild) {
		super(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_PLAYERSLIST_TITLE);
		this.guild = guild;
	}

	@Override
	public void generateContent() {
		generateContent(guild.getPlayers());
	}

	/**
	 * Generates the content
	 *
	 * @param playerList list of players
	 */
	public void generateContent(List<NovaPlayer> playerList) {
		for(final NovaPlayer nPlayer : playerList) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_PLAYERSLIST_ROWITEM
					.clone()
					.setVar(VarKey.PLAYER_NAME, nPlayer.getName())) {
				@Override
				public void execute() {
					new GUIInventoryGuildPlayerSettings(nPlayer).open(getViewer());
				}
			});
		}
	}
}
