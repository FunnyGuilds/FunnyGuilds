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

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.enums.Lang;
import co.marcin.novaguilds.enums.Message;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlIncompleteTranslationTest {
	private static final File langDir = new File(YamlParseTest.resourcesDirectory, "/lang");

	@Test
	public void testMessageEnum() throws Exception {
		File motherLangFile = new File(langDir, "en-en.yml");
		YamlConfiguration motherConfiguration = YamlConfiguration.loadConfiguration(motherLangFile);
		int missingCount = 0;

		for(MessageWrapper message : Message.values()) {
			if(!motherConfiguration.contains(message.getPath())) {
				System.out.println(" - " + message.getPath());
				missingCount++;
			}
		}

		if(missingCount == 0) {
			System.out.println("Result: No missing keys");
		}
		else {
			throw new Exception("Found " + missingCount + " missing keys in en-en file that are present in Message class");
		}
	}

	@Test
	public void testTranslations() throws Exception {
		//Mother lang setup
		File motherLangFile = new File(langDir, "en-en.yml");
		YamlConfiguration motherConfiguration = YamlConfiguration.loadConfiguration(motherLangFile);
		final List<String> motherKeys = new ArrayList<>();
		for(String key : motherConfiguration.getKeys(true)) {
			if(!motherConfiguration.isConfigurationSection(key)) {
				motherKeys.add(key);
			}
		}

		//List all languages and configuration sections
		Map<String, YamlConfiguration> configurationMap = new HashMap<>();
		if(langDir.isDirectory()) {
			File[] list = langDir.listFiles();

			if(list != null) {
				for(File langFile : list) {
					if(!langFile.getName().equals("en-en.yml")) {
						configurationMap.put(StringUtils.replace(langFile.getName(), ".yml", ""), Lang.loadConfiguration(langFile));
					}
				}
			}
		}

		//Get keys from all langs
		System.out.println("Testing lang files for missing keys...");
		int globalMissingCount = 0;
		for(Map.Entry<String, YamlConfiguration> entry : configurationMap.entrySet()) {
			int missingCount = 0;
			String name = entry.getKey();
			YamlConfiguration configuration = entry.getValue();

			System.out.println("---");
			System.out.println();
			System.out.println("Testing lang: " + name);

			for(String mKey : motherKeys) {
				if(!configuration.contains(mKey)) {
					if(missingCount == 0) {
						System.out.println("Missing keys:");
					}

					System.out.println(" - " + mKey);
					missingCount++;
				}
			}

			globalMissingCount += missingCount;
		}

		if(globalMissingCount == 0) {
			System.out.println("Result: No missing keys");
		}
		else {
			throw new Exception("Found " + globalMissingCount + " missing keys in lang files");
		}
	}
}
