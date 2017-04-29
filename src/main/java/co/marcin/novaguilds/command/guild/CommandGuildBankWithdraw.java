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

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;

public class CommandGuildBankWithdraw extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length != 1) {
			Message.CHAT_GUILD_BANK_ENTERAMOUNT.send(sender);
			return;
		}

		String moneyString = args[0];
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(!nPlayer.hasPermission(GuildPermission.BANK_WITHDRAW)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		if(!NumberUtils.isNumeric(moneyString)) {
			Message.CHAT_ENTERINTEGER.send(sender);
			return;
		}

		double money = Double.parseDouble(moneyString);
		money = NumberUtils.roundOffTo2DecPlaces(money);

		if(money < 0) {
			Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
			return;
		}

		if(guild.getMoney() < money) {
			Message.CHAT_GUILD_BANK_WITHDRAW_NOTENOUGH.send(sender);
			return;
		}

		guild.takeMoney(money);
		nPlayer.addMoney(money);
		Message.CHAT_GUILD_BANK_WITHDRAW_SUCCESS.clone().setVar(VarKey.AMOUNT, money).send(sender);
		TabUtils.refresh(nPlayer.getGuild());
	}
}
