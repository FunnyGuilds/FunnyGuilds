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

import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIInventoryRequiredItems extends AbstractGUIInventory {
	private final List<ItemStack> requiredItems = new ArrayList<>();

	/**
	 * The constructor
	 *
	 * @param itemStackList list of required items
	 */
	public GUIInventoryRequiredItems(List<ItemStack> itemStackList) {
		super(ChestGUIUtils.getChestSize(itemStackList.size()), Message.INVENTORY_REQUIREDITEMS_NAME);
		requiredItems.addAll(itemStackList);
	}

	@Override
	public void generateContent() {
		for(ItemStack item : requiredItems) {
			int amountInventory = InventoryUtils.getTotalAmountOfItemStackInInventory(getViewer().getPlayer().getInventory(), item);
			int amountEnderChest = InventoryUtils.getTotalAmountOfItemStackInInventory(getViewer().getPlayer().getEnderChest(), item);
			int needMore = item.getAmount() - amountEnderChest - amountInventory;

			if(needMore < 0) {
				needMore = 0;
			}

			ItemMeta itemStackMeta = item.hasItemMeta()
					? item.getItemMeta()
					: Bukkit.getItemFactory().getItemMeta(item.getType());

			List<String> lore = new ArrayList<>();

			if(itemStackMeta.hasLore()) {
				lore.addAll(itemStackMeta.getLore());
			}

			lore.addAll(Message.INVENTORY_REQUIREDITEMS_LORE
					.clone()
					.setVar(VarKey.AMOUNT_AVAILABLE, amountInventory)
					.setVar(VarKey.AMOUNT_AVAILABLE2, amountEnderChest)
					.setVar(VarKey.AMOUNT_AVAILABLE3, amountInventory + amountEnderChest)
					.setVar(VarKey.AMOUNT, item.getAmount())
					.setVar(VarKey.NEEDMORE, needMore)
					.getList());

			itemStackMeta.setLore(lore);
			item.setItemMeta(itemStackMeta);

			registerAndAdd(new EmptyExecutor(item));
		}
	}
}
