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

package co.marcin.novaguilds.impl.util.guiinventory.guild.rank;

import co.marcin.novaguilds.api.basic.GUIInventory;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.util.SignGUI;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaRankImpl;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.impl.util.guiinventory.GUIInventoryGuildPermissionSelect;
import co.marcin.novaguilds.impl.util.signgui.SignGUIPatternImpl;
import co.marcin.novaguilds.manager.RankManager;
import co.marcin.novaguilds.util.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GUIInventoryGuildRankSettings extends AbstractGUIInventory {
	private final NovaRank rank;

	/**
	 * The constructor
	 *
	 * @param rank the rank
	 */
	public GUIInventoryGuildRankSettings(NovaRank rank) {
		super(9, Message.INVENTORY_GUI_RANK_SETTINGS_TITLE.clone().setVar(VarKey.RANKNAME, rank.getName()));
		this.rank = rank;
	}

	@Override
	public void generateContent() {
		if(!rank.isGeneric()
				&& (getViewer().hasPermission(GuildPermission.RANK_EDIT) && Permission.NOVAGUILDS_GUILD_RANK_EDIT.has(getViewer()) || Permission.NOVAGUILDS_ADMIN_GUILD_RANK_EDIT.has(getViewer()))) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_EDITPERMISSIONS) {
				@Override
				public void execute() {
					new GUIInventoryGuildPermissionSelect(rank).open(getViewer());
				}
			});
		}

		if((rank.isGeneric() || !rank.equals(getGuild().getDefaultRank()))
				&& !RankManager.getLeaderRank().equals(rank)
				&& (getViewer().hasPermission(GuildPermission.RANK_EDIT) && Permission.NOVAGUILDS_GUILD_RANK_EDIT.has(getViewer()) || Permission.NOVAGUILDS_ADMIN_GUILD_RANK_EDIT.has(getViewer()))) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_SETDEFAULT.getItemStack()) {
				@Override
				public void execute() {
					NovaRank clonedRank = rank;
					if(rank.isGeneric()) {
						clonedRank = cloneRank();
					}
					else {
						rank.setDefault(false);
					}

					if(!getGuild().getDefaultRank().isGeneric()) {
						getGuild().getDefaultRank().setDefault(false);
					}

					clonedRank.setDefault(true);

					if(rank.isGeneric()) {
						close();
						new GUIInventoryGuildRankSettings(clonedRank).open(getViewer());
						return;
					}

					regenerate();
				}
			});
		}

		if(!RankManager.getLeaderRank().equals(rank)
				&& getGuild().getRanks().size() < Config.RANK_MAXAMOUNT.getInt()
				&& (getViewer().hasPermission(GuildPermission.RANK_EDIT) && Permission.NOVAGUILDS_GUILD_RANK_EDIT.has(getViewer()) || Permission.NOVAGUILDS_ADMIN_GUILD_RANK_EDIT.has(getViewer()))) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_CLONE) {
				@Override
				public void execute() {
					NovaRank clonedRank = cloneRank();
					close();
					new GUIInventoryGuildRankSettings(clonedRank).open(getViewer());
				}
			});
		}

		if(!rank.isGeneric()) {
			if(Config.SIGNGUI_ENABLED.getBoolean()
					&& (getViewer().hasPermission(GuildPermission.RANK_EDIT) && Permission.NOVAGUILDS_GUILD_RANK_EDIT.has(getViewer()) || Permission.NOVAGUILDS_ADMIN_GUILD_RANK_EDIT.has(getViewer()))) {
				registerAndAdd(new Executor(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_RENAME) {
					@Override
					public void execute() {
						final SignGUIPatternImpl pattern = new SignGUIPatternImpl(Message.SIGNGUI_GUILD_RANKS_SET_NAME.clone().setVar(VarKey.INPUT, rank.getName()));
						plugin.getSignGUI().open(getViewer().getPlayer(), pattern, new SignGUI.SignGUIListener() {
							@Override
							public void onSignDone(Player player, String[] lines) {
								rank.setName(lines[pattern.getInputLine()]);
								close();
								GUIInventoryGuildRankSettings gui = new GUIInventoryGuildRankSettings(rank);
								gui.open(getViewer());
							}
						});
					}
				});
			}

			if(!rank.isDefault()
					&& (getViewer().hasPermission(GuildPermission.RANK_DELETE) && Permission.NOVAGUILDS_GUILD_RANK_DELETE.has(getViewer()) || Permission.NOVAGUILDS_ADMIN_GUILD_RANK_DELETE.has(getViewer()))) {
				registerAndAdd(new Executor(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_DELETE) {
					@Override
					public void execute() {
						rank.delete();
						close();
					}
				});
			}
		}

		if(!GUIInventoryGuildRankMembers.getMembers(getGuild(), rank).isEmpty()) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_MEMBERLIST) {
				@Override
				public void execute() {
					new GUIInventoryGuildRankMembers(getGuild(), rank).open(getViewer());
				}
			});
		}
	}

	/**
	 * Gets the guild
	 *
	 * @return the guild
	 */
	public NovaGuild getGuild() {
		if(rank.isGeneric()) {
			GUIInventory previousGui = getViewer().getGuiInventoryHistory().get(getViewer().getGuiInventoryHistory().size() - 2);

			if(previousGui instanceof GUIInventoryGuildRankList) {
				return ((GUIInventoryGuildRankList) previousGui).getGuild();
			}
			else {
				return getViewer().getGuild();
			}
		}
		else {
			return rank.getGuild();
		}
	}

	/**
	 * Clones a rank
	 *
	 * @return the rank
	 */
	private NovaRank cloneRank() {
		String clonePrefix = Message.INVENTORY_GUI_RANK_SETTINGS_CLONEPREFIX.get();
		String cloneName = rank.getName().startsWith(clonePrefix) || rank.isGeneric() ? rank.getName() : clonePrefix + rank.getName();

		if(StringUtils.contains(cloneName, ' ')) {
			String[] split = StringUtils.split(cloneName, ' ');

			if(NumberUtils.isNumeric(split[split.length - 1])) {
				cloneName = cloneName.substring(0, cloneName.length() - split[split.length - 1].length() - 1);
			}
		}

		NovaRank clone = new NovaRankImpl(rank);
		NovaGuild guild = getGuild();

		boolean doubleName;
		int i = 1;
		do {
			if(i > 999) {
				break;
			}

			doubleName = false;
			for(NovaRank loopRank : guild.getRanks()) {
				if(!loopRank.isGeneric() && loopRank.getName().equalsIgnoreCase(clone.getName())) {
					doubleName = true;
				}
			}

			if(doubleName) {
				clone.setName(cloneName + " " + i);
			}

			i++;
		} while(doubleName);

		guild.addRank(clone);

		//Move players
		for(NovaPlayer nPlayer : new ArrayList<>(rank.getMembers())) {
			nPlayer.setGuildRank(clone);
		}

		return clone;
	}
}
