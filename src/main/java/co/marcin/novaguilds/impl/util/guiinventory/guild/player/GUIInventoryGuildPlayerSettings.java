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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.apache.commons.lang.StringUtils;

public class GUIInventoryGuildPlayerSettings extends AbstractGUIInventory {
	private final NovaPlayer nPlayer;

	/**
	 * The constructor
	 *
	 * @param nPlayer the player who's settings are being edited
	 */
	public GUIInventoryGuildPlayerSettings(NovaPlayer nPlayer) {
		super(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_PLAYERSETTINGS_TITLE.clone().setVar(VarKey.PLAYER_NAME, nPlayer.getName()));
		this.nPlayer = nPlayer;
	}

	@Override
	public void generateContent() {
		if(!nPlayer.equals(getViewer())
				&& (getViewer().hasPermission(GuildPermission.KICK) && Permission.NOVAGUILDS_GUILD_KICK.has(getViewer()) || Permission.NOVAGUILDS_ADMIN_GUILD_KICK.has(getViewer()))) {
			registerAndAdd(new CommandExecutor(Message.INVENTORY_GUI_PLAYERSETTINGS_ITEM_KICK, "novaguilds:guild kick " + nPlayer.getName(), true));
		}

		if(!nPlayer.equals(getViewer())
				&& (getViewer().hasPermission(GuildPermission.RANK_SET) && Permission.NOVAGUILDS_GUILD_RANK_SET.has(getViewer())|| Permission.NOVAGUILDS_ADMIN_GUILD_RANK_SET.has(getViewer()))) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_PLAYERSETTINGS_ITEM_RANK
					.clone()
					.setVar(VarKey.RANKNAME, nPlayer.getGuildRank() == null ? "Invalid_rank" : StringUtils.replace(nPlayer.getGuildRank().getName(), " ", "_"))) {
				@Override
				public void execute() {
					new GUIInventoryGuildPlayerSettingsRank(nPlayer).open(getViewer());
				}
			});
		}
	}
}
