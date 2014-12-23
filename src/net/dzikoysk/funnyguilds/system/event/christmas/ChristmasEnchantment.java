package net.dzikoysk.funnyguilds.system.event.christmas;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;


public class ChristmasEnchantment extends Enchantment {

	public ChristmasEnchantment(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack is) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment e) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public int getMaxLevel() {
		return 24;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

}
