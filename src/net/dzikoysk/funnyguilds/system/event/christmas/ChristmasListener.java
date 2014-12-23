package net.dzikoysk.funnyguilds.system.event.christmas;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChristmasListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		final ChristmasUser user = ChristmasUser.get(User.get(player));
		if(user.seeMap()) return;
		user.setTask(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), new Runnable(){
			int i = 0;
			@Override
			public void run(){
				i++;
				if(i < 4){
					player.sendMessage(StringUtils.colored("&e&lHou! Hou! Hou!"));
					return;
				}
				user.getTask().cancel();
				player.sendMessage(StringUtils.colored(Christmas.getPrefix() + "&eNadszedl czas swiat, dlatego postanowilem zawitac i tutaj z prezentami!"));
				player.sendMessage(StringUtils.colored("&eOtrzymujesz ode mnie dwie receptury na swiateczne prezenty, dobrze je wykorzystaj."));
				player.sendMessage(StringUtils.colored(""));
				player.sendMessage(StringUtils.colored("&eJestes ciekawy jak wyglada sytuacja pod serwerowa choinka? Wpisz &c/choinka &e aby zobaczyc postepy calego serwera"));
				player.sendMessage(StringUtils.colored("&eNajbardziej wkreconych w swiateczne wydarzenia znajdziesz pod &c/prezenty"));
				player.sendMessage(StringUtils.colored("&eJezeli chcesz sprawdzic wlasne postepy wpisz &c/prezent"));
				ChristmasRecipe.getInstance().sendGiftRecipe(player, 1);
				ChristmasRecipe.getInstance().sendGiftRecipe(player, 2);
				user.setSeeMap(true);
			}
		}, 50, 35));
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlace(BlockPlaceEvent event){
		ItemStack item = event.getItemInHand();
		String name = item.getItemMeta().getDisplayName();
		String present = null;
		if(item == null || name == null || item.getType() != Material.NOTE_BLOCK) return;
		if(name.equals(ChristmasGift.getRedGift().getItemMeta().getDisplayName())) present = "CruXXx";
		else if(name.equals(ChristmasGift.getGreenGift().getItemMeta().getDisplayName())) present = "I_Xenon_I";
		else if(name.equals(ChristmasGift.getEmeraldGift().getItemMeta().getDisplayName())) present = "SeerPotion";
		if(present != null){
			Location loc = event.getBlockPlaced().getLocation();
			Block block = loc.getBlock();
			block.setType(Material.SKULL);
			block.setData(((byte) 0x1));
			Skull skull = (Skull) block.getState();
			skull.setOwner(present);
		    skull.update(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		if(block.getType() != Material.SKULL) return;
		Skull skull = (Skull) block.getState();
		String owner = skull.getOwner();
		if(!(owner.equals("CruXXx") || owner.equals("I_Xenon_I") || owner.equals("SeerPotion"))) return;
		ChristmasUser cu = ChristmasUser.get(User.get(event.getPlayer()));
		cu.addGift();
		event.setCancelled(true);
		block.setType(Material.AIR);
		ItemStack spawn = null;
		if(owner.equals("CruXXx")){
			cu.addRedGift();
			if(ChristmasUtils.chance(25)) spawn = ChristmasGift.getRedGift();
			else if(ChristmasUtils.chance(20)) spawn = ChristmasGift.getGreenGift();
			else if(ChristmasUtils.chance(13)) spawn = new ItemStack(Material.GOLD_INGOT);
			else if(ChristmasUtils.chance(8)) spawn = new ItemStack(Material.TNT);
			else if(ChristmasUtils.chance(5)) spawn = new ItemStack(Material.DIAMOND);
			else if(ChristmasUtils.chance(1)) spawn = ChristmasGift.getEmeraldGift();
		} else if(owner.equals("I_Xenon_I")){
			cu.addGreenGift();
			if(ChristmasUtils.chance(25)) spawn = ChristmasGift.getRedGift();
			else if(ChristmasUtils.chance(20)) spawn = ChristmasGift.getGreenGift();
			else if(ChristmasUtils.chance(16)) spawn = new ItemStack(Material.IRON_INGOT);
			else if(ChristmasUtils.chance(8)) spawn = new ItemStack(Material.GOLDEN_APPLE);
			else if(ChristmasUtils.chance(4)) spawn = new ItemStack(Material.EMERALD);
			else if(ChristmasUtils.chance(1)) spawn = ChristmasGift.getEmeraldGift();
		} else if(owner.equals("SeerPotion")){
			cu.addEmeraldGift();
			if(ChristmasUtils.chance(1)){
				User user = User.get(event.getPlayer());
				if(user.hasGuild()) user.getGuild().addLive();
				event.getPlayer().sendMessage(StringUtils
						.colored(Christmas.getPrefix() + "Otrzymujesz &c1 zycie &fdla swojej gildii!"));
			}
			else if(ChristmasUtils.chance(5)) {
				User.get(event.getPlayer()).setBypass(true);
				event.getPlayer().sendMessage(StringUtils
						.colored(Christmas.getPrefix() + "Otrzymujesz mozliwosc zalozenia gildii &cza darmo&f!"));
				event.getPlayer().sendMessage(StringUtils
						.colored(Christmas.getPrefix() + "Pamietaj, ze &cmusisz sie spieszyc&f - po wyjsciu z serwera oferta przestanie byc wazna!"));
			}
			else if(ChristmasUtils.chance(8)) spawn = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
			else if(ChristmasUtils.chance(10)) spawn = new ItemStack(Material.DIAMOND, 5);
			else if(ChristmasUtils.chance(10)) spawn = new ItemStack(Material.EMERALD, 5);
			else if(ChristmasUtils.chance(80)){
				spawn = new ItemStack(Material.COOKIE);
				ItemMeta im = spawn.getItemMeta();
				im.setDisplayName("Swiateczne Ciastko");
				spawn.setItemMeta(im);
			}
		} else return;
		if(spawn != null){
			block.getWorld().dropItem(block.getLocation(), spawn);
			String message = null;
			String name = spawn.getItemMeta().getDisplayName();
			if(name != null) message = (Christmas.getPrefix() + "Otrzymujesz " + name + "&f!").replace("  ", "");
			else message = Christmas.getPrefix() + "Otrzymujesz &c" + spawn.getType().toString().toLowerCase().replace("_", " ") + "&f!";
			event.getPlayer().sendMessage(StringUtils.colored(message));
		}
	}
}
