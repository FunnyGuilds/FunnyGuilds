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

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class NovaRaidImpl implements NovaRaid {
	private NovaGuild guildAttacker;
	private NovaGuild guildDefender;
	private final long startTime = NumberUtils.systemSeconds();
	private long inactiveTime = NumberUtils.systemSeconds();
	private int killsAttacker;
	private int killsDefender;
	private float progress;
	private final List<NovaPlayer> playersOccupying = new ArrayList<>();
	private Result result = Result.DURING;

	/**
	 * The constructor
	 *
	 * @param guildAttacker attacker guild
	 * @param guildDefender defender guild
	 */
	public NovaRaidImpl(NovaGuild guildAttacker, NovaGuild guildDefender) {
		this.guildAttacker = guildAttacker;
		this.guildDefender = guildDefender;
	}

	@Override
	public NovaGuild getGuildAttacker() {
		return guildAttacker;
	}

	@Override
	public NovaGuild getGuildDefender() {
		return guildDefender;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public int getKillsAttacker() {
		return killsAttacker;
	}

	@Override
	public int getKillsDefender() {
		return killsDefender;
	}

	@Override
	public float getProgress() {
		return progress;
	}

	@Override
	public List<NovaPlayer> getPlayersOccupying() {
		return playersOccupying;
	}

	@Override
	public Result getResult() {
		return result;
	}

	@Override
	public long getInactiveTime() {
		return inactiveTime;
	}

	@Override
	public void setGuildAttacker(NovaGuild guild) {
		guildAttacker = guild;
	}

	@Override
	public void setGuildDefender(NovaGuild guild) {
		guildDefender = guild;
	}

	@Override
	public void addKillAttacker() {
		killsAttacker++;
	}

	@Override
	public void addKillDefender() {
		killsDefender++;
	}

	@Override
	public void resetProgress() {
		progress = 0;
	}

	@Override
	public boolean isProgressFinished() {
		return progress >= 100;
	}

	@Override
	public void addProgress(float progress) {
		this.progress += progress;

		if(this.progress > 100) {
			this.progress = 100;
		}
	}

	@Override
	public void setResult(Result result) {
		this.result = result;
	}

	@Override
	public void updateInactiveTime() {
		inactiveTime = NumberUtils.systemSeconds();
	}

	@Override
	public void addPlayerOccupying(NovaPlayer nPlayer) {
		if(!playersOccupying.contains(nPlayer)) {
			playersOccupying.add(nPlayer);
		}
	}

	@Override
	public void removePlayerOccupying(NovaPlayer nPlayer) {
		playersOccupying.remove(nPlayer);
	}
}
