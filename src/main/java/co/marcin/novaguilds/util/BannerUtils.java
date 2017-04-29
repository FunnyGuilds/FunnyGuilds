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


import co.marcin.novaguilds.api.util.BannerMetaSerializer;
import co.marcin.novaguilds.impl.util.BannerMetaSerializerImpl;
import co.marcin.novaguilds.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class BannerUtils {
	private static final BannerMetaSerializer serializer = new BannerMetaSerializerImpl();

	private BannerUtils() {

	}

	/**
	 * Gets the serializer
	 *
	 * @return banner meta serializer
	 */
	public static BannerMetaSerializer getSerializer() {
		return serializer;
	}

	/**
	 * Gets random banner item
	 *
	 * @return banner item stack
	 */
	public static ItemStack randomBannerItemStack() {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R2)) {
			return null;
		}

		ItemStack itemStack = new ItemStack(Material.BANNER);
		itemStack.setItemMeta(getRandomMeta());
		return itemStack;
	}

	/**
	 * Gets random banner meta
	 *
	 * @return banner meta
	 */
	public static BannerMeta getRandomMeta() {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R2)) {
			return null;
		}

		BannerMeta meta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(Material.BANNER);
		meta.setBaseColor(randomDyeColor());

		for(int i = NumberUtils.randInt(0, PatternType.values().length) + 2; i > 0; i--) {
			meta.addPattern(new Pattern(randomDyeColor(), randomPatternType()));
		}

		return meta;
	}

	/**
	 * Gets random pattern type
	 *
	 * @return pattern type
	 */
	protected static PatternType randomPatternType() {
		return PatternType.values()[NumberUtils.randInt(0, PatternType.values().length - 1)];
	}

	/**
	 * Gets random dye color
	 *
	 * @return dye color
	 */
	protected static DyeColor randomDyeColor() {
		return DyeColor.values()[NumberUtils.randInt(0, DyeColor.values().length - 1)];
	}

	/**
	 * Deserializes a string to banner meta
	 *
	 * @param string serialized meta
	 * @return banner meta
	 */
	public static BannerMeta deserialize(String string) {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R2)) {
			return null;
		}

		return getSerializer().deserialize(string);
	}

	/**
	 * Serializes banner into a string
	 *
	 * @param banner banner meta
	 * @return serialized meta
	 */
	public static String serialize(Banner banner) {
		return getSerializer().serialize(getBannerMeta(banner));
	}

	/**
	 * Serializes banner meta into a string
	 *
	 * @param bannerMeta banner meta
	 * @return serialized meta
	 */
	public static String serialize(BannerMeta bannerMeta) {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R2)) {
			return "";
		}

		return getSerializer().serialize(bannerMeta);
	}

	/**
	 * Applies meta to a banner
	 *
	 * @param banner banner block
	 * @param meta   banner meta
	 * @return banner block
	 */
	public static Banner applyMeta(Banner banner, BannerMeta meta) {
		banner.setBaseColor(meta.getBaseColor());
		banner.setPatterns(meta.getPatterns());
		return banner;
	}

	/**
	 * Applies meta to a shield
	 *
	 * @param itemStack  shield item
	 * @param bannerMeta banner meta
	 * @return shield item
	 */
	public static ItemStack applyMeta(ItemStack itemStack, BannerMeta bannerMeta) {
		if(itemStack.getType() != Material.SHIELD && itemStack.getType() != Material.BANNER) {
			throw new IllegalArgumentException("Passed ItemStack is not a shield nor a banner");
		}

		ItemMeta meta = itemStack.getItemMeta();
		BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
		Banner banner = (Banner) blockStateMeta.getBlockState();
		applyMeta(banner, bannerMeta);
		banner.update();
		blockStateMeta.setBlockState(banner);
		itemStack.setItemMeta(blockStateMeta);
		return itemStack;
	}

	/**
	 * Turns a banner into banner meta
	 *
	 * @param banner banner block
	 * @return banner meta
	 */
	public static BannerMeta getBannerMeta(Banner banner) {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R2)) {
			return null;
		}

		BannerMeta meta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(Material.BANNER);

		meta.setBaseColor(banner.getBaseColor());
		for(Pattern pattern : banner.getPatterns()) {
			meta.addPattern(pattern);
		}

		return meta;
	}
}
