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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DynmapManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private MarkerSet markerSet;
	private boolean enabled = false;
	private MarkerIcon guildHomeIcon;
	private final Map<NovaGuild, GuildMarkers> markersMap = new HashMap<>();

	class GuildMarkers {
		private Marker home;
		private final Map<NovaRegion, AreaMarker> regionMap = new HashMap<>();
	}

	/**
	 * Inits the manager
	 */
	public void init() {
		if(plugin.getDependencyManager().isEnabled(Dependency.DYNMAP) && Config.DYNMAP.getBoolean()) {
			DynmapAPI api = (DynmapAPI) plugin.getDependencyManager().get(Dependency.DYNMAP, Plugin.class);
			markerSet = api.getMarkerAPI().createMarkerSet("novaguilds.markerset", "NovaGuilds", null, false);
			InputStream inputStream = plugin.getResource("guildhomeicon.png");
			guildHomeIcon = api.getMarkerAPI().createMarkerIcon("novaguilds.guildhome", "Guild Home", inputStream);
			enabled = true;
		}
	}

	/**
	 * Adds a region area marker
	 *
	 * @param region the region
	 */
	public void addRegion(NovaRegion region) {
		if(!enabled) {
			return;
		}

		String message = Message.DYNMAP_REGION.setVar(VarKey.GUILD_NAME, region.getGuild().getName())
		                                      .setVar(VarKey.INDEX, region.getIndex())
		                                      .get();

		getMarkers(region.getGuild()).regionMap.put(region, markerSet.createAreaMarker(
				region.getUUID().toString(),
				message,
				false,
				region.getWorld().getName(),
				new double[] { region.getCorner(0).getBlockX(), region.getCorner(1).getBlockX() },
				new double[] { region.getCorner(0).getBlockZ(), region.getCorner(1).getBlockZ() },
				false
		));
	}

	/**
	 * Adds guild home marker
	 *
	 * @param guild guild
	 */
	public void addGuildHome(NovaGuild guild) {
		if(!enabled) {
			return;
		}

		String message = Message.DYNMAP_GUILDHOME.setVar(VarKey.GUILD_NAME, guild.getName()).get();

		getMarkers(guild).home = markerSet.createMarker(
				guild.getUUID().toString(),
				message,
				guild.getHome().getWorld().getName(),
				guild.getHome().getBlockX(),
				guild.getHome().getBlockY(),
				guild.getHome().getBlockZ(),
				guildHomeIcon,
				false
		);
	}

	/**
	 * Adds a guild to the map
	 * (Adds all regions too)
	 *
	 * @param guild target guild
	 */
	public void addGuild(NovaGuild guild) {
		if(!enabled) {
			return;
		}

		for(NovaRegion region : guild.getRegions()) {
			addRegion(region);
		}

		addGuildHome(guild);
	}

	/**
	 * Updates a region
	 *
	 * @param region region
	 */
	public void updateRegion(NovaRegion region) {
		removeRegion(region);
		addRegion(region);
	}

	/**
	 * Updates guild's home
	 *
	 * @param guild guild
	 */
	public void updateGuildHome(NovaGuild guild) {
		removeGuildHome(guild);
		addGuildHome(guild);
	}

	/**
	 * Updates a guild
	 *
	 * @param guild guild
	 */
	public void updateGuild(NovaGuild guild) {
		removeGuild(guild);
		addGuild(guild);
	}

	/**
	 * Removes a region marker
	 *
	 * @param region region
	 */
	public void removeRegion(NovaRegion region) {
		if(!enabled) {
			return;
		}

		getMarkers(region.getGuild()).regionMap.get(region).deleteMarker();
	}

	/**
	 * Removes home marker if one exists
	 *
	 * @param guild guild
	 */
	public void removeGuildHome(NovaGuild guild) {
		if(!enabled) {
			return;
		}

		GuildMarkers markers = getMarkers(guild);
		if(markers.home != null) {
			markers.home.deleteMarker();
			markers.home = null;
		}
	}

	/**
	 * Removes a guild
	 * with the home and region markers
	 *
	 * @param guild guild
	 */
	public void removeGuild(NovaGuild guild) {
		if(!enabled) {
			return;
		}

		removeGuildHome(guild);

		for(NovaRegion region : guild.getRegions()) {
			removeRegion(region);
		}

		markersMap.remove(guild);
	}

	/**
	 * Checks if the dynmap manager has
	 * been enabled successfully
	 *
	 * @return true if yes
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Gets guild markers wrapper instance
	 *
	 * @param guild guild
	 * @return guild markers
	 */
	private GuildMarkers getMarkers(NovaGuild guild) {
		if(!markersMap.containsKey(guild)) {
			markersMap.put(guild, new GuildMarkers());
		}

		return markersMap.get(guild);
	}
}
