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

package co.marcin.novaguilds.impl.util.preparedtag;

import co.marcin.novaguilds.api.basic.ConfigWrapper;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;

public class PreparedTagChatImpl extends AbstractPreparedTag {
	private static final ConfigWrapper pattern = Config.CHAT_TAG_CHAT;

	/**
	 * The constructor
	 *
	 * @param guild the guild
	 */
	public PreparedTagChatImpl(NovaGuild guild) {
		super(pattern, guild);
	}

	/**
	 * The constructor
	 *
	 * @param nPlayer the player whose tag is being displayed
	 */
	public PreparedTagChatImpl(NovaPlayer nPlayer) {
		super(pattern, nPlayer.getGuild());
		setUpFor(nPlayer);
	}

	/**
	 * The constructor
	 *
	 * @param nPlayer             the player whose tag is being displayed
	 * @param leaderPrefixEnabled leader prefix enabled
	 */
	public PreparedTagChatImpl(NovaPlayer nPlayer, boolean leaderPrefixEnabled) {
		super(pattern, nPlayer.getGuild(), leaderPrefixEnabled);
		setUpFor(nPlayer);
	}
}
