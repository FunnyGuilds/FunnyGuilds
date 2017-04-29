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

package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.api.util.Schematic;
import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.util.SchematicImpl;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NovaGroupImpl implements NovaGroup {
	public static class Key<T> implements NovaGroup.Key<T> {
		public static final NovaGroup.Key<Double> CREATE_MONEY = new NovaGroupImpl.Key<>(Double.class);
		public static final NovaGroup.Key<List<ItemStack>> CREATE_ITEMS = new NovaGroupImpl.Key<>();
		public static final NovaGroup.Key<Schematic> CREATE_SCHEMATIC = new NovaGroupImpl.Key<>(Schematic.class);
		public static final NovaGroup.Key<Integer> HOME_DELAY = new NovaGroupImpl.Key<>(Integer.class);
		public static final NovaGroup.Key<Double> HOME_MONEY = new NovaGroupImpl.Key<>(Double.class);
		public static final NovaGroup.Key<List<ItemStack>> HOME_ITEMS = new NovaGroupImpl.Key<>();
		public static final NovaGroup.Key<Double> JOIN_MONEY = new NovaGroupImpl.Key<>(Double.class);
		public static final NovaGroup.Key<List<ItemStack>> JOIN_ITEMS = new NovaGroupImpl.Key<>();
		public static final NovaGroup.Key<Double> EFFECT_MONEY = new NovaGroupImpl.Key<>(Double.class);
		public static final NovaGroup.Key<List<ItemStack>> EFFECT_ITEMS = new NovaGroupImpl.Key<>();
		public static final NovaGroup.Key<Double> BUY_LIFE_MONEY = new NovaGroupImpl.Key<>(Double.class);
		public static final NovaGroup.Key<List<ItemStack>> BUY_LIFE_ITEMS = new NovaGroupImpl.Key<>();
		public static final NovaGroup.Key<Double> BUY_SLOT_MONEY = new NovaGroupImpl.Key<>(Double.class);
		public static final NovaGroup.Key<List<ItemStack>> BUY_SLOT_ITEMS = new NovaGroupImpl.Key<>();
		public static final NovaGroup.Key<Double> BUY_BANNER_MONEY = new NovaGroupImpl.Key<>(Double.class);
		public static final NovaGroup.Key<List<ItemStack>> BUY_BANNER_ITEMS = new NovaGroupImpl.Key<>();
		public static final NovaGroup.Key<Double> REGION_CREATE_MONEY = new NovaGroupImpl.Key<>(Double.class);
		public static final NovaGroup.Key<Double> REGION_PRICEPERBLOCK = new NovaGroupImpl.Key<>(Double.class);
		public static final NovaGroup.Key<Integer> REGION_AUTOSIZE = new NovaGroupImpl.Key<>(Integer.class);

		private final Class<T> type;

		/**
		 * The constructor
		 */
		@SuppressWarnings("unchecked")
		public Key() {
			this((Class<T>) List.class);
		}

		/**
		 * The constructor
		 *
		 * @param type value type
		 */
		public Key(Class<T> type) {
			this.type = type;
		}

		/**
		 * Gets variable type
		 *
		 * @return the type
		 */
		@Override
		@SuppressWarnings("unchecked")
		public Class<T> getType() {
			return type;
		}

		/**
		 * Gets all values
		 *
		 * @return array of values
		 */
		public static NovaGroup.Key<?>[] values() {
			final List<NovaGroup.Key<?>> values = new ArrayList<>();

			for(FieldAccessor<NovaGroup.Key> fieldAccessor : Reflections.getFields(NovaGroupImpl.Key.class, NovaGroup.Key.class)) {
				values.add(fieldAccessor.get());
			}

			return values.toArray(new NovaGroup.Key<?>[values.size()]);
		}
	}

	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private static final Map<NovaGroup.Key<?>, String> paths = new HashMap<NovaGroup.Key<?>, String>() {{
		put(Key.CREATE_MONEY, "guild.create.money");
		put(Key.CREATE_ITEMS, "guild.create.items");
		put(Key.CREATE_SCHEMATIC, "guild.create.schematic");
		put(Key.HOME_DELAY, "guild.home.tpdelay");
		put(Key.HOME_MONEY, "guild.home.money");
		put(Key.HOME_ITEMS, "guild.home.items");
		put(Key.JOIN_MONEY, "guild.join.money");
		put(Key.JOIN_ITEMS, "guild.join.items");
		put(Key.EFFECT_MONEY, "guild.effect.money");
		put(Key.EFFECT_ITEMS, "guild.effect.items");
		put(Key.BUY_LIFE_MONEY, "guild.buylife.money");
		put(Key.BUY_LIFE_ITEMS, "guild.buylife.items");
		put(Key.BUY_SLOT_MONEY, "guild.buyslot.money");
		put(Key.BUY_SLOT_ITEMS, "guild.buyslot.items");
		put(Key.BUY_BANNER_MONEY, "guild.banner.money");
		put(Key.BUY_BANNER_ITEMS, "guild.banner.items");
		put(Key.REGION_CREATE_MONEY, "region.createmoney");
		put(Key.REGION_PRICEPERBLOCK, "region.ppb");
		put(Key.REGION_AUTOSIZE, "region.autoregionsize");
	}};

	private final String name;
	private final Map<NovaGroup.Key<?>, Object> values = new HashMap<>();

	/**
	 * The constructor
	 *
	 * @param group group name
	 */
	public NovaGroupImpl(String group) {
		name = group;
		LoggerUtils.info("Loading group '" + name + "'...");

		//setting all values
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("groups." + group);

		for(NovaGroup.Key<?> key : Key.values()) {
			String path = paths.get(key);

			if(!section.contains(path) && values.get(key) != null) {
				continue;
			}

			Object value = null;

			if(key.getType() == Double.class) {
				value = section.getDouble(path);
			}
			else if(key.getType() ==  Integer.class) {
				value = section.getInt(path);
			}
			else if(key.getType() == List.class) {
				value = ItemStackUtils.stringToItemStackList(section.getStringList(path));

				if(value == null) {
					value = new ArrayList<ItemStack>();
				}
			}
			else if(key.getType() == Schematic.class) {
				String schematicName = section.getString(path);
				if(schematicName != null && !schematicName.isEmpty()) {
					try {
						value = new SchematicImpl(schematicName);
					}
					catch(FileNotFoundException e) {
						LoggerUtils.error("Schematic not found: schematic/" + schematicName);
					}
				}
			}

			values.put(key, value);
		}

		int autoRegionWidth = get(Key.REGION_AUTOSIZE) * 2 + 1;
		if(autoRegionWidth > Config.REGION_MAXSIZE.getInt()) {
			values.put(Key.REGION_AUTOSIZE, Config.REGION_MAXSIZE.getInt() / 2 - 1);
			LoggerUtils.error("Group " + name + " has too big autoregion. Reset to " + get(Key.REGION_AUTOSIZE));
		}

		if(autoRegionWidth < Config.REGION_MINSIZE.getInt()) {
			values.put(Key.REGION_AUTOSIZE, Config.REGION_MINSIZE.getInt() / 2);
			LoggerUtils.error("Group " + name + " has too small autoregion. Reset to " + get(Key.REGION_AUTOSIZE));
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(NovaGroup.Key<T> key) {
		T value = (T) values.get(key);

		if(key.getType() == List.class) {
			List<ItemStack> clonedList = new ArrayList<>();
			for(ItemStack itemStack : (List<ItemStack>) value) {
				ItemStack clonedItemStack = itemStack.clone();
				clonedList.add(clonedItemStack);
			}

			return (T) clonedList;
		}

		return value;
	}
}
