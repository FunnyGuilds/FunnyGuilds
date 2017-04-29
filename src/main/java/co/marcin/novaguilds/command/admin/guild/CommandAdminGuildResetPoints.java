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
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.TabUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminGuildResetPoints extends AbstractCommandExecutor {
	private enum ConditionType {
		LARGER,
		SMALLER,
		EQUAL,
		ALL
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) {
			getCommand().getUsageMessage().send(sender);
			return;
		}

		String condition = args[0];
		ConditionType conditionType = null;

		if(condition.equalsIgnoreCase("all")) {
			conditionType = ConditionType.ALL;
		}
		else {
			switch(condition.charAt(0)) {
				case '<':
					conditionType = ConditionType.SMALLER;
					break;
				case '>':
					conditionType = ConditionType.LARGER;
					break;
				case '=':
					conditionType = ConditionType.EQUAL;
					break;
			}
		}

		if(conditionType == null) {
			Message.CHAT_ADMIN_GUILD_RESET_POINTS_INVALIDCONDITIONTYPE.send(sender);
			return;
		}

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.CHAR, String.valueOf(condition.charAt(0)));

		if(condition.length() == 1) {
			Message.CHAT_ADMIN_GUILD_RESET_POINTS_NOVALUE.clone().vars(vars).send(sender);
			return;
		}

		int value = 0;
		if(conditionType != ConditionType.ALL) {
			String valueString = StringUtils.substring(condition, 1);

			if(!NumberUtils.isNumeric(valueString)) {
				Message.CHAT_ENTERINTEGER.send(sender);
				return;
			}

			value = Integer.valueOf(valueString);
		}

		int count = 0;
		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			int points = guild.getPoints();
			boolean passed = conditionType == ConditionType.ALL;

			switch(conditionType) {
				case SMALLER:
					passed = points < value;
					break;
				case LARGER:
					passed = points > value;
					break;
				case EQUAL:
					passed = points == value;
					break;
			}

			if(passed) {
				int newPoints;

				if(args.length == 2) {
					String newPointsString = args[1];

					if(!NumberUtils.isNumeric(newPointsString)) {
						Message.CHAT_ENTERINTEGER.send(sender);
						return;
					}

					newPoints = Integer.valueOf(newPointsString);

					if(newPoints < 0) {
						Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
						return;
					}
				}
				else {
					newPoints = Config.GUILD_START_POINTS.getInt();
				}

				guild.setPoints(newPoints);
				TabUtils.refresh(guild);

				count++;
			}
		}
		vars.put(VarKey.COUNT, String.valueOf(count));

		Message.CHAT_ADMIN_GUILD_RESET_POINTS_SUCCESS.clone().vars(vars).send(sender);
	}
}
