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

import co.marcin.novaguilds.enums.Lang;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class YamlParseTest {
	public static final File resourcesDirectory = new File("./src/main/resources/");

	@Test
	public void testConfig() throws FileNotFoundException, InvalidConfigurationException {
		File configFile = new File(resourcesDirectory, "config.yml");

		if(!configFile.exists()) {
			throw new FileNotFoundException("Config file does not exist.");
		}

		try {
			YamlConfiguration.loadConfiguration(configFile);
		}
		catch(NullPointerException e) {
			throw new InvalidConfigurationException("Invalid YAML file (" + configFile.getPath() + ")");
		}
	}

	@Test
	public void testTranslations() throws NullPointerException, InvalidConfigurationException, IOException {
		File langDir = new File(resourcesDirectory, "/lang");

		if(langDir.isDirectory()) {
			File[] list = langDir.listFiles();

			if(list != null) {
				for(File lang : list) {
					try {
						Lang.loadConfiguration(lang);
					}
					catch(NullPointerException e) {
						throw new InvalidConfigurationException("Invalid YAML file (" + lang.getPath() + ")");
					}
				}
			}
		}
		else {
			throw new FileNotFoundException("Lang dir does not exist.");
		}
	}
}
