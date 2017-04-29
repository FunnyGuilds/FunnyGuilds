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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.api.util.ChatBroadcast;
import co.marcin.novaguilds.api.util.PreparedTag;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.impl.util.preparedtag.PreparedTagChatImpl;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class DeathListener extends AbstractListener {
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player victim = event.getEntity();
		Player attacker = event.getEntity().getKiller();
		NovaPlayer nPlayer = PlayerManager.getPlayer(victim);

		//Exit from region
		if(nPlayer.isAtRegion()) {
			plugin.getRegionManager().playerExitedRegion(nPlayer.getPlayer());
		}

		if(attacker == null || attacker.equals(victim)) {
			return;
		}

		NovaPlayer nPlayerAttacker = PlayerManager.getPlayer(attacker);

		if(nPlayer.isPartRaid() && nPlayerAttacker.isPartRaid() && nPlayer.getPartRaid().equals(nPlayerAttacker.getPartRaid()) && !nPlayer.getGuild().isMember(nPlayerAttacker)) {
			NovaRaid raid = nPlayer.getPartRaid();

			if(raid.getPlayersOccupying().contains(nPlayerAttacker)) {
				raid.addKillAttacker();
			}
			else {
				raid.addKillDefender();
			}
		}

		if(nPlayerAttacker.canGetKillPoints(victim)) {
			PreparedTag preparedTag1 = new PreparedTagChatImpl(nPlayer, false);
			PreparedTag preparedTag2 = new PreparedTagChatImpl(nPlayerAttacker, false);

			Map<VarKey, String> vars = new HashMap<>();
			vars.put(VarKey.PLAYER1, victim.getName());
			vars.put(VarKey.PLAYER2, attacker.getName());

			if(!Message.BROADCAST_PVP_KILLED.isEmpty()) {
				ChatBroadcast chatBroadcast = Message.BROADCAST_PVP_KILLED.clone().vars(vars).newChatBroadcast();
				chatBroadcast.setTag(1, preparedTag1);
				chatBroadcast.setTag(2, preparedTag2);
				chatBroadcast.send();
				event.setDeathMessage(null);
			}

			//Kill and death point
			nPlayerAttacker.addKill();
			nPlayer.addDeath();

			//Guild points
			if(nPlayer.hasGuild()) {
				NovaGuild guildVictim = nPlayer.getGuild();
				guildVictim.takePoints(Config.GUILD_DEATHPOINTS.getInt());
			}

			if(nPlayerAttacker.hasGuild()) {
				NovaGuild guildAttacker = nPlayerAttacker.getGuild();
				guildAttacker.addPoints(Config.GUILD_KILLPOINTS.getInt());
			}

			//Raid bonus percent
			double bonusPercentMoney = 0;
			double bonusPercentPoints = 0;
			if(nPlayer.isPartRaid()) {
				bonusPercentMoney = Config.RAID_PVP_BONUSPERCENT_MONEY.getPercent();
				bonusPercentPoints = Config.RAID_PVP_BONUSPERCENT_POINTS.getPercent();
			}

			//player points
			int points = (int) Math.round(nPlayer.getPoints() * (Config.KILLING_RANKPERCENT.getPercent() + bonusPercentPoints));
			nPlayer.takePoints(points);
			nPlayerAttacker.addPoints(points);
			nPlayerAttacker.addKillHistory(victim);

			//money
			vars.clear();
			vars.put(VarKey.PLAYER_NAME, victim.getName());
			double money;
			if(nPlayer.canGetKillPoints(attacker)) {
				money = NumberUtils.roundOffTo2DecPlaces((Config.KILLING_MONEYFORKILL.getPercent() + bonusPercentMoney) * nPlayer.getMoney());

				if(money > 0) {
					vars.put(VarKey.MONEY, String.valueOf(money));
					Message.CHAT_PLAYER_PVPMONEY_KILL.clone().vars(vars).send(attacker);
				}
			}
			else {
				money = NumberUtils.roundOffTo2DecPlaces((Config.KILLING_MONEYFORREVENGE.getPercent() + bonusPercentMoney) * nPlayer.getMoney());

				if(money > 0) {
					vars.put(VarKey.MONEY, String.valueOf(money));
					Message.CHAT_PLAYER_PVPMONEY_REVENGE.clone().vars(vars).send(attacker);
				}
			}

			if(money > 0) {
				nPlayer.takeMoney(money);
				nPlayerAttacker.addMoney(money);
			}
		}

		//Refresh tab and tag
		TabUtils.refresh(attacker);
		TabUtils.refresh(victim);
		TagUtils.refresh(attacker);
		TagUtils.refresh(victim);
	}
}
