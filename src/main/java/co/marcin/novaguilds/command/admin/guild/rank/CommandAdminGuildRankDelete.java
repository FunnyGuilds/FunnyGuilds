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

package co.marcin.novaguilds.command.admin.guild.rank;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildRankDelete extends AbstractCommandExecutor.Reversed<NovaRank> {
	private final boolean admin;

	public CommandAdminGuildRankDelete() {
		this(true);
	}

	public CommandAdminGuildRankDelete(boolean admin) {
		this.admin = admin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!admin && !nPlayer.hasPermission(GuildPermission.RANK_DELETE)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		NovaRank rank = getParameter();

		if(rank == null) {
			Message.CHAT_ADMIN_GUILD_RANK_NOTFOUND.send(sender);
			return;
		}

		Message.CHAT_ADMIN_GUILD_RANK_DELETE_SUCCESS.send(sender);
		rank.delete();
	}
}
