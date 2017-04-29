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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.util.CompatibilityUtils;
import co.marcin.novaguilds.util.InventoryUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.List;

public class VaultListener extends AbstractListener {
	private static final List<InventoryAction> disallowedActions = new ArrayList<>();
	private static final BlockFace[] doubleChestFaces;

	static {
		//Add disallowed actions
		disallowedActions.add(InventoryAction.CLONE_STACK);
		disallowedActions.add(InventoryAction.COLLECT_TO_CURSOR);
		disallowedActions.add(InventoryAction.HOTBAR_MOVE_AND_READD);
		disallowedActions.add(InventoryAction.HOTBAR_SWAP);
		disallowedActions.add(InventoryAction.MOVE_TO_OTHER_INVENTORY);
		disallowedActions.add(InventoryAction.PICKUP_ALL);
		disallowedActions.add(InventoryAction.PICKUP_HALF);
		disallowedActions.add(InventoryAction.PICKUP_ONE);
		disallowedActions.add(InventoryAction.PICKUP_SOME);
		disallowedActions.add(InventoryAction.SWAP_WITH_CURSOR);
		disallowedActions.add(InventoryAction.DROP_ALL_CURSOR);
		disallowedActions.add(InventoryAction.DROP_ALL_SLOT);
		disallowedActions.add(InventoryAction.DROP_ONE_CURSOR);
		disallowedActions.add(InventoryAction.DROP_ONE_SLOT);
		disallowedActions.add(InventoryAction.UNKNOWN);

		//double chest faces
		doubleChestFaces = new BlockFace[]{
				BlockFace.EAST,
				BlockFace.NORTH,
				BlockFace.SOUTH,
				BlockFace.WEST
		};
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(plugin.getGuildManager().isVaultItemStack(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
			Message.CHAT_GUILD_VAULT_DROP.send(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getInventory() == null) {
			return;
		}

		NovaPlayer nPlayer = PlayerManager.getPlayer(event.getWhoClicked());
		String nameVault = Config.VAULT_ITEM.getItemStack().getItemMeta().getDisplayName();

		if(event.getInventory().getTitle() == null || !event.getInventory().getTitle().equals(nameVault)) {
			return;
		}

		if(!event.getView().getTopInventory().equals(CompatibilityUtils.getClickedInventory(event))) {
			return;
		}

		if(!disallowedActions.contains(event.getAction()) && nPlayer.hasPermission(GuildPermission.VAULT_PUT)) {
			return;
		}

		if(nPlayer.hasPermission(GuildPermission.VAULT_TAKE)) {
			return;
		}

		if(nPlayer.getPreferences().getBypass()) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		if(plugin.getGuildManager().isVaultBlock(event.getBlock())) {
			if(nPlayer.getPreferences().getBypass()) {
				return;
			}

			Chest chest = (Chest) event.getBlock().getState();

			if(InventoryUtils.isEmpty(chest.getInventory())) {
				event.setCancelled(true);

				if(!nPlayer.hasPermission(GuildPermission.VAULT_BREAK)) {
					Message.CHAT_GUILD_NOGUILDPERM.send(player);
					return;
				}

				if(nPlayer.getGuild().getVaultHologram() != null) {
					nPlayer.getGuild().getVaultHologram().delete();
					nPlayer.getGuild().setVaultHologram(null);
				}

				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Config.VAULT_ITEM.getItemStack());

				nPlayer.getGuild().setVaultLocation(null);
				Message.CHAT_GUILD_VAULT_BREAK_SUCCESS.send(player);
			}
			else {
				event.setCancelled(true);
				Message.CHAT_GUILD_VAULT_BREAK_NOTEMPTY.send(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) { //PLACING
		Player player = event.getPlayer();

		if(plugin.getRegionManager().canInteract(player, event.getBlock())) {
			NovaPlayer nPlayer = PlayerManager.getPlayer(player);
			Material itemType = CompatibilityUtils.getItemInMainHand(player).getType();

			if(itemType == Config.VAULT_ITEM.getItemStack().getType()) {
				for(BlockFace face : doubleChestFaces) {
					if(event.getBlock().getRelative(face) != null) {
						if(plugin.getGuildManager().isVaultBlock(event.getBlock().getRelative(face))) {
							event.setCancelled(true);
							Message.CHAT_GUILD_VAULT_PLACE_DOUBLECHEST.send(player);
							return;
						}
					}
				}

				if(plugin.getGuildManager().isVaultItemStack(event.getItemInHand())) {
					if(nPlayer.hasGuild()) {
						if(!nPlayer.hasPermission(GuildPermission.VAULT_PLACE)) {
							event.setCancelled(true);
							Message.CHAT_GUILD_NOGUILDPERM.send(player);
							return;
						}

						if(nPlayer.getGuild().getVaultLocation() == null) {
							NovaRegion region = RegionManager.get(event.getBlockPlaced());
							if(region != null && region.getGuild().isMember(nPlayer)) {
								if(player.getGameMode() == GameMode.CREATIVE) {
									player.getInventory().remove(event.getItemInHand());
								}

								nPlayer.getGuild().setVaultLocation(event.getBlockPlaced().getLocation());
								plugin.getGuildManager().appendVaultHologram(nPlayer.getGuild());
								Message.CHAT_GUILD_VAULT_PLACE_SUCCESS.send(player);
							}
							else {
								Message.CHAT_GUILD_VAULT_OUTSIDEREGION.send(player);
								event.setCancelled(true);
							}
						}
						else {
							Message.CHAT_GUILD_VAULT_PLACE_EXISTS.send(player);
							event.setCancelled(true);
						}
					}
					else {
						player.getInventory().remove(event.getItemInHand());
						event.setCancelled(true);
					}
				}
			}
			else {
				List<Material> blockedMaterials = Config.VAULT_DENYRELATIVE.getMaterialList();
				if(blockedMaterials.contains(itemType)) {
					for(BlockFace face : BlockFace.values()) {
						if(plugin.getGuildManager().isVaultBlock(event.getBlock().getRelative(face))) {
							Message.CHAT_GUILD_VAULT_DENYRELATIVE.send(player);
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
}
