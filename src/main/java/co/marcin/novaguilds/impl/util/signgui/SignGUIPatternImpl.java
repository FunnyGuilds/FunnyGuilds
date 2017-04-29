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

package co.marcin.novaguilds.impl.util.signgui;

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.util.SignGUI;
import co.marcin.novaguilds.manager.MessageManager;

import java.util.List;

public class SignGUIPatternImpl implements SignGUI.SignGUIPattern {
	private final MessageWrapper message;
	private int inputLine;

	/**
	 * The constructor
	 *
	 * @param message message wrapper of the pattern
	 */
	public SignGUIPatternImpl(MessageWrapper message) {
		this.message = message;
	}

	@Override
	public String[] get() {
		return fromList(message.getList());
	}

	@Override
	public int getInputLine() {
		return inputLine;
	}

	/**
	 * Creates the pattern from a list
	 *
	 * @param list list of strings
	 * @return array of strings
	 */
	protected String[] fromList(List<String> list) {
		String[] lines = new String[4];

		List<String> rawList = MessageManager.getMessages().getStringList(message.getPath());
		for(int index = 0; index < rawList.size(); index++) {
			if(rawList.get(index).contains("{INPUT}")) {
				inputLine = index;
				break;
			}
		}

		for(int index = 0; index < list.size(); index++) {
			lines[index] = list.get(index);
		}

		return lines;
	}
}
