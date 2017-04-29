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

package co.marcin.novaguilds.impl.util.guiinventory;

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;

public class GUIInventoryGuildPermissionSelect extends AbstractGUIInventory {
	private final NovaRank rank;

	/**
	 * The constructor
	 *
	 * @param rank the rank
	 */
	public GUIInventoryGuildPermissionSelect(NovaRank rank) {
		super(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_PERMISSIONS_TITLE.clone().setVar(VarKey.RANKNAME, rank.getName()));
		this.rank = rank;
	}

	@Override
	public void generateContent() {
		final MessageWrapper messageWrapperEnabled = Message.INVENTORY_GUI_PERMISSIONS_ITEM_ENABLED;
		final MessageWrapper messageWrapperDisabled = Message.INVENTORY_GUI_PERMISSIONS_ITEM_DISABLED;

		for(final GuildPermission perm : GuildPermission.values()) {
			MessageWrapper message;

			if(rank.hasPermission(perm)) {
				message = messageWrapperEnabled;
			}
			else {
				message = messageWrapperDisabled;
			}

			registerAndAdd(new Executor(message
					.clone()
					.setVar(VarKey.PERMNAME, Message.valueOf("INVENTORY_GUI_PERMISSIONS_NAMES_" + perm.name()).get())
					.getItemStack()) {
				@Override
				public void execute() {
					if(rank.hasPermission(perm)) {
						rank.removePermission(perm);
					}
					else {
						rank.addPermission(perm);
					}

					reopen();
				}
			});
		}
	}
}
