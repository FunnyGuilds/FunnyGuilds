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

import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import org.junit.Test;

public class GuildPermissionEnumTest {
	@Test
	public void testMessageEnum() throws Exception {
		boolean passed = true;
		System.out.println("Looking for GuildPermission values not present in Message class");

		for(GuildPermission perm : GuildPermission.values()) {
			try {
				Message.valueOf("INVENTORY_GUI_PERMISSIONS_NAMES_" + perm.name());
			}
			catch(IllegalArgumentException e) {
				if(passed) {
					System.out.println("Missing enums:");
				}

				System.out.println("INVENTORY_GUI_PERMISSIONS_NAMES_" + perm.name() + ",");
				passed = false;
			}
		}

		if(!passed) {
			throw new Exception("Found missing Message wrappers!");
		}
		else {
			System.out.println("Nothing is missing here.");
		}
	}
}
