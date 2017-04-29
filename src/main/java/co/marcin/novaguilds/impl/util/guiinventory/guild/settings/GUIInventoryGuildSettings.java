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

package co.marcin.novaguilds.impl.util.guiinventory.guild.settings;

import co.marcin.novaguilds.api.util.SignGUI;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.impl.util.signgui.SignGUIPatternImpl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GUIInventoryGuildSettings extends AbstractGUIInventory {

	/**
	 * The constructor
	 */
	public GUIInventoryGuildSettings() {
		super(9, Message.INVENTORY_GUI_SETTINGS_TITLE);
	}

	@Override
	public void generateContent() {
		ItemStack togglePvpItem = (getViewer().getGuild().getFriendlyPvp()
				? Message.INVENTORY_GUI_SETTINGS_ITEM_TOGGLEPVP_ON
				: Message.INVENTORY_GUI_SETTINGS_ITEM_TOGGLEPVP_OFF).getItemStack();

		if(Config.SIGNGUI_ENABLED.getBoolean()
				&& getViewer().hasPermission(GuildPermission.SET_NAME)
				&& Permission.NOVAGUILDS_GUILD_SET_NAME.has(getViewer())) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_SETTINGS_ITEM_SET_NAME) {
				@Override
				public void execute() {
					final SignGUIPatternImpl pattern = new SignGUIPatternImpl(Message.SIGNGUI_GUILD_SETTINGS_SET_NAME.clone().setVar(VarKey.INPUT, getViewer().getGuild().getName()));
					plugin.getSignGUI().open(getViewer().getPlayer(), pattern, new SignGUI.SignGUIListener() {
						@Override
						public void onSignDone(Player player, String[] lines) {
							player.performCommand("novaguilds:guild setname " + lines[pattern.getInputLine()]);
							reopen();
						}
					});
				}
			});
		}

		if(Config.SIGNGUI_ENABLED.getBoolean()
				&& getViewer().hasPermission(GuildPermission.SET_TAG)
				&& Permission.NOVAGUILDS_GUILD_SET_TAG.has(getViewer())) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_SETTINGS_ITEM_SET_TAG) {
				@Override
				public void execute() {
					final SignGUIPatternImpl pattern = new SignGUIPatternImpl(Message.SIGNGUI_GUILD_SETTINGS_SET_TAG.clone().setVar(VarKey.INPUT, getViewer().getGuild().getTag()));
					plugin.getSignGUI().open(getViewer().getPlayer(), pattern, new SignGUI.SignGUIListener() {
						@Override
						public void onSignDone(Player player, String[] lines) {
							player.performCommand("novaguilds:guild settag " + lines[pattern.getInputLine()]);
							reopen();
						}
					});
				}
			});
		}

		if(getViewer().hasPermission(GuildPermission.HOME_SET) && Permission.NOVAGUILDS_GUILD_HOME_SET.has(getViewer())) {
			registerAndAdd(new CommandExecutor(Message.INVENTORY_GUI_SETTINGS_ITEM_SET_HOME, "novaguilds:guild home set", true));
		}

		if(getViewer().hasPermission(GuildPermission.PVPTOGGLE) && Permission.NOVAGUILDS_GUILD_PVPTOGGLE.has(getViewer())) {
			registerAndAdd(new Executor(togglePvpItem) {
				@Override
				public void execute() {
					getViewer().getPlayer().performCommand("novaguilds:guild pvp");
					regenerate();
				}
			});
		}

		if(getViewer().hasPermission(GuildPermission.OPENINVITATION) && Permission.NOVAGUILDS_GUILD_OPENINVITATION.has(getViewer())) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_SETTINGS_ITEM_OPENINVITATION
					.clone()
					.setVar(VarKey.FLAG, Message.getOnOff(getViewer().getGuild().isOpenInvitation()))) {
				@Override
				public void execute() {
					getViewer().getPlayer().performCommand("novaguilds:guild openinv");
					regenerate();
				}
			});
		}

		if(getViewer().hasPermission(GuildPermission.BUYLIFE)
				&& Permission.NOVAGUILDS_GUILD_BUYLIFE.has(getViewer())
				&& getViewer().getGuild().getLives() < Config.GUILD_LIVES_MAX.getInt()) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_SETTINGS_ITEM_BUYLIFE) {
				@Override
				public void execute() {
					getViewer().getPlayer().performCommand("novaguilds:guild buylife");
					regenerate();
				}
			});
		}

		if(getViewer().hasPermission(GuildPermission.BUYSLOT)
				&& Permission.NOVAGUILDS_GUILD_BUYSLOT.has(getViewer())
				&& getViewer().getGuild().getSlots() < Config.GUILD_SLOTS_MAX.getInt()) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_SETTINGS_ITEM_BUYSLOT) {
				@Override
				public void execute() {
					getViewer().getPlayer().performCommand("novaguilds:guild buyslot");
					regenerate();
				}
			});
		}

		if(getViewer().hasPermission(GuildPermission.INVITE) && Permission.NOVAGUILDS_GUILD_INVITE.has(getViewer())) {
			registerAndAdd(new Executor(Message.INVENTORY_GUI_SETTINGS_ITEM_INVITE) {
				@Override
				public void execute() {
					new GUIInventoryGuildInvite().open(getViewer());
				}
			});
		}
	}
}
