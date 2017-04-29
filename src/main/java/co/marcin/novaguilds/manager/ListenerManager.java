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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.impl.util.logging.LoggedPluginManager;
import co.marcin.novaguilds.listener.ChatListener;
import co.marcin.novaguilds.listener.ChestGUIListener;
import co.marcin.novaguilds.listener.DeathListener;
import co.marcin.novaguilds.listener.LoginListener;
import co.marcin.novaguilds.listener.MoveListener;
import co.marcin.novaguilds.listener.PacketListener;
import co.marcin.novaguilds.listener.PlayerInfoListener;
import co.marcin.novaguilds.listener.PvpListener;
import co.marcin.novaguilds.listener.RegionInteractListener;
import co.marcin.novaguilds.listener.ToolListener;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.event.Event;

public class ListenerManager {
	private PacketListener packetListener;
	private static final LoggedPluginManager loggedPluginManager = new LoggedPluginManager() {
		@Override
		protected void customHandler(Event event, Throwable e) {
			LoggerUtils.exception(e);
		}
	};

	/**
	 * Returns the packet listener
	 *
	 * @return packet listener
	 */
	public PacketListener getPacketListener() {
		return packetListener;
	}

	/**
	 * Registers the listeners
	 */
	public void registerListeners() {
		new LoginListener();
		new ToolListener();
		new RegionInteractListener();
		new MoveListener();
		new ChatListener();
		new PvpListener();
		new DeathListener();
		new PlayerInfoListener();
		new ChestGUIListener();
		packetListener = new PacketListener();
	}

	/**
	 * Gets PluginManager instance
	 *
	 * @return PluginManager instance
	 */
	public static LoggedPluginManager getLoggedPluginManager() {
		return loggedPluginManager;
	}
}
