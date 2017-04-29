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

import co.marcin.novaguilds.enums.Config;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarUtilsBukkitImpl extends AbstractBossBarUtils {
	private final Map<UUID, BossBar> bossBars = new HashMap<>();

	/**
	 * Creates a boss bar if doesn't exist
	 *
	 * @param player the player
	 * @return the boss bar
	 */
	private BossBar createIfNotExists(Player player) {
		if(bossBars.containsKey(player.getUniqueId())) {
			return getBossBar(player);
		}

		BossBar bossBar = Bukkit.getServer().createBossBar("", Config.BOSSBAR_RAIDBAR_COLOR.toEnum(BarColor.class), Config.BOSSBAR_RAIDBAR_STYLE.toEnum(BarStyle.class));
		bossBar.addPlayer(player);
		bossBars.put(player.getUniqueId(), bossBar);
		return bossBar;
	}

	/**
	 * Gets the boss bar of a player
	 *
	 * @param player the player
	 * @return the boss bar
	 */
	private BossBar getBossBar(Player player) {
		return bossBars.get(player.getUniqueId());
	}

	@Override
	public void setMessage(Player player, String message) {
		BossBar bar = createIfNotExists(player);
		bar.setTitle(message);
	}

	@Override
	public void setMessage(Player player, String message, float percent) {
		BossBar bar = createIfNotExists(player);
		bar.setTitle(message);
		bar.setProgress(percent / 100.0F);
	}

	@Override
	public void setMessage(Player player, String message, int seconds) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public boolean hasBar(Player player) {
		return bossBars.containsKey(player.getUniqueId());
	}

	@Override
	public void removeBar(Player player) {
		if(getBossBar(player) == null) {
			return;
		}

		getBossBar(player).removeAll();
		bossBars.remove(player.getUniqueId());
	}

	@Override
	public void setHealth(Player player, float percent) {
		getBossBar(player).setProgress(percent);
	}

	@Override
	public float getHealth(Player player) {
		return (float) getBossBar(player).getProgress();
	}

	@Override
	public String getMessage(Player player) {
		return getBossBar(player).getTitle();
	}
}
