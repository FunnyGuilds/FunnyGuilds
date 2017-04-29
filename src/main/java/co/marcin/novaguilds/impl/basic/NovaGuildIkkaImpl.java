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

package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.api.basic.NovaPlayer;

import java.util.UUID;

/**
 * This class extends NovaGuildImpl
 * To provide a system of guild points
 * based on the sum of players' points
 */
public class NovaGuildIkkaImpl extends NovaGuildImpl {
	/**
	 * The constructor
	 *
	 * @param uuid guild uid
	 */
	public NovaGuildIkkaImpl(UUID uuid) {
		super(uuid);
	}

	/**
	 * The constructor
	 *
	 * @param uuid           guild uid
	 * @param loadingWrapper loading wrapper
	 */
	public NovaGuildIkkaImpl(UUID uuid, LoadingWrapper loadingWrapper) {
		super(uuid, loadingWrapper);
	}

	@Override
	public int getPoints() {
		int points = 0;

		for(NovaPlayer nPlayer : getPlayers()) {
			points += nPlayer.getPoints();
		}

		return points;
	}

	@Override
	public void setPoints(int points) {

	}
}
