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
import co.marcin.novaguilds.api.basic.ConfigWrapper;
import co.marcin.novaguilds.impl.util.AbstractVarKeyApplicable;
import co.marcin.novaguilds.manager.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConfigWrapperImpl extends AbstractVarKeyApplicable<ConfigWrapper> implements ConfigWrapper {
	public static class Typed<T> extends ConfigWrapperImpl implements ConfigWrapper.Typed<T> {
		private Class<T> type;

		/**
		 * The constructor
		 *
		 * @param path      path
		 * @param fixColors fix colors
		 * @param type      type class
		 */
		public Typed(String path, boolean fixColors, Class<T> type) {
			super(path, fixColors);
			this.type = type;
		}

		/**
		 * The constructor
		 *
		 * @param configWrapper config wrapper
		 * @param type          type class
		 */
		public Typed(ConfigWrapper configWrapper, Class<T> type) {
			super(configWrapper);
			this.type = type;
		}

		/**
		 * The constructor
		 * Empty typed wrapper
		 *
		 * @param type type
		 */
		public Typed(Class<T> type) {
			super(null, false);
			this.type = type;
		}

		/**
		 * The constructor
		 * This constructor inherits everything
		 * from the config wrapper and sets the type
		 * from the second parameter
		 *
		 * @param configWrapper config wrapper
		 * @param typed         typed wrapper
		 */
		public Typed(ConfigWrapper configWrapper, ConfigWrapperImpl.Typed<T> typed) {
			super(configWrapper);
			this.type = typed.type;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get() {
			T r = cM.isInCache(this)
					? (T) cM.getEnumConfig(this)
					: cM.get(this, type);
			cM.putInCache(this, r);
			return r;
		}
	}

	protected static final ConfigManager cM = NovaGuilds.getInstance() == null ? null : NovaGuilds.getInstance().getConfigManager();
	protected String path;
	protected boolean fixColors;

	/**
	 * The constructor
	 *
	 * @param path      yaml path
	 * @param fixColors fix colors flag
	 */
	public ConfigWrapperImpl(String path, boolean fixColors) {
		this.path = path;
		this.fixColors = fixColors;
	}

	/**
	 * The constructor
	 * copies the wrapper
	 *
	 * @param configWrapper wrapper
	 */
	public ConfigWrapperImpl(ConfigWrapper configWrapper) {
		this(configWrapper.getPath(), configWrapper.isFixColors());
	}

	@Override
	public String getName() {
		return StringUtils.replace(path, ".", "_").toUpperCase();
	}

	@Override
	public String getPath() {
		if(path == null) {
			throw new IllegalArgumentException("Path has not been set!");
		}

		return path;
	}

	@Override
	public boolean isFixColors() {
		return fixColors;
	}

	@Override
	public boolean isEmpty() {
		return cM.getConfig().get(getPath()).equals("none");
	}

	@Override
	public boolean isList() {
		return cM.getConfig().isList(getPath());
	}

	@Override
	public String getString() {
		return cM.get(this, String.class);
	}

	@Override
	public List<String> getStringList() {
		List<String> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<String>) cM.getEnumConfig(this) : cM.getStringList(path, vars, fixColors);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public List<ItemStack> getItemStackList() {
		List<ItemStack> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<ItemStack>) cM.getEnumConfig(this) : cM.getItemStackList(path, vars);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public List<Material> getMaterialList() {
		List<Material> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<Material>) cM.getEnumConfig(this) : cM.getMaterialList(path, vars);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public long getLong() {
		return cM.get(this, Long.class);
	}

	@Override
	public double getDouble() {
		return cM.get(this, Double.class);
	}

	@Override
	public int getInt() {
		return cM.get(this, Integer.class);
	}

	@Override
	public boolean getBoolean() {
		return cM.get(this, Boolean.class);
	}

	@Override
	public int getSeconds() {
		int r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Integer ? (int) cM.getEnumConfig(this) : cM.getSeconds(path);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public ItemStack getItemStack() {
		return cM.get(this, ItemStack.class);
	}

	@Override
	public Material getMaterial() {
		return cM.get(this, Material.class);
	}

	@Override
	public byte getMaterialData() {
		byte r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Byte ? (byte) cM.getEnumConfig(this) : cM.getMaterialData(path);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public double getPercent() {
		return getDouble() / 100;
	}

	@Override
	public ConfigurationSection getConfigurationSection() {
		return cM.getConfig().getConfigurationSection(path);
	}

	@Override
	public void set(Object obj) {
		cM.set(this, obj);
	}

	@Override
	public void setPath(String path) {
		if(path != null) {
			throw new IllegalArgumentException("Path already set");
		}

		this.path = path;
	}

	@Override
	public void setFixColors(boolean b) {
		this.fixColors = b;
	}

	@Override
	public <E extends Enum> E toEnum(Class<E> clazz) {
		for(E enumConstant : clazz.getEnumConstants()) {
			if(enumConstant.name().equalsIgnoreCase(getString())) {
				return enumConstant;
			}
		}

		return null;
	}

	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public ConfigWrapper clone() {
		return new ConfigWrapperImpl(this);
	}
}
