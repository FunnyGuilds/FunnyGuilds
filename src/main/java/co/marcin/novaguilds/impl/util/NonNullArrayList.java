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

import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.Collection;

public class NonNullArrayList<E> extends ArrayList<E> {
	@Override
	public boolean add(E e) {
		Validate.notNull(e);
		return super.add(e);
	}

	@Override
	public void add(int index, E element) {
		Validate.notNull(element);
		super.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for(E e : c) {
			Validate.notNull(e);
		}

		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		for(E e : c) {
			Validate.notNull(e);
		}

		return super.addAll(index, c);
	}
}
