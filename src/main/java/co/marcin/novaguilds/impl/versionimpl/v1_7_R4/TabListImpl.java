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

package co.marcin.novaguilds.impl.versionimpl.v1_7_R4;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.Packet;
import co.marcin.novaguilds.api.util.packet.PacketEvent;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.impl.basic.AbstractTabList;
import co.marcin.novaguilds.impl.util.AbstractPacketHandler;
import co.marcin.novaguilds.impl.versionimpl.v1_7_R4.packet.PacketPlayOutPlayerInfo;
import co.marcin.novaguilds.util.CompatibilityUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.reflect.PacketSender;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TabListImpl extends AbstractTabList {
	private boolean first = true;

	static {
		new AbstractPacketHandler("PacketPlayOutPlayerInfo", PacketExtension.PacketHandler.Direction.OUT) {
			@Override
			public void handle(PacketEvent event) {
				try {
					PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(event.getPacket());

					if(!packet.getUsername().startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
						event.setCancelled(true);
					}
				}
				catch(IllegalAccessException e) {
					LoggerUtils.exception(e);
				}
			}
		};
	}

	/**
	 * The constructor
	 *
	 * @param nPlayer tablist owner
	 */
	public TabListImpl(NovaPlayer nPlayer) {
		super(nPlayer);
	}

	@Override
	public void send() {
		if(!getPlayer().isOnline()) {
			return;
		}

		Scoreboard scoreboard = getPlayer().getPlayer().getScoreboard();
		List<Packet> packets = new ArrayList<>();
		List<String> oldLines = new ArrayList<>(lines);
		TabUtils.fillVars(this);

		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			line = StringUtils.replaceVarKeyMap(line, getVars());
			line = StringUtils.fixColors(line);

			Team team = scoreboard.getTeam("ngtab_" + i);
			if(team != null) {
				team.unregister();
			}

			team = scoreboard.registerNewTeam("ngtab_" + i);
			String prefix = "", suffix = "";

			//Add order
			StringBuilder colorPrefix = new StringBuilder();
			for(char ic : String.valueOf(i).toCharArray()) {
				colorPrefix.append(ChatColor.COLOR_CHAR).append(ic);
			}

			//Reset the color
			boolean startsWithColor = line.length() > 1 && line.charAt(0) == ChatColor.COLOR_CHAR && ChatColor.getByChar(line.charAt(1)) != null;
			if(!startsWithColor && !line.isEmpty()) {
				colorPrefix.append(ChatColor.COLOR_CHAR).append("r");
			}

			int maxLineLength = 16 - (startsWithColor ? 0 : 2) - (i >= 10 ? 2 : 0) - 2;
			if(line.length() > maxLineLength) {
				int prefixCut = line.length() > 16 ? 16 : line.length();
				prefix = line.substring(0, prefixCut);
				line = line.substring(prefixCut);
				String lastColorsPrefix = ChatColor.getLastColors(prefix);

				//Change color reset to the color char of the prefix
				if(lastColorsPrefix.length() > 0) {
					maxLineLength -= 2;
					colorPrefix.append(ChatColor.COLOR_CHAR).append(lastColorsPrefix.charAt(1));
				}

				if(line.length() > maxLineLength) {
					suffix = line.substring(maxLineLength);
					line = line.substring(0, maxLineLength);

					//Add the color to the suffix
					String lastColors = ChatColor.getLastColors(line);
					if(lastColors.length() > 0) {
						suffix = lastColors + suffix;
					}

					if(suffix.length() > 16) {
						suffix = suffix.substring(0, 16);
					}
				}
			}

			line = colorPrefix + line;
			team.setPrefix(prefix);
			team.setSuffix(suffix);
			CompatibilityUtils.addTeamEntry(team, line);
			lines.set(i, line);
		}

		try {
			if(!first) {
				for(String line : oldLines) {
					packets.add(new PacketPlayOutPlayerInfo(line, PacketPlayOutPlayerInfo.Action.REMOVE_PLAYER, 0));
				}
			}
			else {
				first = false;
			}

			for(int i = 0; i < 20; i++) {
				for(int x = 0; x < 3; x++) {
					String line = i < lines.size() ? lines.get(i + x*20) : "";
					packets.add(new PacketPlayOutPlayerInfo(line, PacketPlayOutPlayerInfo.Action.ADD_PLAYER, 0));
				}
			}


			PacketSender.sendPacket(getPlayer().getPlayer(), packets.toArray());
		}
		catch(IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
			LoggerUtils.exception(e);
		}
	}
}
