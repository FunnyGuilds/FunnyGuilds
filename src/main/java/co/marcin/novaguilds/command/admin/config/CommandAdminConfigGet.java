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

package co.marcin.novaguilds.command.admin.config;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandAdminConfigGet extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) {
			getCommand().getUsageMessage().send(sender);
			return;
		}

		String path = args[0];
		String value = "";
		Map<VarKey, String> vars = new HashMap<>();
		FileConfiguration config = plugin.getConfigManager().getConfig();

		if(!config.contains(path)) {
			Message.CHAT_INVALIDPARAM.send(sender);
			return;
		}

		if(config.isConfigurationSection(path)) {
			int depth = 1;
			String lastSection = null;

			vars.put(VarKey.DEPTH, "");
			vars.put(VarKey.KEY, path);
			Message.CHAT_ADMIN_CONFIG_GET_LIST_SECTION.clone().vars(vars).send(sender);

			for(String string : config.getConfigurationSection(path).getKeys(true)) {
				String[] prefixSplit = StringUtils.split(string, ".");
				String prefix = StringUtils.contains(string, ".") ? StringUtils.removeEnd(string, "." + prefixSplit[prefixSplit.length - 1]) : string;

				if(lastSection != null && !prefix.startsWith(lastSection)) {
					depth--;
					lastSection = null;
				}

				String space = "";
				for(int i = 0; i < depth; i++) {
					space += " ";
				}
				vars.put(VarKey.DEPTH, space);

				if(config.isConfigurationSection(path + "." + string)) {
					depth++;
					lastSection = string;

					vars.put(VarKey.KEY, prefixSplit[prefixSplit.length - 1]);
					Message.CHAT_ADMIN_CONFIG_GET_LIST_SECTION.clone().vars(vars).send(sender);
				}
				else { //key
					vars.put(VarKey.KEY, StringUtils.removeStart(string, prefix + "."));
					Message.CHAT_ADMIN_CONFIG_GET_LIST_KEY.clone().vars(vars).send(sender);
				}
			}
		}
		else {
			if(config.isList(path)) {
				value = StringUtils.join(config.getStringList(path), " ");
			}
			else {
				value = config.getString(path);
			}
		}

		vars.put(VarKey.KEY, path);
		vars.put(VarKey.VALUE, value);

		if(!value.isEmpty()) {
			Message.CHAT_ADMIN_CONFIG_GET_SINGLE.clone().vars(vars).send(sender);
		}
	}

	@Override
	protected Collection<String> tabCompleteOptions(CommandSender sender, String[] args) {
		return NovaGuilds.getInstance().getConfigManager().getConfig().getKeys(true);
	}
}
