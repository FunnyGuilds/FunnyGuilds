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

package co.marcin.novaguilds.command.admin.player;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;

/**
 * Sets player points
 * Can add or subtract points by passing
 * +=X or -=X as the first argument
 * Sets the points to given value when
 * entered an integer
 */
public class CommandAdminPlayerSetPoints extends AbstractCommandExecutor.Reversed<NovaPlayer> {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = getParameter();

		if(args.length != 1) { //no new name
			getCommand().getUsageMessage().send(sender);
			return;
		}

		String points = args[0];
		int pointsInteger = 0;

		boolean subtract = points.startsWith("-=");
		if(points.startsWith("+=") || subtract) {
			pointsInteger = nPlayer.getPoints();
			points = points.substring(2);

			if(subtract) {
				points = "-" + points;
			}
		}

		if(!NumberUtils.isNumeric(points)) {
			Message.CHAT_ENTERINTEGER.send(sender);
			return;
		}

		pointsInteger += Integer.parseInt(points);

		if(pointsInteger < 0) {
			Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
			return;
		}

		nPlayer.setPoints(pointsInteger);
		TabUtils.refresh(nPlayer);
		Message.CHAT_ADMIN_PLAYER_SET_POINTS
				.clone()
				.setVar(VarKey.PLAYER_NAME, nPlayer.getName())
				.setVar(VarKey.PLAYER_POINTS, nPlayer.getPoints())
				.send(sender);
	}
}
