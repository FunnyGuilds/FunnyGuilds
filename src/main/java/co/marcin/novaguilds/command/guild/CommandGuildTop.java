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

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandGuildTop extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		Collection<NovaGuild> guilds = plugin.getGuildManager().getGuilds();

		if(guilds.isEmpty()) {
			Message.CHAT_GUILD_NOGUILDS.send(sender);
			return;
		}

		int limit = Config.LEADERBOARD_GUILD_ROWS.getInt();
		int i = 1;

		Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.send(sender);

		Map<VarKey, String> vars = new HashMap<>();
		for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
			vars.clear();
			vars.put(VarKey.GUILD_NAME, guild.getName());
			vars.put(VarKey.N, String.valueOf(i));
			vars.put(VarKey.GUILD_POINTS, String.valueOf(guild.getPoints()));
			Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_ROW.clone().vars(vars).send(sender);
			i++;
		}
	}
}
