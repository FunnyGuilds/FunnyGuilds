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

package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminRegionList extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		Message.CHAT_REGION_LIST_HEADER.send(sender);

		int perPage = 10;
		int size = plugin.getRegionManager().getRegions().size();
		int pages_number = size / perPage;
		if(size % perPage > 0) {
			pages_number++;
		}

		//pages
		int page = 1;
		if(args.length == 1 && NumberUtils.isNumeric(args[0])) {
			page = Integer.parseInt(args[0]);
		}

		if(page < 1) {
			page = 1;
		}

		String rowFormat = Message.CHAT_REGION_LIST_ITEM.get();
		int i = 0;
		boolean display = false;
		Map<VarKey, String> vars = new HashMap<>();

		if(size > perPage) {
			vars.put(VarKey.PAGE, String.valueOf(page));
			vars.put(VarKey.NEXT, String.valueOf(page + 1));
			vars.put(VarKey.PAGES, String.valueOf(pages_number));

			if(pages_number > page) {
				Message.CHAT_ADMIN_GUILD_LIST_PAGE_HASNEXT.clone().vars(vars).send(sender);
			}
			else {
				Message.CHAT_ADMIN_GUILD_LIST_PAGE_NONEXT.clone().vars(vars).send(sender);
			}
		}

		for(NovaRegion region : plugin.getRegionManager().getRegions()) {
			vars.clear();

			if((i + 1 > (page - 1) * perPage || page == 1) && !display) {
				display = true;
				i = 0;
			}

			if(display) {
				vars.put(VarKey.GUILD_NAME, region.getGuild().getName());
				vars.put(VarKey.INDEX, String.valueOf(region.getIndex()));
				vars.put(VarKey.X, String.valueOf(region.getCorner(0).getBlockX()));
				vars.put(VarKey.Z, String.valueOf(region.getCorner(0).getBlockZ()));

				String rowMessage = MessageManager.replaceVarKeyMap(rowFormat, vars);
				MessageManager.sendMessage(sender, rowMessage);

				if(i + 1 >= perPage) {
					break;
				}
			}

			i++;
		}
	}
}
