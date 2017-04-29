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

package co.marcin.novaguilds.impl.util.logging;

import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Level;

public class WrappedLogger extends PluginLogger {
	/**
	 * The constructor
	 *
	 * @param context plugin instance
	 */
	public WrappedLogger(Plugin context) {
		super(context);
	}

	@Override
	public void log(Level level, String msg, Throwable thrown) {
		if(level == Level.WARNING) {
			StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			if(ste[2].getClassName().endsWith("CraftScheduler")) {
				LoggerUtils.exception(thrown);
			}
		}
		else {
			super.log(level, msg, thrown);
		}
	}
}
