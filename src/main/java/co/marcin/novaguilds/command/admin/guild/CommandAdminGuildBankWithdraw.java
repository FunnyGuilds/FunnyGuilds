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
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminGuildBankWithdraw extends AbstractCommandExecutor.Reversed<NovaGuild> {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaGuild guild = getParameter();

		if(args.length != 1) { //invalid arguments
			getCommand().getUsageMessage().send(sender);
			return;
		}

		String moneyString = args[0];

		if(!NumberUtils.isNumeric(moneyString)) { //money not int
			Message.CHAT_ENTERINTEGER.send(sender);
			return;
		}

		double money = Double.parseDouble(moneyString);

		if(money < 0) {
			Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
			return;
		}

		if(guild.getMoney() < money) { //guild has not enough money
			Message.CHAT_GUILD_BANK_WITHDRAW_NOTENOUGH.send(sender);
			return;
		}

		money = NumberUtils.roundOffTo2DecPlaces(money);

		guild.takeMoney(money);
		TabUtils.refresh(guild);

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.MONEY, moneyString);
		vars.put(VarKey.GUILD_NAME, guild.getName());
		Message.CHAT_ADMIN_GUILD_BANK_WITHDREW.clone().vars(vars).send(sender);
	}
}
