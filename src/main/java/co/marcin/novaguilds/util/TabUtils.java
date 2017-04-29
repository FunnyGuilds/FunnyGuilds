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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.ConfigWrapper;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.TabList;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class TabUtils {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();

	private TabUtils() {

	}

	/**
	 * Refreshes player's tab
	 *
	 * @param nPlayer target player
	 */
	public static void refresh(final NovaPlayer nPlayer) {
		if(!Config.TABLIST_ENABLED.getBoolean()) {
			return;
		}

		if(!nPlayer.hasTabList()) {
			return;
		}

		if(Bukkit.isPrimaryThread()) {
			new Thread() {
				@Override
				public void run() {
					nPlayer.getTabList().send();
				}
			}.start();
		}
		else {
			nPlayer.getTabList().send();
		}
	}

	/**
	 * Refreshes player's tab
	 *
	 * @param player target player
	 */
	public static void refresh(Player player) {
		refresh(PlayerManager.getPlayer(player));
	}

	/**
	 * Refreshes all players' tabs
	 */
	public static void refresh() {
		refresh(new ArrayList<>(plugin.getPlayerManager().getOnlinePlayers()));
	}

	/**
	 * Refreshes tabs of all guild's members
	 *
	 * @param guild target guild
	 */
	public static void refresh(NovaGuild guild) {
		refresh(guild.getPlayers());
	}

	/**
	 * Refreshes tabs of players from a list
	 *
	 * @param list list of players
	 */
	public static void refresh(final List<NovaPlayer> list) {
		new Thread() {
			@Override
			public void run() {
				for(NovaPlayer nPlayer : list) {
					refresh(nPlayer);
				}
			}
		}.start();
	}

	/**
	 * Fills variables in a tablist
	 *
	 * @param tabList tablist
	 */
	@SuppressWarnings("deprecation")
	public static void fillVars(TabList tabList) {
		NovaPlayer nPlayer = tabList.getPlayer();
		Map<VarKey, String> vars = tabList.getVars();
		tabList.clear();

		//Online players excluding vanished
		int onlinePlayersCount = 0;
		for(Player player : CompatibilityUtils.getOnlinePlayers()) {
			if(!plugin.getPlayerManager().isVanished(player)) {
				onlinePlayersCount++;
			}
		}

		//Server vars
		vars.put(VarKey.SERVER_ONLINE, String.valueOf(onlinePlayersCount));
		vars.put(VarKey.SERVER_ONLINE_ALL, String.valueOf(CompatibilityUtils.getOnlinePlayers().size())); //Including vanished players
		vars.put(VarKey.SERVER_MAX, String.valueOf(Bukkit.getMaxPlayers()));

		//Time
		Date date = Calendar.getInstance().getTime();
		vars.put(VarKey.DATE_YEAR, String.valueOf(1900 + date.getYear()));
		vars.put(VarKey.DATE_MONTH, String.valueOf((date.getMonth() < 10 ? "0" : "") + date.getMonth()));
		vars.put(VarKey.DATE_DAY, String.valueOf((date.getDay() < 10 ? "0" : "") + date.getDay()));
		vars.put(VarKey.DATE_HOURS, String.valueOf((date.getHours() < 10 ? "0" : "") + date.getHours()));
		vars.put(VarKey.DATE_MINUTES, String.valueOf((date.getMinutes() < 10 ? "0" : "") + date.getMinutes()));
		vars.put(VarKey.DATE_SECONDS, String.valueOf((date.getSeconds() < 10 ? "0" : "") + date.getSeconds()));

		//World vars
		if(nPlayer.isOnline()) {
			World world = Bukkit.getWorlds().get(0);
			vars.put(VarKey.WORLD_NAME, world.getName());
			vars.put(VarKey.WORLD_SPAWN, Message.getCoords3D(world.getSpawnLocation()).get());
		}

		//Player vars
		vars.put(VarKey.PLAYER_NAME, nPlayer.getName());
		vars.put(VarKey.PLAYER_BALANCE, String.valueOf(NumberUtils.roundOffTo2DecPlaces(nPlayer.getMoney())));
		vars.put(VarKey.PLAYER_KILLS, String.valueOf(nPlayer.getKills()));
		vars.put(VarKey.PLAYER_DEATHS, String.valueOf(nPlayer.getDeaths()));
		vars.put(VarKey.PLAYER_KDR, String.valueOf(nPlayer.getKillDeathRate()));
		vars.put(VarKey.PLAYER_CHATMODE, nPlayer.getPreferences().getChatMode().getName().get());
		vars.put(VarKey.PLAYER_SPYMODE, Message.getOnOff(nPlayer.getPreferences().getSpyMode()));
		vars.put(VarKey.PLAYER_BYPASS, Message.getOnOff(nPlayer.getPreferences().getBypass()));
		vars.put(VarKey.PLAYER_POINTS, String.valueOf(nPlayer.getPoints()));

		//Guild vars
		NovaGuild guild = nPlayer.getGuild();
		String guildName, guildTag, guildPlayersOnline, guildPlayersMax, guildLives, guildTimeRegen, guildRaidProgress, guildPvp, guildMoney, guildPoints, guildSlots = "";
		String guildTimeRest, guildTimeCreated, guildHomeCoordinates, guildOpenInvitation, guildTimeProtection = "";
		guildName = guildTag = guildPlayersOnline = guildPlayersMax = guildLives = guildTimeRegen = guildRaidProgress = guildPvp = guildMoney = guildPoints = guildSlots;
		guildTimeRest = guildTimeCreated = guildHomeCoordinates = guildOpenInvitation = guildTimeProtection;

		if(nPlayer.hasGuild()) {
			long liveRegenerationTime = Config.LIVEREGENERATION_REGENTIME.getSeconds() - (NumberUtils.systemSeconds() - guild.getLostLiveTime());
			long createdTime = NumberUtils.systemSeconds() - guild.getTimeCreated();
			long restTime = Config.RAID_TIMEREST.getSeconds() - (NumberUtils.systemSeconds() - guild.getTimeRest());
			long timeProtection = Config.GUILD_CREATEPROTECTION.getSeconds() - createdTime;

			guildName = guild.getName();
			guildTag = guild.getTag();
			guildPlayersOnline = String.valueOf(guild.getOnlinePlayers().size());
			guildPlayersMax = String.valueOf(guild.getPlayers().size());
			guildLives = String.valueOf(guild.getLives());
			guildRaidProgress = guild.isRaid() ? String.valueOf(guild.getRaid().getProgress()) : "";
			guildPvp = Message.getOnOff(guild.getFriendlyPvp());
			guildMoney = String.valueOf(guild.getMoney());
			guildPoints = String.valueOf(guild.getPoints());
			guildSlots = String.valueOf(guild.getSlots());
			guildTimeRegen = StringUtils.secondsToString(liveRegenerationTime, TimeUnit.HOURS);
			guildTimeRest = StringUtils.secondsToString(restTime, TimeUnit.HOURS);
			guildTimeCreated = StringUtils.secondsToString(createdTime, TimeUnit.HOURS);
			guildTimeProtection = StringUtils.secondsToString(timeProtection, TimeUnit.HOURS);
			guildHomeCoordinates = Message.getCoords3D(guild.getHome()).get();
			guildOpenInvitation = Message.getOnOff(guild.isOpenInvitation());
		}

		vars.put(VarKey.GUILD_NAME, guildName);
		vars.put(VarKey.GUILD_TAG, guildTag);
		vars.put(VarKey.GUILD_PLAYERS_ONLINE, guildPlayersOnline);
		vars.put(VarKey.GUILD_PLAYERS_MAX, guildPlayersMax);
		vars.put(VarKey.GUILD_LIVES, guildLives);
		vars.put(VarKey.GUILD_RAIDPROGRESS, guildRaidProgress);
		vars.put(VarKey.GUILD_PVP, guildPvp);
		vars.put(VarKey.GUILD_MONEY, guildMoney);
		vars.put(VarKey.GUILD_POINTS, guildPoints);
		vars.put(VarKey.GUILD_SLOTS, guildSlots);
		vars.put(VarKey.GUILD_TIME_REGEN, guildTimeRegen);
		vars.put(VarKey.GUILD_TIME_REST, guildTimeRest);
		vars.put(VarKey.GUILD_TIME_CREATED, guildTimeCreated);
		vars.put(VarKey.GUILD_TIME_PROTECTION, guildTimeProtection);
		vars.put(VarKey.GUILD_HOME, guildHomeCoordinates);
		vars.put(VarKey.GUILD_OPENINVITATION, guildOpenInvitation);

		//Guild TOP
		List<NovaGuild> topGuildsList = plugin.getGuildManager().getTopGuildsByPoints(20);
		for(int i = 1; i <= 20; i++) {
			if(i <= topGuildsList.size()) {
				NovaGuild guildTop = topGuildsList.get(i - 1);
				String row = Config.TABLIST_TOPROW_GUILDS.getString();
				Map<VarKey, String> rowVars = new HashMap<>();
				rowVars.put(VarKey.N, String.valueOf(i));
				rowVars.put(VarKey.GUILD_NAME, guildTop.getName());
				rowVars.put(VarKey.GUILD_TAG, guildTop.getTag());
				rowVars.put(VarKey.GUILD_POINTS, String.valueOf(guildTop.getPoints()));
				row = StringUtils.replaceVarKeyMap(row, rowVars);

				vars.put(VarKey.valueOf("GUILD_TOP_N" + i), row);
			}
			else {
				vars.put(VarKey.valueOf("GUILD_TOP_N" + i), "");
			}
		}

		//Players TOP
		final List<ListDisplay> listDisplays = new ArrayList<>();
		listDisplays.add(new ListDisplay(Config.TABLIST_TOPROW_PLAYERS_POINTS, VarKey.PLAYER_POINTS, plugin.getPlayerManager().getTopPlayersByPoints(20)));
		listDisplays.add(new ListDisplay(Config.TABLIST_TOPROW_PLAYERS_KDR, VarKey.PLAYER_KDR, plugin.getPlayerManager().getTopPlayersByKDR(20)));

		for(ListDisplay listDisplay : listDisplays) {
			List<NovaPlayer> topPlayersList = listDisplay.getList();

			for(int i = 1; i <= 20; i++) {
				if(i <= topPlayersList.size()) {
					NovaPlayer nPlayerTop = topPlayersList.get(i - 1);
					String row = listDisplay.getRowPattern().getString();
					Map<VarKey, String> rowVars = new HashMap<>();
					rowVars.put(VarKey.N, String.valueOf(i));
					rowVars.put(VarKey.PLAYER_NAME, nPlayerTop.getName());
					rowVars.put(VarKey.PLAYER_KILLS, String.valueOf(nPlayerTop.getKills()));
					rowVars.put(VarKey.PLAYER_DEATHS, String.valueOf(nPlayerTop.getDeaths()));
					rowVars.put(VarKey.PLAYER_KDR, String.valueOf(nPlayerTop.getKillDeathRate()));
					rowVars.put(VarKey.PLAYER_POINTS, String.valueOf(nPlayerTop.getPoints()));
					row = StringUtils.replaceVarKeyMap(row, rowVars);

					vars.put(VarKey.valueOf("PLAYER_TOP_" + listDisplay.getVarKey() + "_N" + i), row);
				}
				else {
					vars.put(VarKey.valueOf("PLAYER_TOP_" + listDisplay.getVarKey() + "_N" + i), "");
				}
			}
		}
	}

	private static class ListDisplay {
		private final ConfigWrapper rowPattern;
		private final VarKey varKey;
		private final List<NovaPlayer> list;

		/**
		 * ListDisplay constructor
		 *
		 * @param rowPattern pattern for every row from config
		 * @param varKey     variable key
		 * @param list       list of players
		 */
		ListDisplay(ConfigWrapper rowPattern, VarKey varKey, List<NovaPlayer> list) {
			this.rowPattern = rowPattern;
			this.varKey = varKey;
			this.list = list;
		}

		/**
		 * Gets row pattern as Config enum
		 *
		 * @return the pattern
		 */
		public ConfigWrapper getRowPattern() {
			return rowPattern;
		}

		/**
		 * Returns the var key
		 *
		 * @return the var key
		 */
		public VarKey getVarKey() {
			return varKey;
		}

		/**
		 * Returns player list
		 *
		 * @return the list
		 */
		public List<NovaPlayer> getList() {
			return list;
		}
	}
}
