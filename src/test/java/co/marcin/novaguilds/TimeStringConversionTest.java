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

package co.marcin.novaguilds;

import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.yaml.YamlParseTest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;

public class TimeStringConversionTest {
	private final String[] strings = {
			"",
			"0s",
			"1s",
			"1m",
			"1h",
			"1d",
			"1w",
			"1mo",
			"1y",
			"1y 1mo 1w 1d 1h 1m 1s"
	};

	private final Long[] longs = {
			0L,
			0L,
			1L,
			60L,
			3600L,
			86400L,
			604800L,
			2678400L,
			31536000L,
			34909261L,
	};

	private final String[] strings2 = {
			"0 seconds",
			"0 seconds",
			"1 second",
			"1 minute",
			"1 hour",
			"1 day",
			"1 week",
			"1 month",
			"1 year",
			"1 year 1 month 1 week 1 day 1 hour 1 minute 1 second",
	};

	static {
		MessageManager messageManager = new MessageManager();
		File langDir = new File(YamlParseTest.resourcesDirectory, "/lang");
		File motherLangFile = new File(langDir, "en-en.yml");
		YamlConfiguration motherConfiguration = YamlConfiguration.loadConfiguration(motherLangFile);
		messageManager.setMessages(motherConfiguration);
	}

	@Test
	public void testStringToSeconds() throws Exception {
		System.out.println("Testing stringToSeconds()");
		for(int i = 0; i < strings.length; i++) {
			System.out.println(strings[i] + "->" + StringUtils.stringToSeconds(strings[i]) + " = " + longs[i]);
			if(StringUtils.stringToSeconds(strings[i]) != longs[i]) {
				throw new Exception("Values not equal!");
			}
		}

		System.out.println();
	}

	@Test
	public void testSecondsToString() throws Exception {
		System.out.println("Testing secondsToString()");

		for(int i = 0; i < strings2.length; i++) {
			System.out.println(longs[i] + "->" + StringUtils.secondsToString(longs[i]) + " = " + strings2[i]);
			if(!StringUtils.secondsToString(longs[i]).equals(strings2[i])) {
				throw new Exception("Values not equal!");
			}
		}

		System.out.println();
	}

	@Test
	public void testRandom() throws Exception {
		System.out.println("Testing random values");

		for(int i = 1; i < 100; i++) {
			int seconds = NumberUtils.randInt(0, 100000000);

			String str = StringUtils.secondsToString(seconds);

			String str2;
			str2 = org.apache.commons.lang.StringUtils.replace(str, " seconds", "s");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " minutes", "m");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " hours", "h");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " days", "d");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " weeks", "w");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " months", "mo");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " years", "y");

			str2 = org.apache.commons.lang.StringUtils.replace(str2, " second", "s");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " minute", "m");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " hour", "h");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " day", "d");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " week", "w");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " month", "mo");
			str2 = org.apache.commons.lang.StringUtils.replace(str2, " year", "y");

			if(StringUtils.stringToSeconds(str2) != seconds) {
				System.out.println("Failed case:");
				System.out.println(" Seconds: " + seconds);
				System.out.println(" String #1: " + str);
				System.out.println(" String #2: " + str2);
				throw new Exception("Failed Random!");
			}

			System.out.println(seconds + " " + str);
		}

		System.out.println();
	}
}
