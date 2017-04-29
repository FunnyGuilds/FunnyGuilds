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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.IBossBarUtils;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.manager.ConfigManager;
import org.bukkit.entity.Player;

public class BossBarUtils {
	private static IBossBarUtils bossBarUtils;

	static {
		if(bossBarUtils == null) {
			AbstractBossBarUtils abstractBossBarUtils = new AbstractBossBarUtils() {
				@Override
				public void setMessage(Player player, String message) {

				}

				@Override
				public void setMessage(Player player, String message, float percent) {

				}

				@Override
				public void setMessage(Player player, String message, int seconds) {

				}

				@Override
				public boolean hasBar(Player player) {
					return false;
				}

				@Override
				public void removeBar(Player player) {

				}

				@Override
				public void setHealth(Player player, float percent) {

				}

				@Override
				public float getHealth(Player player) {
					return 0;
				}

				@Override
				public String getMessage(Player player) {
					return null;
				}
			};

			if(Config.BOSSBAR_ENABLED.getBoolean()) {
				switch(ConfigManager.getServerVersion()) {
					case MINECRAFT_1_7_R3:
					case MINECRAFT_1_7_R4:
					case MINECRAFT_1_8_R1:
					case MINECRAFT_1_8_R2:
					case MINECRAFT_1_8_R3:
						if(NovaGuilds.getInstance().getDependencyManager().isEnabled(Dependency.BOSSBARAPI)) {
							bossBarUtils = new BossBarUtilsBossBarImpl();
						}
						else if(NovaGuilds.getInstance().getDependencyManager().isEnabled(Dependency.BARAPI)) {
							bossBarUtils = new BossBarUtilsBarAPIImpl();
						}
						else {
							bossBarUtils = abstractBossBarUtils;
							Config.BOSSBAR_ENABLED.set(false);
						}
						break;
					case MINECRAFT_1_9_R1:
					case MINECRAFT_1_9_R2:
					case MINECRAFT_1_10_R1:
					case MINECRAFT_1_10_R2:
					case MINECRAFT_1_11_R1:
						bossBarUtils = new BossBarUtilsBukkitImpl();
						break;
					default:
						bossBarUtils = abstractBossBarUtils;
						break;
				}
			}
			else {
				bossBarUtils = abstractBossBarUtils;
			}
		}
	}

	public static void setMessage(String message) {
		bossBarUtils.setMessage(message);
	}

	public static void setMessage(Player player, String message) {
		bossBarUtils.setMessage(player, message);
	}

	public static void setMessage(String message, float percent) {
		bossBarUtils.setMessage(message, percent);
	}

	public static void setMessage(Player player, String message, float percent) {
		bossBarUtils.setMessage(player, message, percent);
	}

	public static void setMessage(String message, int seconds) {
		bossBarUtils.setMessage(message, seconds);
	}

	public static void setMessage(Player player, String message, int seconds) {
		bossBarUtils.setMessage(player, message, seconds);
	}

	public static boolean hasBar(Player player) {
		return bossBarUtils.hasBar(player);
	}

	public static void removeBar(Player player) {
		if(hasBar(player)) {
			bossBarUtils.removeBar(player);
		}
	}

	public static void setHealth(Player player, float percent) {
		bossBarUtils.setHealth(player, percent);
	}

	public static float getHealth(Player player) {
		return bossBarUtils.getHealth(player);
	}

	public static String getMessage(Player player) {
		return bossBarUtils.getMessage(player);
	}
}
