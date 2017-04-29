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

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class StringUtils {
	private StringUtils() {

	}

	/**
	 * Fixes color characters in a string
	 *
	 * @param msg message
	 * @return new string
	 */
	public static String fixColors(String msg) {
		if(msg == null) {
			return null;
		}

		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	/**
	 * Fixes color characters for each string in a list
	 *
	 * @param list list
	 * @return new list
	 */
	public static List<String> fixColors(List<String> list) {
		for(int i = 0; i < list.size(); i++) {
			list.set(i, fixColors(list.get(i)));
		}

		return list;
	}

	/**
	 * Reverses color character replacement
	 *
	 * @param msg colored message
	 * @return raw message
	 */
	public static String unTranslateAlternateColorCodes(String msg) {
		char altColorChar = ChatColor.COLOR_CHAR;

		char[] b = msg.toCharArray();
		for(int i = 0; i < b.length - 1; i++) {
			if(b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = '&';
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}

		return new String(b);
	}

	/**
	 * Removes colors from a string
	 *
	 * @param msg the message
	 * @return new message
	 */
	public static String removeColors(String msg) {
		return ChatColor.stripColor(fixColors(msg));
	}

	/**
	 * Converts location to a String (3D)
	 * Contains world name
	 *
	 * @param location the location
	 * @return location string
	 */
	public static String parseDBLocation(Location location) {
		return location == null ? "" : location.getWorld().getUID().toString() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ() + ";" + Math.round(location.getYaw());
	}

	/**
	 * Converts location to a String (2D)
	 *
	 * @param location the location
	 * @return location string
	 */
	public static String parseDBLocationCoordinates2D(Location location) {
		return location.getBlockX() + ";" + location.getBlockZ();
	}

	/**
	 * Cuts first elements of an array
	 *
	 * @param args array
	 * @param cut  amount of elements to cut
	 * @return new array
	 */
	public static String[] parseArgs(String[] args, int cut) {
		if(args.length == 0 || args.length < cut) {
			return args;
		}

		String[] newArgs = new String[args.length - cut];

		int index = 0;
		for(int i = 0; i < args.length; i++) {
			if(i >= cut) {
				newArgs[index] = args[i];
				index++;
			}
		}

		return newArgs;
	}

	/**
	 * Created a list from strings separated by semicolons
	 *
	 * @param str string
	 * @return list
	 */
	public static List<String> semicolonToList(String str) {
		return split(str, ";");
	}

	/**
	 * Created a list from strings separated by a specified string
	 *
	 * @param str       string
	 * @param separator the separator
	 * @return list
	 */
	public static List<String> split(String str, String separator) {
		final List<String> list = new ArrayList<>();

		if(str.contains(separator)) {
			String[] split = str.split(separator);
			Collections.addAll(list, split);
		}
		else if(!str.isEmpty()) {
			list.add(str);
		}

		return list;
	}

	/**
	 * Joins together elements from a list
	 *
	 * @param items     list
	 * @param separator separator
	 * @return string
	 */
	public static String join(Collection items, String separator) {
		return join(items.toArray(), separator);
	}

	/**
	 * Join together elements
	 * divided with a semicolon
	 *
	 * @param list the list
	 * @return string
	 */
	public static String joinSemicolon(Collection list) {
		return join(list, ";");
	}

	/**
	 * Joins together elements from an array
	 *
	 * @param items     array of objects
	 * @param separator separator
	 * @return string
	 */
	public static String join(Object[] items, String separator) {
		String joined = "";

		if(items.length > 0) {
			for(Object row : items) {
				joined = joined + row.toString() + separator;
			}

			joined = joined.substring(0, joined.length() - separator.length());
		}

		return joined;
	}

	/**
	 * Joins messages together
	 *
	 * @param list      list of message wrappers
	 * @param separator separator message wrapper
	 * @return string
	 */
	public static String join(Collection<MessageWrapper> list, MessageWrapper separator) {
		String joined = "";

		if(!list.isEmpty()) {
			Iterator<MessageWrapper> iterator = list.iterator();
			while(iterator.hasNext()) {
				MessageWrapper row = iterator.next();
				joined += row.get();

				if(iterator.hasNext()) {
					joined += separator.get();
				}
			}
		}

		return joined;
	}

	/**
	 * Replaces variables in a string
	 *
	 * @param msg  target string
	 * @param vars variable map (with values)
	 * @return replaced string
	 */
	public static String replaceVarKeyMap(String msg, Map<VarKey, String> vars) {
		if(vars != null) {
			for(Map.Entry<VarKey, String> entry : vars.entrySet()) {
				msg = org.apache.commons.lang.StringUtils.replace(msg, "{" + entry.getKey().name() + "}", entry.getValue());
			}
		}

		return msg;
	}

	/**
	 * Replaces variables in a string
	 *
	 * @param msg  target string
	 * @param vars variable map (with values)
	 * @return replaced string
	 */
	public static String replaceMap(String msg, Map<String, String> vars) {
		if(vars != null) {
			for(Map.Entry<String, String> entry : vars.entrySet()) {
				msg = org.apache.commons.lang.StringUtils.replace(msg, "{" + entry.getKey() + "}", entry.getValue());
			}
		}

		return msg;
	}

	/**
	 * Converts seconds to a string
	 * Time unit is seconds
	 *
	 * @param seconds amount of seconds
	 * @return time string
	 */
	public static String secondsToString(long seconds) {
		return secondsToString(seconds, TimeUnit.SECONDS);
	}

	/**
	 * Converts seconds to a string
	 * Time unit is seconds
	 *
	 * @param seconds amount of seconds
	 * @param unit    time unit
	 * @return time string
	 */
	public static String secondsToString(long seconds, TimeUnit unit) {
		if(seconds <= 0) {
			seconds = 0;
		}

		int minute = 60;
		int hour = 60 * minute;
		int day = hour * 24;
		int week = day * 7;
		int month = day * 31;
		int year = 31536000;

		long years = seconds / year;
		seconds = seconds % year;

		long months = seconds / month;
		seconds = seconds % month;

		long weeks = seconds / week;
		seconds = seconds % week;

		long days = seconds / day;
		seconds = seconds % day;

		long hours = seconds / hour;
		seconds = seconds % hour;

		long minutes = seconds / minute;
		seconds = seconds % minute;

		String stringYears = "", stringMonths = "", stringWeeks = "", stringDays = "", stringHours = "", stringSeconds = "", stringMinutes = "";

		if(years > 0) {
			MessageWrapper form = years > 1 ? Message.TIMEUNIT_YEAR_PLURAL : Message.TIMEUNIT_YEAR_SINGULAR;
			stringYears = years + " " + form.get() + " ";
		}

		if(months > 0) {
			MessageWrapper form = months > 1 ? Message.TIMEUNIT_MONTH_PLURAL : Message.TIMEUNIT_MONTH_SINGULAR;
			stringMonths = months + " " + form.get() + " ";
		}

		if(weeks > 0) {
			MessageWrapper form = weeks > 1 ? Message.TIMEUNIT_WEEK_PLURAL : Message.TIMEUNIT_WEEK_SINGULAR;
			stringWeeks = weeks + " " + form.get() + " ";
		}

		if(days > 0) {
			MessageWrapper form = days > 1 ? Message.TIMEUNIT_DAY_PLURAL : Message.TIMEUNIT_DAY_SINGULAR;
			stringDays = days + " " + form.get() + " ";
		}

		if(hours > 0) {
			MessageWrapper form = hours > 1 ? Message.TIMEUNIT_HOUR_PLURAL : Message.TIMEUNIT_HOUR_SINGULAR;
			stringHours = hours + " " + form.get() + " ";
		}

		if(minutes > 0) {
			MessageWrapper form = minutes > 1 ? Message.TIMEUNIT_MINUTE_PLURAL : Message.TIMEUNIT_MINUTE_SINGULAR;
			stringMinutes = minutes + " " + form.get() + " ";
		}

		if(seconds > 0 || (seconds == 0 && minutes == 0 && hours == 0 && days == 0 && weeks == 0 && months == 0 && years == 0)) {
			MessageWrapper form = seconds == 1 ? Message.TIMEUNIT_SECOND_SINGULAR : Message.TIMEUNIT_SECOND_PLURAL;
			stringSeconds = seconds + " " + form.get() + " ";
		}

		if(unit == TimeUnit.DAYS && days > 0) {
			stringHours = "";
			stringMinutes = "";
			stringSeconds = "";
		}
		else if(unit == TimeUnit.HOURS && hours > 0) {
			stringMinutes = "";
			stringSeconds = "";
		}
		else if(unit == TimeUnit.MINUTES && minutes > 0) {
			stringSeconds = "";
		}

		String r = stringYears + stringMonths + stringWeeks + stringDays + stringHours + stringMinutes + stringSeconds;
		r = r.substring(0, r.length() - 1);
		return r;
	}

	/**
	 * Converts a string to seconds
	 *
	 * @param str string
	 * @return amount of seconds
	 */
	public static int stringToSeconds(String str) {
		String[] spaceSplit = str.split(" ");
		int seconds = 0;

		for(String word : spaceSplit) {
			if(word.endsWith("s")) {
				word = word.substring(0, word.length() - 1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word);
				}
			}

			if(word.endsWith("m")) {
				word = word.substring(0, word.length() - 1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60;
				}
			}

			if(word.endsWith("h")) {
				word = word.substring(0, word.length() - 1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60 * 60;
				}
			}

			if(word.endsWith("d")) {
				word = word.substring(0, word.length() - 1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60 * 60 * 24;
				}
			}

			if(word.endsWith("w")) {
				word = word.substring(0, word.length() - 1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60 * 60 * 24 * 7;
				}
			}

			if(word.endsWith("mo")) {
				word = word.substring(0, word.length() - 2);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60 * 60 * 24 * 31;
				}
			}

			if(word.endsWith("y")) {
				word = word.substring(0, word.length() - 1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60 * 60 * 24 * 365;
				}
			}
		}

		return seconds;
	}

	/**
	 * Checks if a string is allowed due to rules specified in config
	 *
	 * @param string string
	 * @return boolean
	 */
	public static boolean isStringAllowed(String string) {
		if(Config.GUILD_STRINGCHECK_ENABLED.getBoolean()) {
			if(Config.GUILD_STRINGCHECK_REGEX.getBoolean()) {
				Pattern pattern = Config.GUILD_STRINGCHECK_REGEXPATTERN.get();
				return pattern.matcher(string).matches();
			}
			else {
				String allowed = Config.GUILD_STRINGCHECK_PATTERN.getString();
				for(int i = 0; i < string.length(); i++) {
					if(allowed.indexOf(string.charAt(i)) == -1) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Creates a message with a list of items
	 *
	 * @param items item list
	 * @return message string
	 */
	public static String getItemList(List<ItemStack> items) {
		String itemListString = "";
		int i = 0;
		for(ItemStack missingItemStack : items) {
			MessageWrapper rowMessage = Message.CHAT_CREATEGUILD_ITEMLIST.clone();
			rowMessage.setVar(VarKey.ITEMNAME, missingItemStack.getType().name());
			rowMessage.setVar(VarKey.AMOUNT, missingItemStack.getAmount());

			itemListString += rowMessage.get();

			if(i < items.size() - 1) {
				itemListString += Message.CHAT_CREATEGUILD_ITEMLISTSEP.get();
			}
			i++;
		}

		return fixColors(itemListString);
	}

	/**
	 * Converts JSON to a list of strings
	 *
	 * @param json json string
	 * @return list of strings
	 */
	public static List<String> jsonToList(String json) {
		json = "{array:" + json + "}";
		JSONObject obj = new JSONObject(json);
		JSONArray arr = obj.optJSONArray("array");

		final List<String> list = new ArrayList<>();

		for(int i = 0; i < arr.length(); i++) {
			list.add(arr.getString(i));
		}

		return list;
	}

	/**
	 * Checks if a string is an UUID
	 *
	 * @param string string
	 * @return true if UUID
	 */
	public static boolean isUUID(String string) {
		return string.contains("-") && string.split("-").length == 5;
	}

	/**
	 * Treats quoted text as one argument
	 *
	 * @param args input argument array
	 * @return parsed argument array
	 */
	public static String[] parseQuotedArguments(String[] args) {
		try {
			final List<String> newArgs = new ArrayList<>();
			String argCache = "";

			for(String a : args) {
				if((a.startsWith("\"") || a.startsWith("'") || !argCache.isEmpty()) && !a.startsWith("\\\"") && !a.startsWith("\\'")) {
					argCache += a + " ";
				}
				else {
					newArgs.add(a);
					continue;
				}

				if(((argCache.startsWith("\"") && a.endsWith("\"")) || (argCache.startsWith("'") && a.endsWith("'"))) && !argCache
						.isEmpty() && !a.endsWith("\\\"") && !a.endsWith("\\'")) {
					newArgs.add(argCache.length() > 2 ? argCache.substring(1, argCache.length() - 2) : argCache);
					argCache = "";
				}
			}

			if(!argCache.isEmpty()) {
				newArgs.add(argCache.length() > 2 ? argCache.substring(1, argCache.length() - 1) : argCache);
			}

			for(int i = 0; i < newArgs.size(); i++) {
				String newString = org.apache.commons.lang.StringUtils.replace(newArgs.get(i), "\\'", "'");
				newString = org.apache.commons.lang.StringUtils.replace(newString, "\\\"", "\"");
				newArgs.set(i, newString);
			}

			return newArgs.toArray(new String[newArgs.size()]);
		}
		catch(Exception e) { //Returns original arguments in case of an exception
			LoggerUtils.exception(e);
			return args;
		}
	}
}
