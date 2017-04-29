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

package co.marcin.novaguilds.impl.versionimpl.v1_8_R1;

import co.marcin.novaguilds.api.util.Title;
import co.marcin.novaguilds.impl.util.AbstractTitle;
import co.marcin.novaguilds.impl.versionimpl.v1_8_R1.packet.PacketPlayOutTitle;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.entity.Player;

public class TitleImpl extends AbstractTitle {
	public TitleImpl() {
		super("");
	}

	public TitleImpl(String title) {
		super(title);
	}

	public TitleImpl(String title, String subtitle) {
		super(title, subtitle);
	}

	public TitleImpl(Title title) {
		super(title);
	}

	public TitleImpl(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
		super(title, subtitle, fadeInTime, stayTime, fadeOutTime);
	}

	@Override
	public void send(Player player) {
		resetTitle(player);

		try {
			if(fadeInTime != -1 && fadeOutTime != -1 && stayTime != -1) {
				new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeInTime * (ticks ? 1 : 20), stayTime * (ticks ? 1 : 20), fadeOutTime * (ticks ? 1 : 20)).send(player);
			}

			String titleJson = "{text:\"" + StringUtils.fixColors(title) + "\",color:" + titleColor.name().toLowerCase() + "}";

			new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJson).send(player);

			if(subtitle != null) {
				String subTitleJson = "{text:\"" + StringUtils.fixColors(subtitle) + "\",color:" + subtitleColor.name().toLowerCase() + "}";
				new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subTitleJson).send(player);
			}
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void clearTitle(Player player) {
		try {
			new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.CLEAR, null).send(player);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void resetTitle(Player player) {
		try {
			new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, null).send(player);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}
}
