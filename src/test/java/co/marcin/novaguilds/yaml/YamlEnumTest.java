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

package co.marcin.novaguilds.yaml;

import co.marcin.novaguilds.api.basic.ConfigWrapper;
import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Lang;
import co.marcin.novaguilds.enums.Message;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YamlEnumTest {
	private String[] ignoreConfig;

	/**
	 * The constructor
	 * Defines ignored sections
	 */
	public YamlEnumTest() {
		ignoreConfig = new String[]{
				"aliases.",
				"gguicmd",
				"groups",
				"rank.defaultranks",
		};
	}

	@Test
	public void testConfig() throws Exception {
		System.out.println();
		System.out.println("Testing config enums...");
		YamlConfiguration config = getConfig();
		final List<String> configEnumNames = new ArrayList<>();
		for(ConfigWrapper v : Config.values()) {
			configEnumNames.add(v.getName());
		}

		int missingCount = 0;
		for(String key : config.getKeys(true)) {
			boolean ig = config.isConfigurationSection(key);
			for(String ignore : ignoreConfig) {
				if(key.startsWith(ignore)) {
					ig = true;
					break;
				}
			}

			if(!ig) {
				String name = StringUtils.replace(key, ".", "_").toUpperCase();
				if(!configEnumNames.contains(name)) {
					if(missingCount == 0) {
						System.out.println("Missing keys:");
					}

					System.out.println(name + ",");
					missingCount++;
				}
			}
		}

		if(missingCount == 0) {
			System.out.println("All values are present in Config enum");
		}
		else {
			throw new Exception("Found " + missingCount + " missing Config enums");
		}
	}

	@Test
	public void testEmptyEnums() throws Exception {
		System.out.println("Testing empty Config enums");

		YamlConfiguration config = getConfig();
		int missingCount = 0;
		for(ConfigWrapper c : Config.values()) {
			if(!config.contains(c.getPath())) {
				System.out.println("Empty enum: " + c.getName());
				missingCount++;
			}
		}

		if(missingCount == 0) {
			System.out.println("All values are fine in the Config enum");
		}
		else {
			throw new Exception("Found " + missingCount + " empty Config enums");
		}

		System.out.println();
	}

	@Test
	public void testMessages() throws Exception {
		System.out.println();
		System.out.println("Testing message wrappers...");
		File motherFile = new File(YamlParseTest.resourcesDirectory, "lang/en-en.yml");
		YamlConfiguration motherConfiguration = Lang.loadConfiguration(motherFile);
		final List<String> messageEnumNames = new ArrayList<>();

		for(MessageWrapper v : Message.values()) {
			messageEnumNames.add(v.getName());
		}

		int missingCount = 0;
		for(String key : motherConfiguration.getKeys(true)) {
			if(!motherConfiguration.isConfigurationSection(key)) {
				String name = StringUtils.replace(key, ".", "_").toUpperCase();
				if(!messageEnumNames.contains(name)) {
					if(missingCount == 0) {
						System.out.println("Missing keys:");
					}

					System.out.println(name + ",");
					missingCount++;
				}
			}
		}

		if(missingCount == 0) {
			System.out.println("All values are present in Message class");
		}
		else {
			throw new Exception("Found " + missingCount + " missing Message wrappers");
		}
	}

	/**
	 * Gets the config
	 *
	 * @return config.yml YamlConfiguration
	 */
	private YamlConfiguration getConfig() {
		File configFile = new File(YamlParseTest.resourcesDirectory, "config.yml");
		return YamlConfiguration.loadConfiguration(configFile);
	}
}
