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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaGroupImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CommandGuildEffect extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
		Player player = (Player) sender;

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.hasPermission(GuildPermission.EFFECT)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		//Money
		double money = GroupManager.getGroup(sender).get(NovaGroupImpl.Key.EFFECT_MONEY);
		if(!nPlayer.getGuild().hasMoney(money)) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
		}

		//items
		List<ItemStack> guildEffectItems = GroupManager.getGroup(sender).get(NovaGroupImpl.Key.EFFECT_ITEMS);
		if(!guildEffectItems.isEmpty()) {
			List<ItemStack> missingItems = InventoryUtils.getMissingItems(player.getInventory(), guildEffectItems);

			if(!missingItems.isEmpty()) {
				Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
				sender.sendMessage(StringUtils.getItemList(missingItems));
				return;
			}
		}

		//Generate effect
		List<PotionEffectType> potionEffects = plugin.getConfigManager().getGuildEffects();
		int index = NumberUtils.randInt(0, potionEffects.size() - 1);
		PotionEffectType effectType = potionEffects.get(index);
		PotionEffect effect = effectType.createEffect(Config.GUILD_EFFECT_DURATION.getSeconds() * 20, 1);


		//add effect
		for(Player gPlayer : nPlayer.getGuild().getOnlinePlayers()) {
			if(gPlayer.hasPotionEffect(effectType)) {
				gPlayer.removePotionEffect(effectType);
			}

			gPlayer.addPotionEffect(effect);
		}

		//remove money and items
		nPlayer.getGuild().takeMoney(money);
		InventoryUtils.removeItems(player, guildEffectItems);

		//message
		Message.CHAT_GUILD_EFFECT_SUCCESS.clone().setVar(VarKey.EFFECTTYPE, effectType.getName()).send(sender);
	}
}
