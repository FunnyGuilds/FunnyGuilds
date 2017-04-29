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

package co.marcin.novaguilds.impl.util.signgui;

import co.marcin.novaguilds.api.util.SignGUI;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSignGui implements SignGUI {
	protected final Map<UUID, SignGUI.SignGUIListener> listeners = new ConcurrentHashMap<>();
	protected final Map<UUID, Location> signLocations = new ConcurrentHashMap<>();

	@Override
	public void destroy() {
		listeners.clear();
		signLocations.clear();
	}

	@Override
	public Map<UUID, SignGUI.SignGUIListener> getListeners() {
		return listeners;
	}

	@Override
	public Map<UUID, Location> getSignLocations() {
		return signLocations;
	}

	@Override
	public void open(Player player, SignGUIPattern signGUIPattern, SignGUIListener response) {
		open(player, signGUIPattern.get(), response);
	}
}
