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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.exceptionparser.IError;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.impl.util.exceptionparser.ErrorImpl;

import java.util.Collection;
import java.util.logging.Logger;

public final class LoggerUtils {
	private static final Logger logger = Logger.getLogger("Minecraft");
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private static final String logPrefix = "[NovaGuilds]";

	private LoggerUtils() {

	}

	/**
	 * Sends error message
	 *
	 * @param error       message string
	 * @param classPrefix include class prefix
	 */
	public static void error(String error, boolean classPrefix) {
		logger.severe(StringUtils.fixColors(logPrefix + (classPrefix ? classPrefix() : "") + space(error) + error));
	}

	/**
	 * Sends error message
	 *
	 * @param error       message string array
	 * @param classPrefix include class prefix
	 */
	public static void error(Collection<String> error, boolean classPrefix) {
		for(String string : error) {
			error(string, classPrefix);
		}
	}

	/**
	 * Sends error message
	 * includes class prefix
	 *
	 * @param error message string
	 */
	public static void error(String error) {
		error(error, true);
	}

	/**
	 * Sends info message
	 * includes class prefix
	 *
	 * @param msg message string
	 */
	public static void info(String msg) {
		info(msg, true);
	}

	/**
	 * Sends info message
	 *
	 * @param msg         message string
	 * @param classPrefix include class prefix
	 */
	public static void info(String msg, boolean classPrefix) {
		if(msg == null) {
			msg = "null";
		}

		logger.info(StringUtils.fixColors(logPrefix + (classPrefix ? classPrefix() : "") + space(msg) + msg));
	}

	/**
	 * Sends debug message
	 * includes class prefix
	 *
	 * @param msg message string
	 */
	public static void debug(String msg) {
		debug(msg, true);
	}

	/**
	 * Sends debug message
	 *
	 * @param msg         message string
	 * @param classPrefix include class prefix
	 */
	public static void debug(String msg, boolean classPrefix) {
		if(plugin != null && plugin.getConfigManager() != null) {
			if(Config.DEBUG.getBoolean()) {
				info("[DEBUG] " + (classPrefix ? classPrefix() : "") + msg, false);
			}
		}
	}

	/**
	 * Gets the prefix of a class where a message is being sent
	 *
	 * @return class prefix
	 */
	private static String classPrefix() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String line = ste[4].toString();
		String[] split1 = org.apache.commons.lang.StringUtils.split(line, '(');
		String[] split2 = split1[1].split(":");
		String className = org.apache.commons.lang.StringUtils.replace(split2[0], ".java", "");
		return className.equals("NovaGuilds") ? "" : "[" + className + "]";
	}

	/**
	 * Creates a space if the string does not contain 'Manager' word
	 *
	 * @param string string
	 * @return string with or without a space
	 */
	private static String space(String string) {
		return string.contains("Manager]") ? "" : " ";
	}

	/**
	 * Handles an exception
	 *
	 * @param exception the exception
	 */
	public static void exception(Throwable exception) {
		IError error = new ErrorImpl(exception);
		error(error.getConsoleOutput(), false);
		plugin.getErrorManager().addError(error);

		//notify all permitted players
		Message.CHAT_ERROROCCURED.broadcast(Permission.NOVAGUILDS_ERROR);
	}
}
