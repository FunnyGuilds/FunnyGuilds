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

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandAdminGuildRankList extends AbstractCommandExecutor.Reversed<NovaGuild> {
	protected final boolean admin;

	public CommandAdminGuildRankList() {
		this(true);
	}

	public CommandAdminGuildRankList(boolean admin) {
		this.admin = admin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(!admin) {
			NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
			set(nPlayer.getGuild());

			if(!nPlayer.hasPermission(GuildPermission.RANK_LIST)) {
				Message.CHAT_GUILD_NOGUILDPERM.send(sender);
				return;
			}
		}

		Message.CHAT_ADMIN_GUILD_RANK_LIST_HEADER.clone().setVar(VarKey.GUILD_NAME, getParameter().getName()).send(sender);
		List<MessageWrapper> itemList = new ArrayList<>();

		for(NovaRank rank : getParameter().getRanks()) {
			MessageWrapper row = Message.CHAT_ADMIN_GUILD_RANK_LIST_ITEM.clone();
			row.setVar(VarKey.NAME, rank.getName());
			row.setVar(VarKey.GUILD_NAME, rank.getGuild().getName());
			row.setVar(VarKey.UUID, rank.getUUID().toString());
			itemList.add(row);
		}

		Message.send(itemList, sender);
	}
}
