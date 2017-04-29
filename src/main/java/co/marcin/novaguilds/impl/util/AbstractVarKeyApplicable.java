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

import co.marcin.novaguilds.api.util.VarKeyApplicable;
import co.marcin.novaguilds.enums.VarKey;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class AbstractVarKeyApplicable<T extends VarKeyApplicable> implements VarKeyApplicable<T> {
	protected final Map<VarKey, String> vars = new HashMap<>();
	protected int varsHashCode = vars.hashCode();

	@Override
	public Map<VarKey, String> getVars() {
		return vars;
	}

	@Override
	public T vars(Map<VarKey, String> vars) {
		this.vars.clear();
		this.vars.putAll(vars);
		return (T) this;
	}

	@Override
	public T setVar(VarKey varKey, String value) {
		vars.put(varKey, value);
		return (T) this;
	}

	@Override
	public T setVar(VarKey varKey, Integer value) {
		return setVar(varKey, String.valueOf(value));
	}

	@Override
	public T setVar(VarKey varKey, Double value) {
		return setVar(varKey, String.valueOf(value));
	}

	@Override
	public boolean isChanged() {
		if(varsHashCode == vars.hashCode()) {
			return false;
		}

		varsHashCode = vars.hashCode();
		return true;
	}
}
