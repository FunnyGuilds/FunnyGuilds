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

import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.impl.util.guiinventory.guild.GUIInventoryGuildJoin;
import co.marcin.novaguilds.impl.util.guiinventory.guild.player.GUIInventoryGuildPlayersList;
import co.marcin.novaguilds.impl.util.guiinventory.guild.rank.GUIInventoryGuildRankList;
import co.marcin.novaguilds.impl.util.guiinventory.guild.settings.GUIInventoryGuildSettings;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIInventoryGuildMenu extends AbstractGUIInventory {
	/**
	 * The constructor
	 */
	public GUIInventoryGuildMenu() {
		super(9, Message.INVENTORY_GGUI_NAME);
	}

	@Override
	public void generateContent() {
		ItemStack topItemStack = Message.INVENTORY_GUI_GUILDTOP.getItemStack();
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(topItemStack.getType());
		meta.setDisplayName(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
		meta.setLore(plugin.getGuildManager().getTopGuilds());
		topItemStack.setItemMeta(meta);
		registerAndAdd(new EmptyExecutor(topItemStack));

		if(getViewer().hasGuild()) {
			registerAndAdd(new CommandExecutor(Message.INVENTORY_GUI_HOMETP, "novaguilds:guild home", true));

			registerAndAdd(new Executor(Message.INVENTORY_GUI_PLAYERSLIST_ICONITEM) {
				@Override
				public void execute() {
					new GUIInventoryGuildPlayersList(getViewer().getGuild()).open(getViewer());
				}
			});

			if(Config.RANK_GUI.getBoolean()
					&& (getViewer().hasPermission(GuildPermission.RANK_EDIT) && Permission.NOVAGUILDS_GUILD_RANK_EDIT.has(getViewer()) || Permission.NOVAGUILDS_ADMIN_GUILD_RANK_EDIT.has(getViewer()))) {
				registerAndAdd(new Executor(Message.INVENTORY_GUI_RANKS_ICONITEM) {
					@Override
					public void execute() {
						new GUIInventoryGuildRankList(getViewer().getGuild()).open(getViewer());
					}
				});
			}

			registerAndAdd(new Executor(Message.INVENTORY_GUI_SETTINGS_ITEM_ICON) {
				@Override
				public void execute() {
					new GUIInventoryGuildSettings().open(getViewer());
				}
			});
		}
		else {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_JOIN_ICONITEM) {
				@Override
				public void execute() {
					new GUIInventoryGuildJoin(getViewer().getInvitedTo()).open(getViewer());
				}
			});
		}
	}
}
