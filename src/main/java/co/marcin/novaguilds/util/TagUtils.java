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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.PreparedTag;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.util.preparedtag.PreparedTagScoreboardImpl;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class TagUtils {
	private TagUtils() {
	}

	/**
	 * Refreshes tag of a player
	 *
	 * @param p target player
	 */
	@SuppressWarnings("deprecation")
	public static void refresh(Player p) {
		if(!Config.TAGAPI_ENABLED.getBoolean()) {
			return;
		}

		Scoreboard board = p.getScoreboard();
		for(Player player : CompatibilityUtils.getOnlinePlayers()) {
			NovaPlayer nPlayerLoop = PlayerManager.getPlayer(player);

			String tName = "ng_" + player.getName();
			if(tName.length() > 16) {
				tName = tName.substring(0, 16);
			}

			Team team = board.getTeam(tName);

			if(team == null) {
				team = board.registerNewTeam(tName);
				team.addPlayer(player);
			}

			//Points
			Objective pointsObjective = board.getObjective("points");
			if(Config.POINTSBELOWNAME.getBoolean()) {
				if(pointsObjective == null) {
					pointsObjective = board.registerNewObjective("points", "dummy");
					pointsObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
					pointsObjective.setDisplayName(Message.MISC_POINTSBELOWNAME.get());
				}

				Score score = pointsObjective.getScore(player);
				score.setScore(nPlayerLoop.getPoints());
			}
			else if(pointsObjective != null) {
				pointsObjective.unregister();
			}

			//set tag
			PreparedTag tag = new PreparedTagScoreboardImpl(PlayerManager.getPlayer(player));
			tag.setTagColorFor(PlayerManager.getPlayer(p));
			team.setPrefix(tag.get());
		}
	}

	/**
	 * Refreshes tags of all players online
	 */
	public static void refresh() {
		for(Player player : CompatibilityUtils.getOnlinePlayers()) {
			refresh(player);
		}
	}

	/**
	 * Refreshes tags of a whole guild
	 *
	 * @param guild target guild
	 */
	public static void refresh(NovaGuild guild) {
		if(guild != null) {
			for(Player player : guild.getOnlinePlayers()) {
				refresh(player);
			}
		}
	}
}
