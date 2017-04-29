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
import co.marcin.novaguilds.manager.ConfigManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class BossBarUtilsBossBarImpl extends AbstractBossBarUtils {
	protected static final boolean v1_9 = ConfigManager.getServerVersion().isNewerThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R2);
	private final Map<UUID, BossBar> bossBarMap = new HashMap<>();

	/**
	 * Creates a boss bar if doesn't exist
	 *
	 * @param player the player
	 * @return the boss bar
	 */
	private BossBar createIfNotExists(Player player) {
		if(!v1_9) {
			return null;
		}

		if(hasBar(player)) {
			return getBossBar(player);
		}

		BossBar bossBar = BossBarAPI.addBar(player, new TextComponent(""), Config.BOSSBAR_RAIDBAR_COLOR.toEnum(BossBarAPI.Color.class), Config.BOSSBAR_RAIDBAR_STYLE.toEnum(BossBarAPI.Style.class), 0);

		bossBarMap.put(player.getUniqueId(), bossBar);
		return bossBar;
	}

	/**
	 * Gets the boss bar of a player
	 *
	 * @param player the player
	 * @return the boss bar
	 */
	private BossBar getBossBar(Player player) {
		return bossBarMap.get(player.getUniqueId());
	}

	@Override
	public void setMessage(Player player, String message) {
		setMessage(player, message, 100F);
	}

	@Override
	public void setMessage(Player player, String message, float percent) {
		if(v1_9) {
			BossBar bar = createIfNotExists(player);
			bar.setMessage(new TextComponent(message).toString());
			bar.setProgress(percent / 100F);
		}
		else {
			BossBarAPI.setMessage(player, message, percent);
		}
	}

	@Override
	public void setMessage(Player player, String message, int seconds) {
		throw new UnsupportedOperationException("Not supported yet");
	}

	@Override
	public boolean hasBar(Player player) {
		return BossBarAPI.hasBar(player) || bossBarMap.containsKey(player.getUniqueId());
	}

	@Override
	public void removeBar(Player player) {
		if(v1_9) {
			BossBar bar = bossBarMap.remove(player.getUniqueId());
			bar.removePlayer(player);
		}
		else {
			BossBarAPI.removeBar(player);
		}
	}

	@Override
	public void setHealth(Player player, float percent) {
		if(v1_9) {
			createIfNotExists(player).setProgress(percent / 100F);
		}
		else {
			BossBarAPI.setHealth(player, percent);
		}
	}

	@Override
	public float getHealth(Player player) {
		return hasBar(player) ? v1_9 ? getBossBar(player).getProgress() : BossBarAPI.getHealth(player) : 0;
	}

	@Override
	public String getMessage(Player player) {
		return hasBar(player) ? v1_9 ? getBossBar(player).getMessage() : BossBarAPI.getMessage(player) : "";
	}
}
