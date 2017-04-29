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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.api.basic.GUIInventory;
import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChestGUIUtils {
	public static final List<NovaPlayer> guiContinueList = new ArrayList<>();

	private ChestGUIUtils() {

	}

	/**
	 * Creates a number which is the multiple of 9
	 * So an inventory can be created with it
	 *
	 * @param count amount of items
	 * @return suitable inventory size
	 */
	public static int getChestSize(int count) {
		return count == 0 ? 9 : (count / 9) * 9 + (count % 9 == 0 ? 0 : 9);
	}

	/**
	 * Opens inventory gui
	 *
	 * @param nPlayer      player
	 * @param guiInventory target gui
	 */
	public static void openGUIInventory(NovaPlayer nPlayer, GUIInventory guiInventory) {
		if(nPlayer.isOnline()) {
			nPlayer.setGuiInventory(guiInventory);

			if(!guiContinueList.contains(nPlayer)) {
				guiContinueList.add(nPlayer);
			}

			guiInventory.getInventory().clear();
			guiInventory.getExecutors().clear();
			guiInventory.generateContent();
			addBackItem(guiInventory);

			nPlayer.getPlayer().openInventory(guiInventory.getInventory());

			guiContinueList.remove(nPlayer);

			guiInventory.onOpen();
		}
	}

	/**
	 * Creates an inventory
	 *
	 * @param size  size
	 * @param title title
	 * @return the inventory
	 */
	public static Inventory createInventory(int size, String title) {
		if(title.length() > 32) {
			title = title.substring(0, 32);
		}

		return Bukkit.createInventory(null, size, title);
	}

	/**
	 * Creates an inventory
	 *
	 * @param size  size
	 * @param title title message
	 * @return the inventory
	 */
	public static Inventory createInventory(int size, MessageWrapper title) {
		return createInventory(size, title.get());
	}

	/**
	 * Adds a item allowing to return to a previous gui
	 *
	 * @param guiInventory gui
	 */
	public static void addBackItem(GUIInventory guiInventory) {
		Inventory inventory = guiInventory.getInventory();
		NovaPlayer nPlayer = guiInventory.getViewer();

		ItemStack lastItem = inventory.getItem(inventory.getSize() - 1);
		if((lastItem == null || lastItem.getType() == Material.AIR) && nPlayer.getGuiInventoryHistory().size() > 1) {
			inventory.setItem(inventory.getSize() - 1, Message.INVENTORY_GUI_BACK.getItemStack());
		}
	}
}
