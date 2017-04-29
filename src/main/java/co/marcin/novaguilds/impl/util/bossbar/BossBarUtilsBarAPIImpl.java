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

package co.marcin.novaguilds.impl.util.bossbar;

import me.confuser.barapi.BarAPI;
import org.bukkit.entity.Player;

public class BossBarUtilsBarAPIImpl extends AbstractBossBarUtils {
	@Override
	public void setMessage(Player player, String message) {
		BarAPI.setMessage(player, message);
	}

	@Override
	public void setMessage(Player player, String message, float percent) {
		BarAPI.setMessage(player, message, percent);
	}

	@Override
	public void setMessage(Player player, String message, int seconds) {
		BarAPI.setMessage(player, message, seconds);
	}

	@Override
	public boolean hasBar(Player player) {
		return BarAPI.hasBar(player);
	}

	@Override
	public void removeBar(Player player) {
		BarAPI.removeBar(player);
	}

	@Override
	public void setHealth(Player player, float percent) {
		BarAPI.setHealth(player, percent);
	}

	@Override
	public float getHealth(Player player) {
		return BarAPI.getHealth(player);
	}

	@Override
	public String getMessage(Player player) {
		return BarAPI.getMessage(player);
	}
}
