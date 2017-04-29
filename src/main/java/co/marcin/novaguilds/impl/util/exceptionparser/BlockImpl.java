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

package co.marcin.novaguilds.impl.util.exceptionparser;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.exceptionparser.Block;
import org.apache.commons.lang.StringUtils;

public class BlockImpl implements Block {
	private final String name;
	private final String message;
	private final String stackTraceElement;

	/**
	 * The constructor
	 *
	 * @param throwable the exception or one of its causes
	 */
	public BlockImpl(Throwable throwable) {
		this(throwable.getClass().getSimpleName(), throwable.getMessage(), throwable.getStackTrace()[0].toString());
	}

	/**
	 * The constructor
	 *
	 * @param name              exception name
	 * @param message           exception message
	 * @param stackTraceElement first stacktrace element
	 */
	public BlockImpl(String name, String message, String stackTraceElement) {
		if(StringUtils.startsWith(stackTraceElement, NovaGuilds.class.getPackage().getName())) {
			StringUtils.replaceOnce(stackTraceElement, NovaGuilds.class.getPackage().getName() + ".", "");
		}

		this.name = name;
		this.message = message;
		this.stackTraceElement = stackTraceElement;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getStackTraceElement() {
		return stackTraceElement;
	}
}
