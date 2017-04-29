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
import co.marcin.novaguilds.api.util.exceptionparser.ErrorSignature;
import co.marcin.novaguilds.api.util.exceptionparser.IError;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.util.VersionUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

public class ErrorImpl implements IError {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Collection<String> consoleOutput = new ArrayList<>();
	private final Collection<Throwable> causes = new LinkedHashSet<>();
	private final Throwable exception;
	private ErrorSignature signature;

	/**
	 * The constructor
	 *
	 * @param exception the exception
	 */
	public ErrorImpl(Throwable exception) {
		this.exception = exception;

		Throwable cause = exception.getCause();
		while(cause != null) {
			causes.add(cause);
			cause = cause.getCause();
		}
	}

	/**
	 * Generates Error signature
	 */
	protected void generateSignature() {
		signature = new ErrorSignatureImpl(this);
	}

	@Override
	public ErrorSignature getSignature() {
		if(signature == null) {
			generateSignature();
		}

		return signature;
	}

	@Override
	public Throwable getException() {
		return exception;
	}

	@Override
	public Collection<Throwable> getCauses() {
		return causes;
	}

	@Override
	public Collection<String> getConsoleOutput() {
		if(consoleOutput.isEmpty()) {
			generateConsoleOutput();
		}

		return consoleOutput;
	}

	/**
	 * Generates console output
	 */
	protected void generateConsoleOutput() {
		consoleOutput.add("");
		consoleOutput.add("Severe error: " + exception.getClass().getSimpleName());
		consoleOutput.add(" Please send this whole message to the author (/novaguilds)");
		consoleOutput.add("");
		consoleOutput.add("Server Information:");
		consoleOutput.add(" NovaGuilds: #" + VersionUtils.getBuildCurrent() + " (" + VersionUtils.getCommit() + ")");
		consoleOutput.add(" Storage Type: " + (plugin.getConfigManager() == null || plugin.getConfigManager().getDataStorageType() == null ? "null" : plugin.getConfigManager().getDataStorageType().name()));
		consoleOutput.add(" Bukkit: " + Bukkit.getBukkitVersion());
		consoleOutput.add(" Version Implementation: " + ConfigManager.getServerVersion().name());
		consoleOutput.add(" Java: " + System.getProperty("java.version"));
		consoleOutput.add(" Thread: " + Thread.currentThread());
		consoleOutput.add(" Running CraftBukkit: " + Bukkit.getServer().getClass().getSimpleName().equals("CraftServer"));
		consoleOutput.add("");
		consoleOutput.add("Exception Message: ");
		consoleOutput.add(" " + exception.getMessage());

		for(StackTraceElement stackTraceElement : exception.getStackTrace()) {
			consoleOutput.add("  at " + stackTraceElement.toString());
		}

		consoleOutput.add("");

		for(Throwable cause : causes) {
			consoleOutput.add("Caused by: " + cause.getClass().getName());
			consoleOutput.add(" " + cause.getMessage());

			for(StackTraceElement stackTraceElement : cause.getStackTrace()) {
				consoleOutput.add("  at " + stackTraceElement.toString());
			}

			consoleOutput.add("");
		}

		consoleOutput.add("End of Error.");
		consoleOutput.add("");
	}
}
