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
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class VersionUtils {
	private static int buildCurrent;
	private static int buildLatest;
	private static int buildDev;
	private static String commit = "";
	private static boolean updateAvailable;
	private static boolean init;

	static {
		if(!init) {
			try {
				new VersionUtils();
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
	}

	/**
	 * Gets initialized statically
	 * Checks plugin versions
	 *
	 * @throws IOException when failed to fetch versions
	 */
	private VersionUtils() throws IOException {
		init = true;
		NovaGuilds ng = NovaGuilds.getInstance();
		String currentString;

		if(ng == null) {
			currentString = YamlConfiguration.loadConfiguration(new File("./src/main/resources/plugin.yml")).getString("version");
		}
		else {
			currentString = ng.getDescription().getVersion();
			commit = ng.getResource("commit.yml") == null ? "invalid" : IOUtils.inputStreamToString(ng.getResource("commit.yml"));
		}

		currentString = org.apache.commons.lang.StringUtils.replace(currentString, "-SNAPSHOT", "");
		buildCurrent = Integer.parseInt(currentString);

		String latestString = IOUtils.getContent("http://novaguilds.pl/latest.info");
		String devString = IOUtils.getContent("http://novaguilds.pl/dev.info");

		buildLatest = Integer.parseInt(org.apache.commons.lang.StringUtils.replace(latestString, "-SNAPSHOT", ""));
		buildDev = Integer.parseInt(org.apache.commons.lang.StringUtils.replace(devString, "-SNAPSHOT", ""));
	}

	/**
	 * Prints info about versions to the console
	 */
	public static void checkVersion() {
		LoggerUtils.info("You're using build: #" + buildCurrent);
		LoggerUtils.info("Latest stable build of the plugin is: #" + buildLatest);

		if(buildCurrent == buildLatest) {
			LoggerUtils.info("Your plugin build is the latest stable one");
		}
		else if(buildCurrent > buildLatest) {
			if(buildCurrent > buildDev) {
				LoggerUtils.info("You are using unreleased build #" + buildCurrent);
			}
			else if(buildCurrent == buildDev) {
				LoggerUtils.info("You're using latest development build");
			}
			else {
				LoggerUtils.info("Why the hell are you using outdated dev build?");
				updateAvailable = true;
			}
		}
		else {
			LoggerUtils.info("You should update your plugin to #" + buildLatest + "!");
			updateAvailable = true;
		}

		//Notify admins if there's an update (only for reload)
		if(updateAvailable) {
			Message.CHAT_UPDATE.broadcast(Permission.NOVAGUILDS_ADMIN_UPDATEAVAILABLE);
		}
	}

	/**
	 * Gets current plugin build
	 *
	 * @return build number
	 */
	public static int getBuildCurrent() {
		return buildCurrent;
	}

	/**
	 * Gets latest stable build
	 *
	 * @return build number
	 */
	public static int getBuildLatest() {
		return buildLatest;
	}

	/**
	 * Gets latest development build
	 *
	 * @return build number
	 */
	public static int getBuildDev() {
		return buildDev;
	}

	/**
	 * Gets commit sha of the plugin
	 *
	 * @return commit SHA (first 7 characters)
	 */
	public static String getCommit() {
		return org.apache.commons.lang.StringUtils.substring(commit, 0, 7);
	}

	/**
	 * Tells if an update is available
	 *
	 * @return boolean
	 */
	public static boolean isUpdateAvailable() {
		return updateAvailable;
	}
}
