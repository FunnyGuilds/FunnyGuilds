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

package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.util.CompatibilityUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.entity.Player;

public class RunnableAutoSave implements Runnable {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();

	@Override
	public void run() {
		plugin.getGuildManager().save();
		plugin.getRegionManager().save();
		plugin.getPlayerManager().save();
		plugin.getRankManager().save();
		LoggerUtils.info("Saved data.");

		//send message to admins
		for(Player player : CompatibilityUtils.getOnlinePlayers()) {
			if(Permission.NOVAGUILDS_ADMIN_SAVE_NOTIFY.has(player)) {
				Message.CHAT_ADMIN_SAVE_AUTOSAVE.send(player);
			}
		}
	}
}
