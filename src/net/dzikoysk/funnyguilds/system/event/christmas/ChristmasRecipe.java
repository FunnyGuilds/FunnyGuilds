package net.dzikoysk.funnyguilds.system.event.christmas;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

@SuppressWarnings("deprecation")
public class ChristmasRecipe {
	
	private static ChristmasRecipe instance;
	private short red = 1;
	private short lime = 10;
	private short yellow = 11;
	private ShapedRecipe redGift;
	private ShapedRecipe greenGift;
	private ItemStack redRecipeMap;
	private ItemStack greenRecipeMap;
	private ItemStack[] redIngredients = new ItemStack[] {
		new ItemStack(Material.INK_SACK, 1, red),
		new ItemStack(Material.INK_SACK, 1, yellow),
		new ItemStack(Material.INK_SACK, 1, red),
		new ItemStack(Material.INK_SACK, 1, yellow),
		new ItemStack(Material.INK_SACK, 1, yellow),
		new ItemStack(Material.INK_SACK, 1, yellow),
		new ItemStack(Material.INK_SACK, 1, red),
		new ItemStack(Material.INK_SACK, 1, yellow),
		new ItemStack(Material.INK_SACK, 1, red)
	};
	private ItemStack[] greenIngredients = new ItemStack[] {
		new ItemStack(Material.INK_SACK, 1, lime),
		new ItemStack(Material.INK_SACK, 1, red),
		new ItemStack(Material.INK_SACK, 1, lime),
		new ItemStack(Material.INK_SACK, 1, red),
		new ItemStack(Material.INK_SACK, 1, red),
		new ItemStack(Material.INK_SACK, 1, red),
		new ItemStack(Material.INK_SACK, 1, lime),
		new ItemStack(Material.INK_SACK, 1, red),
		new ItemStack(Material.INK_SACK, 1, lime)
	};
	
	
	
	public ChristmasRecipe(){
		instance = this;

		ShapedRecipe redRecipe = new ShapedRecipe(ChristmasGift.getRedGift());
		redRecipe.shape("012", "345", "678");
		for(Integer i = 0; i < 9; i++){
			redRecipe.setIngredient(i.toString().charAt(0), redIngredients[i].getType(), redIngredients[i].getDurability());
		}
		redGift = redRecipe;
		
		ShapedRecipe greenRecipe = new ShapedRecipe(ChristmasGift.getGreenGift());
		greenRecipe.shape("012", "345", "678");
		for(Integer i = 0; i < 9; i++){
			greenRecipe.setIngredient(i.toString().charAt(0), greenIngredients[i].getType(), greenIngredients[i].getDurability());
		}
		greenGift = greenRecipe;
		
		MapView view = Bukkit.createMap(Bukkit.getWorlds().get(0));
		view.getRenderers().clear();
		view.addRenderer(new ChristmasMap(1));
		redRecipeMap = new ItemStack(Material.MAP, 1, view.getId());
		ItemMeta redMeta = redRecipeMap.getItemMeta();
		redMeta.setDisplayName(ChatColor.RED + "Crafting Czerwonego Prezentu");
		redRecipeMap.setItemMeta(redMeta);
		
		view = Bukkit.createMap(Bukkit.getWorlds().get(0));
		view.getRenderers().clear();
		view.addRenderer(new ChristmasMap(2));
		greenRecipeMap = new ItemStack(Material.MAP, 1, view.getId());
		ItemMeta greenMeta = greenRecipeMap.getItemMeta();
		greenMeta.setDisplayName(ChatColor.GREEN + "Crafting Zielonego Prezentu");
		greenRecipeMap.setItemMeta(greenMeta);
	}
	
	public void register(){
		Bukkit.addRecipe(redGift);
		Bukkit.addRecipe(greenGift);
	}
	
	public void sendGiftRecipe(Player player, int i){
		switch(i){
		case 1: 
			player.getInventory().addItem(redRecipeMap);
			break;
		case 2: 
			player.getInventory().addItem(greenRecipeMap);
			break;
		}
	}

	public static ChristmasRecipe getInstance(){
		return instance;
	}
}
