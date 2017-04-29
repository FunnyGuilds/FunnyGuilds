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

package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.InventoryUtils;
import org.bukkit.command.CommandSender;

public class CommandGuildVaultRestore extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.hasPermission(GuildPermission.VAULT_PLACE)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		if(nPlayer.getGuild().getVaultLocation() != null) {
			Message.CHAT_GUILD_VAULT_PLACE_EXISTS.send(sender);
			return;
		}

		if(InventoryUtils.containsAtLeast(nPlayer.getPlayer().getInventory(), Config.VAULT_ITEM.getItemStack(), 1)) {
			Message.CHAT_GUILD_VAULT_RESTORE_HASALREADY.send(sender);
			return;
		}

		nPlayer.getPlayer().getInventory().addItem(Config.VAULT_ITEM.getItemStack());
		Message.CHAT_GUILD_VAULT_RESTORE_SUCCESS.send(sender);
	}
}
