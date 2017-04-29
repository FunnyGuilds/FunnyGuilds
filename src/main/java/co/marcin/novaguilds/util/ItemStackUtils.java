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

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemStackUtils {
	private ItemStackUtils() {

	}

	/**
	 * Converts string to an item
	 *
	 * @param str string
	 * @return item
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack stringToItemStack(String str) {
		if(!str.isEmpty()) {
			ItemStack itemStack;
			Material material;
			String name = "";
			int amount = 0;
			final List<String> lore = new ArrayList<>();
			String loreString = "";
			String bookAuthor = null;
			String bookBook = null;
			String player = null;
			short durability = 0;
			PotionType potionType = null;
			int potionLevel = 0;
			byte data = (byte) 0;
			Map<Enchantment, Integer> enchantments = new HashMap<>();

			String[] explode = str.split(" ");
			String[] dataSplit = null;
			String materialString = explode[0];

			DyeColor color;

			if(explode[0].contains(":")) {
				dataSplit = explode[0].split(":");
				materialString = dataSplit[0];
			}

			if(NumberUtils.isNumeric(materialString)) {
				material = Material.getMaterial(Integer.parseInt(materialString));
			}
			else {
				material = Material.getMaterial(materialString.toUpperCase());
			}

			if(material == null) {
				return stringToItemStack("DIRT 1 name:&cINVALID_ITEM");
			}

			if(explode[0].contains(":")) {
				String dataString = dataSplit[1];
				if(NumberUtils.isNumeric(dataString)) {
					durability = Short.parseShort(dataString);
					data = Byte.parseByte(dataString);
				}
				else {
					color = DyeColor.valueOf(dataString.toUpperCase());
					if(color != null) {
						if(material == Material.INK_SACK) {
							data = color.getDyeData();
						}
						else {
							data = color.getWoolData();
						}
					}

					durability = data;
				}
			}

			if(explode.length > 1) { //amount
				if(NumberUtils.isNumeric(explode[1])) {
					amount = Integer.parseInt(explode[1]);
					explode[1] = null;
				}
			}
			else {
				amount = material.getMaxStackSize();
			}

			explode[0] = null;

			for(String detail : explode) {
				if(detail != null) {
					if(detail.contains(":")) {
						String[] detailSplit = detail.split(":", 2);
						String value = detailSplit[1];
						switch(detailSplit[0].toLowerCase()) {
							case "name":
								name = value;
								break;
							case "lore":
								loreString = value;
								break;
							case "title":
								name = value;
								break;
							case "author":
								bookAuthor = value;
								break;
							case "book":
								bookBook = value;
								break;
							case "power":
								if(material == Material.BOW) {
									enchantments.put(Enchantment.ARROW_DAMAGE, Integer.valueOf(value));
								}
								else if(material == Material.POTION) {
									if(NumberUtils.isNumeric(value)) {
										potionLevel = Integer.parseInt(value);
									}
								}
								else if(material == Material.FIREWORK) {
									//TODO
								}
								break;
							case "effect":
								if(material == Material.POTION) {
									potionType = PotionType.valueOf(value.toUpperCase());
								}
								break;
							case "duration":
								break;
							case "color":
								color = DyeColor.valueOf(value.toUpperCase());
								break;
							case "player":
								player = value;
								break;
							case "fade":
								break;
							case "shape":
								break;

							case "sharpness":
							case "alldamage":
								enchantments.put(Enchantment.DAMAGE_ALL, Integer.valueOf(value));
								break;
							case "arrowdamage":
							case "ardmg":
								enchantments.put(Enchantment.ARROW_DAMAGE, Integer.valueOf(value));
								break;
							case "baneofarthropods":
								enchantments.put(Enchantment.DAMAGE_ARTHROPODS, Integer.valueOf(value));
								break;
							case "durability":
							case "unbreaking":
								enchantments.put(Enchantment.DURABILITY, Integer.valueOf(value));
								break;
							case "fire":
							case "fireaspect":
								enchantments.put(Enchantment.FIRE_ASPECT, Integer.valueOf(value));
								break;
							case "knockback":
								enchantments.put(Enchantment.KNOCKBACK, Integer.valueOf(value));
								break;
							case "looting":
							case "fortune":
								enchantments.put(Enchantment.LOOT_BONUS_BLOCKS, Integer.valueOf(value));
								break;
							case "mobloot":
								enchantments.put(Enchantment.LOOT_BONUS_MOBS, Integer.valueOf(value));
								break;
							case "smite":
							case "undeaddamage":
								enchantments.put(Enchantment.DAMAGE_UNDEAD, Integer.valueOf(value));
								break;
							case "arrowknockback":
							case "punch":
								enchantments.put(Enchantment.ARROW_KNOCKBACK, Integer.valueOf(value));
								break;
							case "flame":
							case "flamearrow":
								enchantments.put(Enchantment.ARROW_FIRE, Integer.valueOf(value));
								break;
							case "infarrows":
							case "infinity":
								enchantments.put(Enchantment.ARROW_INFINITE, Integer.valueOf(value));
								break;
							case "digspeed":
							case "efficiency":
								enchantments.put(Enchantment.DIG_SPEED, Integer.valueOf(value));
							case "silktouch":
								enchantments.put(Enchantment.SILK_TOUCH, Integer.valueOf(value));
								break;
							case "highcrit":
							case "thorns":
								enchantments.put(Enchantment.THORNS, Integer.valueOf(value));
								break;
							case "blastprotect":
								enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, Integer.valueOf(value));
								break;
							case "fallprot":
							case "featherfall":
								enchantments.put(Enchantment.PROTECTION_FALL, Integer.valueOf(value));
								break;
							case "fireprot":
							case "fireprotect":
								enchantments.put(Enchantment.PROTECTION_FIRE, Integer.valueOf(value));
								break;
							case "projectileprotection":
							case "projprot":
								enchantments.put(Enchantment.PROTECTION_PROJECTILE, Integer.valueOf(value));
								break;
							case "protect":
							case "protection":
								enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, Integer.valueOf(value));
								break;
							case "waterworker":
								enchantments.put(Enchantment.WATER_WORKER, Integer.valueOf(value));
								break;
							case "respiration":
							case "breath":
							case "aquainfinity":
								enchantments.put(Enchantment.OXYGEN, Integer.valueOf(value));
								break;
							case "luck":
								enchantments.put(Enchantment.LUCK, Integer.valueOf(value));
								break;
							case "lure":
								enchantments.put(Enchantment.LURE, Integer.valueOf(value));
								break;
							case "glow":
								enchantments.put(new EnchantmentGlow(), 1);
								break;
						}
					}
				}
			}

			//replace _ with spaces
			name = org.apache.commons.lang.StringUtils.replace(name, "_", " ");
			name = StringUtils.fixColors(name);
			loreString = org.apache.commons.lang.StringUtils.replace(loreString, "_", " ");
			loreString = StringUtils.fixColors(loreString);

			if(loreString.contains("|")) {
				Collections.addAll(lore, org.apache.commons.lang.StringUtils.split(loreString, '|'));
			}
			else {
				lore.add(loreString);
			}


			itemStack = new ItemStack(material, amount, data);
			itemStack.addUnsafeEnchantments(enchantments);
			ItemMeta itemMeta = itemStack.getItemMeta();

			if(player != null && itemStack.getType() == Material.SKULL_ITEM) {
				SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
				skullMeta.setOwner(player);
				itemStack.setItemMeta(skullMeta);
			}

			if(!name.isEmpty()) {
				itemMeta.setDisplayName(name);
			}

			if(!loreString.isEmpty()) {
				itemMeta.setLore(lore);
			}

			if(material == Material.POTION && potionLevel != 0 && potionType != null) {
				Potion potion = new Potion(potionType, potionLevel);
				potion.apply(itemStack);
			}

			itemStack.setDurability(durability);
			itemStack.setItemMeta(itemMeta);

			return itemStack;
		}

		return stringToItemStack("DIRT 1 name:&cINVALID_ITEM");
	}

	/**
	 * Converts a list of strings to a list of items
	 *
	 * @param list list of strings
	 * @return list of items
	 */
	public static List<ItemStack> stringToItemStackList(List<String> list) {
		final List<ItemStack> itemList = new ArrayList<>();
		for(String item : list) {
			ItemStack itemStack = stringToItemStack(item);
			if(itemStack != null) {
				itemList.add(itemStack);
			}
		}

		return itemList;
	}

	/**
	 * Checks if two items are the same and only amount may be different
	 *
	 * @param itemStack1 item 1
	 * @param itemStack2 item 2
	 * @return boolean
	 */
	public static boolean isSimilar(ItemStack itemStack1, ItemStack itemStack2) {
		if(itemStack1 == null && itemStack2 != null || itemStack2 == null) {
			return false;
		}

		if(itemStack1.getType() != itemStack2.getType()) {
			return false;
		}

		if((itemStack1.hasItemMeta() || itemStack2.hasItemMeta()) && !itemStack1.getItemMeta().equals(itemStack2.getItemMeta())) {
			return false;
		}

		if(itemStack1.getDurability() != itemStack2.getDurability()) {
			return false;
		}

		if(!itemStack1.getEnchantments().equals(itemStack2.getEnchantments())) {
			return false;
		}

		if(itemStack1.getMaxStackSize() != itemStack2.getMaxStackSize()) {
			return false;
		}

		return true;
	}
}
