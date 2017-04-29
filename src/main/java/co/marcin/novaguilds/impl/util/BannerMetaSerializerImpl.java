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

package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.api.util.BannerMetaSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerMetaSerializerImpl implements BannerMetaSerializer {
	@Override
	public String serialize(BannerMeta bannerMeta) {
		if(bannerMeta == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();

		builder.append((bannerMeta.getBaseColor() == null ? DyeColor.BLACK : bannerMeta.getBaseColor()).name());

		if(bannerMeta.numberOfPatterns() > 0) {
			builder.append(':');
		}

		int index = 1;
		for(Pattern pattern : bannerMeta.getPatterns()) {
			builder.append(pattern.getColor().name());
			builder.append('-');
			builder.append(pattern.getPattern().getIdentifier());

			if(index < bannerMeta.numberOfPatterns()) {
				builder.append("|");
			}

			index++;
		}

		return builder.toString();
	}

	@Override
	public BannerMeta deserialize(String string) {
		BannerMeta meta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(Material.BANNER);

		if(string == null || string.isEmpty()) {
			return meta;
		}

		String baseColorString;
		String patternsString;

		if(StringUtils.contains(string, ':')) {
			String[] baseSplit = StringUtils.split(string, ':');
			baseColorString = baseSplit[0];
			patternsString = baseSplit[1];
		}
		else {
			baseColorString = string;
			patternsString = "";
		}

		meta.setBaseColor(DyeColor.valueOf(baseColorString));

		if(!patternsString.isEmpty()) {
			String[] patternsSplit;

			if(StringUtils.contains(patternsString, '|')) {
				patternsSplit = StringUtils.split(patternsString, '|');
			}
			else {
				patternsSplit = new String[]{
						patternsString
				};
			}

			for(String patternString : patternsSplit) {
				String[] patternSplit = StringUtils.split(patternString, '-');

				meta.addPattern(new Pattern(DyeColor.valueOf(patternSplit[0]), PatternType.getByIdentifier(patternSplit[1])));
			}
		}

		return meta;
	}
}
