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

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.event.PlayerInteractEntityEvent;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.EntityUseAction;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.util.BannerUtils;
import co.marcin.novaguilds.util.CompatibilityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RegionInteractListener extends AbstractListener {
	/**
	 * The constructor
	 */
	public RegionInteractListener() {
		super();

		if(ConfigManager.getServerVersion().isNewerThan(ConfigManager.ServerVersion.MINECRAFT_1_7_R4)) {
			new Non1_7Events();
		}

		if(ConfigManager.getServerVersion().isNewerThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R3)) {
			new Non1_8Events();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(event.getClickedBlock() == null) {
			return;
		}

		List<String> denyInteract = Config.REGION_DENYINTERACT.getStringList();
		List<String> denyUse = Config.REGION_DENYUSE.getStringList();

		String clickedBlockName = event.getClickedBlock().getType().name();
		String usedItemName = (event.getItem() == null ? Material.AIR : event.getItem().getType()).name();

		NovaRegion region = RegionManager.get(event.getClickedBlock());

		if(region == null) {
			return;
		}

		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		NovaGuild guild = region.getGuild();

		if(nPlayer.getPreferences().getBypass()) {
			return;
		}

		if(!denyInteract.contains(clickedBlockName) && !denyUse.contains(usedItemName)) {
			return;
		}

		if(nPlayer.hasGuild()) {
			if(guild.isMember(nPlayer)) {
				if(nPlayer.hasPermission(GuildPermission.INTERACT)) {
					if(event.getAction() != Action.RIGHT_CLICK_BLOCK || (!plugin.getGuildManager().isVaultBlock(event.getClickedBlock()) || nPlayer.hasPermission(GuildPermission.VAULT_ACCESS))) {
						return;
					}
				}
			}
			else if(guild.isAlly(nPlayer.getGuild()) && Config.REGION_ALLYINTERACT.getBoolean()) {
				return;
			}
		}


		event.setCancelled(true);

		if(clickedBlockName.contains("_PLATE")) { //Suppress for plates
			return;
		}

		Message.CHAT_REGION_DENY_INTERACT.send(player);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) { //BREAKING
		Player player = event.getPlayer();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		if(RegionManager.get(event.getBlock()) != null && (!plugin.getRegionManager().canInteract(player, event.getBlock()) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.BLOCK_BREAK)))) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) { //PLACING
		Player player = event.getPlayer();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		if(RegionManager.get(event.getBlock()) != null && (!plugin.getRegionManager().canInteract(player, event.getBlock()) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.BLOCK_PLACE)))) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) { //Entity Damage
		List<String> denyMobDamageList = Config.REGION_DENYMOBDAMAGE.getStringList();

		if(denyMobDamageList.contains(event.getEntityType().name())) {
			boolean playerCaused = false;
			Player player = null;
			Arrow arrow = null;

			if(event.getCause() == DamageCause.PROJECTILE && event.getDamager() instanceof Arrow) {
				arrow = (Arrow) event.getDamager();

				if(arrow.getShooter() instanceof Player) {
					playerCaused = true;
					player = (Player) arrow.getShooter();
				}
			}

			if(event.getDamager() instanceof Player) {
				playerCaused = true;
				player = (Player) event.getDamager();
			}

			if(!playerCaused) {
				return;
			}

			PlayerInteractEntityEvent interactEntityEvent = new PlayerInteractEntityEvent(player, event.getEntity(), EntityUseAction.ATTACK);
			ListenerManager.getLoggedPluginManager().callEvent(interactEntityEvent);
			event.setCancelled(interactEntityEvent.isCancelled());

			if(interactEntityEvent.isCancelled()) {
				if(arrow != null) {
					arrow.remove();
				}
			}
		}
	}

	@EventHandler
	public void onPlayerClickEntityEvent(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		Entity entity = event.getEntity();
		List<String> denyDamage = Config.REGION_DENYMOBDAMAGE.getStringList();

		if(entity instanceof Player ) {
			return;
		}

		if(RegionManager.get(entity) != null) {
			if(event.getAction() == EntityUseAction.ATTACK) {
				if(!plugin.getRegionManager().canInteract(player, entity) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_ATTACK))
						&& denyDamage.contains(entity.getType().name())) {
					event.setCancelled(true);
					Message.CHAT_REGION_DENY_ATTACKMOB.send(player);
				}
			}
			else if(!plugin.getRegionManager().canInteract(player, entity) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_RIDE))
					&& entity.getType() == EntityType.SHEEP
					&& CompatibilityUtils.getItemInMainHand(player).getType() == Material.SHEARS) {
				event.setCancelled(true);
				Message.CHAT_REGION_DENY_RIDEMOB.send(player);
			}
		}
	}

	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		Location loc = event.getLocation();
		NovaRegion region = RegionManager.get(loc);

		if(region != null) {
			for(Block block : new ArrayList<>(event.blockList())) {
				if(plugin.getGuildManager().isVaultBlock(block) && !region.getGuild().isRaid()) {
					event.blockList().remove(block);
				}
			}

			Message.CHAT_GUILD_EXPLOSIONATREGION.broadcast(region.getGuild());
		}
	}

	@EventHandler
	public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {
		List<String> denyRiding = Config.REGION_DENYRIDING.getStringList();
		Player player = event.getPlayer();
		Entity entity = event.getEntity();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		if(denyRiding.contains(entity.getType().name())) {
			if(RegionManager.get(entity) != null
					&& (!plugin.getRegionManager().canInteract(player, entity) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_LEASH)))
					&& !(entity instanceof Vehicle) || !PlayerManager.getPlayer(player).isVehicleListed((Vehicle) event.getEntity())) {
				event.setCancelled(true);
				Message.CHAT_REGION_DENY_UNLEASH.send(player);
			}
		}
	}

	@EventHandler
	public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
		List<String> denyRiding = Config.REGION_DENYRIDING.getStringList();
		Player player = event.getPlayer();
		Entity entity = event.getEntity();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		if(denyRiding.contains(entity.getType().name())
				&& RegionManager.get(entity) != null
				&& (!plugin.getRegionManager().canInteract(player, entity) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_LEASH)))
				&& !(entity instanceof Vehicle) || !PlayerManager.getPlayer(player).isVehicleListed((Vehicle) event.getEntity())) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_LEASH.send(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		Player player = event.getPlayer();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		if(nPlayer.getPreferences().getBypass()) {
			return;
		}

		//Fluid protection
		NovaRegion fluidProtectRegion = null;
		for(NovaRegion region : plugin.getRegionManager().getRegions()) {
			if(!region.getWorld().equals(block.getWorld())) {
				continue;
			}

			Location centerLocation = region.getCenter().clone();
			Location blockLocation = block.getLocation().clone();
			centerLocation.setY(0);
			blockLocation.setY(0);

			if(blockLocation.distance(centerLocation) <= region.getDiagonal() / 2 + Config.REGION_FLUIDPROTECT.getInt()) {
				fluidProtectRegion = region;
				break;
			}
		}

		if((fluidProtectRegion != null
				&& (!nPlayer.hasGuild()
					|| (!fluidProtectRegion.getGuild().isMember(nPlayer)
						&& !fluidProtectRegion.getGuild().isAlly(nPlayer.getGuild()))))
				|| (RegionManager.get(block) != null
					&& (!plugin.getRegionManager().canInteract(player, block)
						|| !nPlayer.hasPermission(GuildPermission.BLOCK_PLACE)))) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if(!Config.REGION_WATERFLOW.getBoolean()) {
			Material type = event.getBlock().getType();

			if((type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LAVA || type == Material.STATIONARY_LAVA)
					&& RegionManager.get(event.getBlock()) == null
					&& RegionManager.get(event.getToBlock()) != null) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		Vehicle vehicle = event.getVehicle();

		if(!(event.getEntered() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntered();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		List<String> denyRiding = Config.REGION_DENYRIDING.getStringList();

		if(denyRiding.contains(vehicle.getType().name())
				&& RegionManager.get(vehicle) != null
				&& (!plugin.getRegionManager().canInteract(player, vehicle) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_RIDE)))
				&& !PlayerManager.getPlayer(event.getEntered()).isVehicleListed(vehicle)) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_RIDEMOB.send(event.getEntered());
		}
	}

	/**
	 * Handles breaking paintings, item frames, leashes
	 *
	 * @param event The event
	 */
	@EventHandler
	public void onHangingEntityBreak(HangingBreakByEntityEvent event) {
		if(!(event.getRemover() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getRemover();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		boolean isLeash = event.getEntity() instanceof LeashHitch;

		if(RegionManager.get(event.getEntity()) != null
				&& (!plugin.getRegionManager().canInteract(player, event.getEntity()) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(isLeash ? GuildPermission.MOB_LEASH : GuildPermission.BLOCK_BREAK)))) {
			event.setCancelled(true);
			(isLeash ? Message.CHAT_REGION_DENY_UNLEASH : Message.CHAT_REGION_DENY_INTERACT).send(player);
		}
	}

	/**
	 * Handles placing paintings, item frames, leashes
	 *
	 * @param event The event
	 */
	@EventHandler
	public void onHangingPlace(HangingPlaceEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		Location location = event.getEntity().getLocation();

		if(RegionManager.get(location) != null
				&& (!plugin.getRegionManager().canInteract(player, location) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.BLOCK_PLACE)))) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}

	private class Non1_8Events extends AbstractListener {
		@EventHandler
		public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
			Player player = (Player) event.getEntity();
			NovaPlayer nPlayer = PlayerManager.getPlayer(player);

			if(RegionManager.get(event.getBlock()) != null
					&& (!plugin.getRegionManager().canInteract(player, event.getBlock()) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.BLOCK_PLACE)))) {
				event.setCancelled(true);
				Message.CHAT_REGION_DENY_INTERACT.send(player);
			}
		}

		@EventHandler
		public void onCraftItem(CraftItemEvent event) {
			NovaPlayer nPlayer = PlayerManager.getPlayer(event.getWhoClicked());

			if(event.getRecipe().getResult().getType() != Material.SHIELD
					|| !nPlayer.hasGuild()
					|| nPlayer.getGuild().getBannerMeta().numberOfPatterns() == 0) {
				return;
			}

			for(ItemStack ingredient : event.getInventory().getContents()) {
				if(ingredient != null
						&& ingredient.getType() == Material.SHIELD
						&& ingredient.hasItemMeta()) {
					return;
				}
			}

			event.getInventory().setResult(BannerUtils.applyMeta(event.getRecipe().getResult(), nPlayer.getGuild().getBannerMeta()));
		}

		@EventHandler
		public void onPrepareItemCraft(PrepareItemCraftEvent event) {
			if(event.getViewers().isEmpty()) {
				return;
			}

			NovaPlayer nPlayer = PlayerManager.getPlayer(event.getViewers().get(0));

			if(event.getRecipe() == null
					|| event.getRecipe().getResult() == null
					|| event.getRecipe().getResult().getType() != Material.SHIELD
					|| !nPlayer.hasGuild()
					|| nPlayer.getGuild().getBannerMeta().numberOfPatterns() == 0) {
				return;
			}

			for(ItemStack ingredient : event.getInventory().getContents()) {
				if(ingredient != null
						&& ingredient.getType() == Material.SHIELD
						&& ingredient.hasItemMeta()) {
					return;
				}
			}

			event.getInventory().setResult(BannerUtils.applyMeta(event.getRecipe().getResult(), nPlayer.getGuild().getBannerMeta()));
		}
	}

	private class Non1_7Events extends AbstractListener {
		/**
		 * Handles editing items on an ArmorStand
		 *
		 * @param event The event
		 */
		@EventHandler
		public void onPlayerManipulateArmorStand(PlayerArmorStandManipulateEvent event) {
			Player player = event.getPlayer();
			NovaPlayer nPlayer = PlayerManager.getPlayer(player);
			Location location = event.getRightClicked().getLocation();

			if(RegionManager.get(location) != null
					&& (!plugin.getRegionManager().canInteract(player, location) || (!nPlayer.getPreferences().getBypass() && !nPlayer.hasPermission(GuildPermission.INTERACT)))) {
				event.setCancelled(true);
				Message.CHAT_REGION_DENY_INTERACT.send(player);
			}
		}
	}
}
