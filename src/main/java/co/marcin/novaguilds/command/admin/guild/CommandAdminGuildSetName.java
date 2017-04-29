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

package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetName extends AbstractCommandExecutor.Reversed<NovaGuild> {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaGuild guild = getParameter();

		if(args.length == 0) { //no new name
			Message.CHAT_ADMIN_GUILD_SET_NAME_ENTERNEWNAME.send(sender);
			return;
		}

		String newName = args[0];

		if(newName.length() < Config.GUILD_SETTINGS_NAME_MIN.getInt()) { //too short name
			Message.CHAT_CREATEGUILD_NAME_TOOSHORT.send(sender);
			return;
		}

		if(newName.length() > Config.GUILD_SETTINGS_NAME_MAX.getInt()) { //too long name
			Message.CHAT_CREATEGUILD_NAME_TOOLONG.send(sender);
			return;
		}

		if(plugin.getGuildManager().exists(newName)) { //name exists
			Message.CHAT_CREATEGUILD_NAMEEXISTS.send(sender);
			return;
		}

		plugin.getGuildManager().changeName(guild, newName);
		plugin.getHologramManager().refreshTopHolograms();
		plugin.getDynmapManager().updateGuild(guild);
		TabUtils.refresh(guild);

		Message.CHAT_ADMIN_GUILD_SET_NAME_SUCCESS.send(sender);
	}
}
