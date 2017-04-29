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

package co.marcin.novaguilds.impl.util.guiinventory.guild.player;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUIInventoryGuildPlayerSettingsRank extends AbstractGUIInventory {
	private final NovaPlayer nPlayer;

	/**
	 * The constructor
	 *
	 * @param nPlayer player who's rank are being set
	 */
	public GUIInventoryGuildPlayerSettingsRank(NovaPlayer nPlayer) {
		super(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_RANKS_TITLE);
		this.nPlayer = nPlayer;
	}

	@Override
	public void generateContent() {
		NovaGuild guild = nPlayer.getGuild();

		final List<NovaRank> ranks = new ArrayList<>();
		ranks.addAll(NovaGuilds.getInstance().getRankManager().getGenericRanks());
		ranks.addAll(guild.getRanks());

		for(final NovaRank rank : ranks) {
			if(guild.getCloneOfGenericRank(rank) != null) {
				continue;
			}

			ItemStack itemStack = Message.INVENTORY_GUI_RANKS_ROWITEM.setVar(VarKey.RANKNAME, StringUtils.replace(rank.getName(), " ", "_")).getItemStack();

			registerAndAdd(new Executor(itemStack) {
				@Override
				public void execute() {
					nPlayer.setGuildRank(rank);
					close();
				}
			});
		}
	}
}
