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

package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.ChatBroadcast;
import co.marcin.novaguilds.api.util.PreparedTag;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.CompatibilityUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChatBroadcastImpl implements ChatBroadcast {
	private final MessageWrapper message;
	private final Map<Integer, PreparedTag> preparedTagMap = new HashMap<>();

	/**
	 * The constructor
	 *
	 * @param message the message
	 */
	public ChatBroadcastImpl(MessageWrapper message) {
		this.message = message.clone();
	}

	@Override
	public void send() {
		for(Player player : CompatibilityUtils.getOnlinePlayers()) {
			for(Map.Entry<Integer, PreparedTag> entry : preparedTagMap.entrySet()) {
				NovaPlayer nPlayer = PlayerManager.getPlayer(player);
				PreparedTag tag = entry.getValue();

				tag.setTagColorFor(nPlayer);
				message.setVar(VarKey.valueOf("TAG" + entry.getKey()), tag.get());
			}

			message.send(player);
		}
	}

	@Override
	public void setTag(Integer index, PreparedTag preparedTag) {
		if(index > 10 || index < 1) {
			throw new IllegalArgumentException("Index must be between 1 and 10");
		}

		preparedTagMap.put(index, preparedTag);
	}

	@Override
	public PreparedTag getTag(Integer index) {
		return preparedTagMap.get(index);
	}

	@Override
	public MessageWrapper getMessage() {
		return message;
	}
}
