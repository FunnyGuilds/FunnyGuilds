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

package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.PlayerManager;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.VariableReplacer;
import org.bukkit.entity.Player;

public class ScoreboardStatsHook {
	/**
	 * The constructor
	 */
	public ScoreboardStatsHook() {
		final NovaGuilds plugin = NovaGuilds.getInstance();
		ScoreboardStats scoreboardStats = plugin.getDependencyManager().get(Dependency.SCOREBOARDSTATS, ScoreboardStats.class);
		scoreboardStats.getReplaceManager().register(new VariableReplacer() {
			@Override
			public void onReplace(Player player, String s, ReplaceEvent replaceEvent) {
				if(!s.startsWith("ng_")) {
					return;
				}

				NovaPlayer nPlayer = PlayerManager.getPlayer(player);
				int value = 0;

				try {
					VarKey varKey = VarKey.valueOf(s.replaceFirst("ng_", "").toUpperCase());

					if(nPlayer.hasGuild()) {
						NovaGuild guild = nPlayer.getGuild();

						switch(varKey) {
							case GUILD_MONEY:
								value = (int) guild.getMoney();
								break;
							case GUILD_LIVES:
								value = guild.getLives();
								break;
							case GUILD_SLOTS:
								value = guild.getSlots();
								break;
							case GUILD_POINTS:
								value = guild.getPoints();
								break;
							case GUILD_RAIDPROGRESS:
								if(guild.isRaid()) {
									value = (int) guild.getRaid().getProgress();
								}
								break;
							case GUILD_PLAYERS_ONLINE:
								value = guild.getOnlinePlayers().size();
								break;
							case GUILD_PLAYERS_MAX:
								value = guild.getPlayers().size();
								break;
						}
					}

					switch(varKey) {
						case PLAYER_KILLS:
							value = nPlayer.getKills();
							break;
						case PLAYER_DEATHS:
							value = nPlayer.getDeaths();
							break;
					}

					replaceEvent.setScore(value);
				}
				catch(IllegalArgumentException ignored) {

				}
			}
		},
				plugin,
				"ng_guild_money",
				"ng_guild_lives",
				"ng_guild_slots",
				"ng_guild_points",
				"ng_guild_raidprogress",
				"ng_guild_players_online",
				"ng_guild_players_max",
				"ng_player_kills",
				"ng_player_deaths");
	}
}
