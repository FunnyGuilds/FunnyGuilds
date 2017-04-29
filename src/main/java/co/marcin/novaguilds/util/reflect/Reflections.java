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

package co.marcin.novaguilds.util.reflect;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.api.util.reflect.MethodInvoker;
import co.marcin.novaguilds.impl.util.reflect.FieldAccessorImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public final class Reflections {
	private static Method entityGetHandleMethod;
	private static Method worldGetHandleMethod;
	private static Field modifiersField;

	static {
		try {
			if(NovaGuilds.getInstance() != null) {
				Class<?> craftWorldClass = getBukkitClass("CraftWorld");
				Class<?> craftEntityClass = getBukkitClass("entity.CraftEntity");
				worldGetHandleMethod = getMethod(craftWorldClass, "getHandle");
				entityGetHandleMethod = getMethod(craftEntityClass, "getHandle");
				modifiersField = getPrivateField(Field.class, "modifiers");
			}
		}
		catch(NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Gets a class
	 *
	 * @param name class name
	 * @return class instance
	 * @throws ClassNotFoundException when the class doesn't exist
	 */
	public static Class<?> getClass(String name) throws ClassNotFoundException {
		return Class.forName(name);
	}

	/**
	 * Gets NMS class
	 *
	 * @param name class name
	 * @return class
	 * @throws ClassNotFoundException when the class doesn't exist
	 */
	public static Class<?> getCraftClass(String name) throws ClassNotFoundException {
		return getClass("net.minecraft.server." + getVersion() + name);
	}

	/**
	 * Gets CraftBukkit class
	 *
	 * @param name class name
	 * @return class
	 * @throws ClassNotFoundException when the class doesn't exist
	 */
	public static Class<?> getBukkitClass(String name) throws ClassNotFoundException {
		return getClass("org.bukkit.craftbukkit." + getVersion() + name);
	}

	/**
	 * Gets the handle of an entity
	 *
	 * @param entity target entity
	 * @return entity handle
	 * @throws InvocationTargetException when something goes wrong
	 * @throws IllegalAccessException    when something goes wrong
	 */
	public static Object getHandle(Entity entity) throws InvocationTargetException, IllegalAccessException {
		return entityGetHandleMethod.invoke(entity);
	}

	/**
	 * Gets the handle of a world
	 *
	 * @param world target world
	 * @return world handle
	 * @throws InvocationTargetException when something goes wrong
	 * @throws IllegalAccessException    when something goes wrong
	 */
	public static Object getHandle(World world) throws InvocationTargetException, IllegalAccessException {
		return worldGetHandleMethod.invoke(world);
	}

	/**
	 * Gets a field
	 *
	 * @param clazz     class
	 * @param fieldName field name
	 * @return field
	 * @throws NoSuchFieldException when something goes wrong
	 */
	public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		return clazz.getDeclaredField(fieldName);
	}

	/**
	 * Gets a field
	 *
	 * @param target    class
	 * @param fieldType field type
	 * @param index     index
	 * @param <T>       type
	 * @return field accessor
	 * @throws NoSuchFieldException when something goes wrong
	 */
	public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) throws NoSuchFieldException {
		return getField(target, null, fieldType, index);
	}

	/**
	 * Gets a field
	 *
	 * @param target    class
	 * @param name      field name
	 * @param fieldType field type
	 * @param <T>       type
	 * @return field accessor
	 * @throws NoSuchFieldException when something goes wrong
	 */
	public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) throws NoSuchFieldException {
		return getField(target, name, fieldType, 0);
	}

	/**
	 * Gets a field
	 *
	 * @param target    class
	 * @param name      field name
	 * @param fieldType field type
	 * @param index     index
	 * @param <T>       type
	 * @return field accessor
	 * @throws NoSuchFieldException when something goes wrong
	 */
	private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) throws NoSuchFieldException {
		for(final Field field : target.getDeclaredFields()) {
			if((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
				field.setAccessible(true);
				return new FieldAccessorImpl<>(field);
			}
		}

		if(target.getSuperclass() != null) {
			return getField(target.getSuperclass(), name, fieldType, index);
		}

		throw new NoSuchFieldException("Cannot find field with type " + fieldType);
	}

	/**
	 * Gets a private field
	 *
	 * @param clazz     class
	 * @param fieldName field name
	 * @return field
	 * @throws NoSuchFieldException when a field doesn't exist
	 * @throws NoSuchFieldException when something goes wrong
	 */
	public static Field getPrivateField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}

	/**
	 * Gets fields
	 *
	 * @param clazz class
	 * @param type  searched field type
	 * @param <T>   type parameter
	 * @return set with fields
	 */
	public static <T> Set<FieldAccessor<T>> getFields(Class<?> clazz, Class<T> type) {
		Set<FieldAccessor<T>> collection = new HashSet<>();

		for(Field field : clazz.getFields()) {
			if(!field.getType().equals(type)) {
				continue;
			}

			collection.add(new FieldAccessorImpl<T>(field));
		}

		return collection;
	}

	/**
	 * Gets a method
	 *
	 * @param clazz  class
	 * @param method method name
	 * @param args   argument classes
	 * @return method
	 * @throws NoSuchMethodException when something goes wrong
	 */
	public static Method getMethod(Class<?> clazz, String method, Class<?>... args) throws NoSuchMethodException {
		for(Method m : clazz.getMethods()) {
			if(m.getName().equals(method) && classListEqual(args, m.getParameterTypes())) {
				return m;
			}
		}

		throw new NoSuchMethodException("Could not access the method");
	}

	/**
	 * Gets a method
	 *
	 * @param clazz  class
	 * @param method method name
	 * @return method
	 * @throws NoSuchMethodException when something goes wrong
	 */
	public static Method getMethod(Class<?> clazz, String method) throws NoSuchMethodException {
		for(Method m : clazz.getMethods()) {
			if(m.getName().equals(method)) {
				return m;
			}
		}

		throw new NoSuchMethodException("Could not access the method");
	}

	/**
	 * Sets the field as not final
	 *
	 * @param field field
	 * @throws IllegalAccessException when something goes wrong
	 */
	public static void setNotFinal(Field field) throws IllegalAccessException {
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}

	/**
	 * Gets a method
	 *
	 * @param clazz      class
	 * @param type       return type
	 * @param methodName method name
	 * @param args       argument types
	 * @param <T>        return type
	 * @return method invoker
	 * @throws NoSuchMethodException when something goes wrong
	 */
	public static <T> MethodInvoker<T> getMethod(final Class<?> clazz, final Class<T> type, final String methodName, final Class<?>... args) throws NoSuchMethodException {
		return new MethodInvoker<T>() {
			private final Method method;

			{
				if(args.length == 0) {
					method = getMethod(clazz, methodName);
				}
				else {
					method = getMethod(clazz, methodName, args);
				}

				if(!method.getReturnType().equals(type)) {
					throw new IllegalArgumentException("Invalid return type. " + type.getName() + " assumed, got " + method.getReturnType().getName());
				}
			}

			@Override
			@SuppressWarnings("unchecked")
			public T invoke(Object target, Object... arguments) {
				try {
					return (T) method.invoke(target, arguments);
				}
				catch(IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException("Cannot access reflection.", e);
				}
			}
		};
	}

	/**
	 * Compares two lists of classes
	 *
	 * @param l1 list 1
	 * @param l2 list 2
	 * @return boolean
	 */
	public static boolean classListEqual(Class<?>[] l1, Class<?>[] l2) {
		if(l1.length != l2.length) {
			return false;
		}

		for(int i = 0; i < l1.length; i++) {
			if(l1[i] != l2[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Converts the value to an enum
	 *
	 * @param clazz enum class
	 * @param name enum constant name
	 * @return enum value
	 */
	public static Enum getEnumConstant(Class<?> clazz, String name) {
		if(!clazz.isEnum()) {
			throw new IllegalArgumentException("Class" + clazz.getName() + " is not an enum");
		}

		for(Object enumConstant : clazz.getEnumConstants()) {
			if(((Enum) enumConstant).name().equalsIgnoreCase(name)) {
				return (Enum) enumConstant;
			}
		}

		throw new IllegalArgumentException("Could not find enum constant");
	}

	/**
	 * Gets CraftBukkit version
	 *
	 * @return the version
	 */
	public static String getVersion() {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		return name.substring(name.lastIndexOf('.') + 1) + ".";
	}

	public interface ConstructorInvoker<T> {
		/**
		 * Invokes a constructor
		 *
		 * @param arguments arguments
		 * @return instance
		 */
		T invoke(Object... arguments);
	}
}
