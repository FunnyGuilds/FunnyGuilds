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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.TabList;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.VarKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTabList implements TabList {
	protected final List<String> lines = new ArrayList<>();
	private final NovaPlayer nPlayer;
	private final Map<VarKey, String> vars = new HashMap<>();

	/**
	 * The constructor
	 *
	 * @param nPlayer tablist owner
	 */
	public AbstractTabList(NovaPlayer nPlayer) {
		this.nPlayer = nPlayer;
		clear();
	}

	@Override
	public NovaPlayer getPlayer() {
		return nPlayer;
	}

	@Override
	public Map<VarKey, String> getVars() {
		return vars;
	}

	@Override
	public void clear() {
		lines.clear();
		lines.addAll(Config.TABLIST_SCHEME.getStringList());
		vars.clear();
	}
}
