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

package co.marcin.novaguilds.impl.util.converter;

import co.marcin.novaguilds.api.util.IConverter;
import co.marcin.novaguilds.util.LoggerUtils;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractConverter<K, V> implements IConverter<K, V> {
	@Override
	public Collection<V> convert(Collection<K> list) {
		final Collection<V> convertedList = new ArrayList<>();

		for(K k : list) {
			V converted = convert(k);

			if(converted == null) {
				LoggerUtils.debug("[" + getClass().getSimpleName() + "] Converted item: " + k.toString() + " is null", false);
				continue;
			}

			convertedList.add(converted);
		}

		return convertedList;
	}
}
