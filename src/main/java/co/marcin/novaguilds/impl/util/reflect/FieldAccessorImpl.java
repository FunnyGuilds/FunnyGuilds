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

package co.marcin.novaguilds.impl.util.reflect;

import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.util.reflect.Reflections;

import java.lang.reflect.Field;

public class FieldAccessorImpl<T> implements FieldAccessor<T> {
	private final Field field;

	/**
	 * The constructor
	 *
	 * @param field the field
	 */
	public FieldAccessorImpl(Field field) {
		this.field = field;
	}

	@Override
	public Field getField() {
		return field;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(Object target) {
		try {
			return (T) field.get(target);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException("Cannot access reflection.", e);
		}
	}

	@Override
	public T get() {
		return get(null);
	}

	@Override
	public String getName() {
		return field.getName();
	}

	@Override
	public void set(T value) {
		set(null, value);
	}

	@Override
	public void set(Object target, Object value) {
		try {
			field.set(target, value);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException("Cannot access reflection.", e);
		}
	}

	@Override
	public boolean hasField(Object target) {
		return field.getDeclaringClass().isAssignableFrom(target.getClass());
	}

	@Override
	public void setNotFinal() {
		try {
			Reflections.setNotFinal(field);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException("Cannot access reflection.", e);
		}
	}
}
