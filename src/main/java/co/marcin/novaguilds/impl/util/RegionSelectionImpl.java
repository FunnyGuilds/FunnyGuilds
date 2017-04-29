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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.CompatibilityUtils;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegionSelectionImpl implements RegionSelection {
	private final List<Block> blockList = new ArrayList<>();
	private final Location[] corners = new Location[2];
	private final List<NovaPlayer> playerList = new ArrayList<>();
	private final Type type;
	private RegionValidity regionValidity = RegionValidity.VALID;

	private Material cornerMaterial;
	private Byte cornerData;
	private Material borderMaterial;
	private Byte borderData;
	private NovaRegion selectedRegion;
	private Integer resizingCorner = -1;

	/**
	 * Constructor for filling with corner locations
	 *
	 * @param nPlayer the player
	 * @param type    selection type
	 */
	public RegionSelectionImpl(NovaPlayer nPlayer, Type type) {
		this(nPlayer, type, null);
	}

	/**
	 * The constructor
	 * for resizing
	 *
	 * @param nPlayer the player
	 * @param type    selection type
	 * @param region  region being resized
	 */
	public RegionSelectionImpl(NovaPlayer nPlayer, Type type, NovaRegion region) {
		addSpectator(nPlayer);
		this.type = type;
		this.selectedRegion = region;

		nPlayer.setActiveSelection(this);
	}

	@Override
	public void send() {
		for(Player player : CompatibilityUtils.getOnlinePlayers()) {
			if(!Permission.NOVAGUILDS_ADMIN_REGION_SPECTATE.has(player)) {
				continue;
			}

			NovaPlayer onlineNovaPlayer = PlayerManager.getPlayer(player);

			if(onlineNovaPlayer.getPreferences().getRegionSpectate()) {
				addSpectator(onlineNovaPlayer);
			}
		}

		loadMaterials();

		for(NovaPlayer nPlayer : getSpectators()) {
			for(Block block : getBlocks()) {
				RegionUtils.resetBlock(nPlayer.getPlayer(), block);
			}
		}

		clearBlockList();

		for(NovaPlayer nPlayer : getSpectators()) {
			Player player = nPlayer.getPlayer();

			sendRectangle(player);
			highlightCorner(player, getCorner(0), cornerMaterial, cornerData);
			highlightCorner(player, getCorner(1), cornerMaterial, cornerData);
		}
	}

	@Override
	public void reset() {
		for(NovaPlayer nPlayer : getSpectators()) {
			reset(nPlayer);
		}

		clearBlockList();
	}

	@Override
	public void reset(NovaPlayer nPlayer) {
		for(Block block : new ArrayList<>(getBlocks())) {
			RegionUtils.resetBlock(nPlayer.getPlayer(), block);
		}

		if(getSpectators().size() == 1 && getSpectators().contains(nPlayer)) {
			clearBlockList();
		}

		if(nPlayer.getActiveSelection() == this) {
			nPlayer.setActiveSelection(null);
		}
	}

	@Override
	public void setCorner(Integer index, Location location) {
		checkCornerBounds(index);
		Location corner = null;
		if(location != null) {
			corner = location.clone();
			corner.setY(0);
		}

		corners[index] = corner;
	}

	@Override
	public void setResizingCorner(Integer index) {
		checkCornerBounds(index);
		this.resizingCorner = index;
	}

	@Override
	public void setValidity(RegionValidity regionValidity) {
		this.regionValidity = regionValidity;
	}

	@Override
	public void addSpectator(NovaPlayer nPlayer) {
		if(!playerList.contains(nPlayer)) {
			playerList.add(nPlayer);
		}
	}

	@Override
	public void removeSpectator(NovaPlayer nPlayer) {
		if(getPlayer().equals(nPlayer)) {
			return;
		}

		playerList.remove(nPlayer);
	}

	@Override
	public Material getBorderMaterial() {
		return borderMaterial;
	}

	@Override
	public byte getBorderData() {
		return borderData;
	}

	@Override
	public Material getCornerMaterial() {
		return cornerMaterial;
	}

	@Override
	public byte getCornerData() {
		return cornerData;
	}

	@Override
	public RegionValidity getValidity() {
		return regionValidity;
	}

	@Override
	public boolean isValid() {
		return hasBothSelections() && regionValidity == RegionValidity.VALID;
	}

	@Override
	public boolean hasBothSelections() {
		return getCorner(0) != null && getCorner(1) != null;
	}

	@Override
	public boolean isSent() {
		return !blockList.isEmpty();
	}

	@Override
	public int getWidth() {
		if(!hasBothSelections()) {
			return 0;
		}

		return Math.abs(getCorner(0).getBlockX() - getCorner(1).getBlockX()) + 1;
	}

	@Override
	public int getLength() {
		if(!hasBothSelections()) {
			return 0;
		}

		return Math.abs(getCorner(0).getBlockZ() - getCorner(1).getBlockZ()) + 1;
	}

	@Override
	public Location getCenter() {
		int newX = getCorner(0).getBlockX() + (getCorner(0).getBlockX() < getCorner(1).getBlockX() ? getWidth() : -getWidth()) / 2;
		int newZ = getCorner(0).getBlockZ() + (getCorner(0).getBlockZ() < getCorner(1).getBlockZ() ? getLength() : -getLength()) / 2;

		return new Location(getWorld(), newX, 0, newZ);
	}

	@Override
	public World getWorld() {
		if(!hasBothSelections()) {
			return null;
		}

		return getCorner(0).getWorld();
	}

	@Override
	public Location getCorner(Integer index) {
		checkCornerBounds(index);
		return corners[index];
	}

	@Override
	public Integer getResizingCorner() {
		return resizingCorner;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public List<Block> getBlocks() {
		return blockList;
	}

	@Override
	public NovaRegion getSelectedRegion() {
		return selectedRegion;
	}

	@Override
	public NovaPlayer getPlayer() {
		return getSpectators().get(0);
	}

	@Override
	public List<NovaPlayer> getSpectators() {
		return playerList;
	}

	/**
	 * Loads materials depending on selection type
	 */
	protected void loadMaterials() {
		switch(type) {
			case HIGHLIGHT:
				cornerMaterial = Config.REGION_MATERIALS_HIGHLIGHT_REGION_CORNER.getMaterial();
				cornerData = Config.REGION_MATERIALS_HIGHLIGHT_REGION_CORNER.getMaterialData();

				borderMaterial = Config.REGION_MATERIALS_HIGHLIGHT_REGION_BORDER.getMaterial();
				borderData = Config.REGION_MATERIALS_HIGHLIGHT_REGION_CORNER.getMaterialData();
				break;
			case HIGHLIGHT_RESIZE:
				cornerMaterial = Config.REGION_MATERIALS_HIGHLIGHT_RESIZE_CORNER.getMaterial();
				cornerData = Config.REGION_MATERIALS_HIGHLIGHT_RESIZE_CORNER.getMaterialData();

				borderMaterial = Config.REGION_MATERIALS_HIGHLIGHT_RESIZE_BORDER.getMaterial();
				borderData = Config.REGION_MATERIALS_HIGHLIGHT_RESIZE_BORDER.getMaterialData();
				break;
			case CREATE:
				cornerMaterial = Config.REGION_MATERIALS_SELECTION_VALID_CORNER.getMaterial();
				cornerData = Config.REGION_MATERIALS_SELECTION_VALID_CORNER.getMaterialData();

				borderMaterial = Config.REGION_MATERIALS_SELECTION_VALID_BORDER.getMaterial();
				borderData = Config.REGION_MATERIALS_SELECTION_VALID_BORDER.getMaterialData();
				break;
			case RESIZE:
				cornerMaterial = Config.REGION_MATERIALS_RESIZE_CORNER.getMaterial();
				cornerData = Config.REGION_MATERIALS_RESIZE_CORNER.getMaterialData();

				borderMaterial = Config.REGION_MATERIALS_RESIZE_BORDER.getMaterial();
				borderData = Config.REGION_MATERIALS_RESIZE_BORDER.getMaterialData();
				break;
		}

		if(getValidity() != RegionValidity.VALID) {
			cornerMaterial = Config.REGION_MATERIALS_SELECTION_INVALID_CORNER.getMaterial();
			cornerData = Config.REGION_MATERIALS_SELECTION_INVALID_CORNER.getMaterialData();

			borderMaterial = Config.REGION_MATERIALS_SELECTION_INVALID_BORDER.getMaterial();
			borderData = Config.REGION_MATERIALS_SELECTION_INVALID_BORDER.getMaterialData();
		}
	}

	/**
	 * Clears block list
	 */
	protected void clearBlockList() {
		blockList.clear();
	}

	/**
	 * Sends a rectangle
	 *
	 * @param player target player
	 */
	@SuppressWarnings("deprecation")
	protected void sendRectangle(Player player) {
		if(player == null || getCorner(0) == null || getCorner(1) == null) {
			return;
		}

		for(Block block : RegionUtils.getBorderBlocks(getCorner(0), getCorner(1))) {
			if(borderMaterial == null) {
				RegionUtils.resetBlock(player, block);
				continue;
			}

			player.sendBlockChange(block.getLocation(), borderMaterial, borderData);
			blockList.add(block);
		}
	}

	/**
	 * Sends block change to a player
	 *
	 * @param player   target player
	 * @param location block location
	 * @param material material
	 * @param data     durability byte
	 */
	@SuppressWarnings("deprecation")
	protected void highlightCorner(Player player, Location location, Material material, Byte data) {
		if(player == null || location == null) {
			return;
		}

		location = location.clone();
		Block highest1 = player.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ());
		location.setY(highest1.getY() - (highest1.getType() == Material.SNOW ? 0 : 1));

		if(material == null) {
			material = location.getBlock().getType();
			data = location.getBlock().getData();
		}

		player.sendBlockChange(location, material, data);
		getBlocks().add(location.getBlock());
	}

	/**
	 * Checks corner bounds
	 * Can be 0 or 1
	 *
	 * @param index index
	 */
	protected final void checkCornerBounds(int index) {
		if(index != 0 && index != 1) {
			throw new IllegalArgumentException("Index can be either 0 or 1");
		}
	}
}
