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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.GUIInventory;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.CompatibilityUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestGUIListener extends AbstractListener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = CompatibilityUtils.getClickedInventory(event);
		if(inventory == null
				|| event.getCurrentItem() == null
				|| (!inventory.equals(event.getView().getTopInventory()) && event.getClick() != ClickType.SHIFT_LEFT && event.getClick() != ClickType.SHIFT_RIGHT)) {
			return;
		}

		Player player = (Player) event.getWhoClicked();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		GUIInventory guiInventory = nPlayer.getGuiInventory();

		if(guiInventory != null) {
			event.setCancelled(true);

			if(event.getSlot() == inventory.getSize() - 1 && event.getCurrentItem().equals(Message.INVENTORY_GUI_BACK.getItemStack())) {
				player.closeInventory();
				return;
			}

			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				guiInventory.onClick(event);
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		final NovaPlayer nPlayer = PlayerManager.getPlayer(event.getPlayer());
		if(nPlayer.getGuiInventory() != null && !ChestGUIUtils.guiContinueList.contains(nPlayer)) {
			if(nPlayer.getGuiInventoryHistory().size() == 1) {
				nPlayer.setGuiInventory(null);
			}
			else {
				nPlayer.removeLastGUIInventoryHistory();

				NovaGuilds.runTask(new Runnable() {
					@Override
					public void run() {
						nPlayer.getGuiInventory().open(nPlayer);
					}
				});
			}
		}
	}
}
