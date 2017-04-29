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

import co.marcin.novaguilds.enums.Permission;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PermissionInReadmeTest {
	@Test
	public void testPermissionInReadme() throws Exception {
		final List<Permission> fromReadme = new ArrayList<>();

		Document document = Jsoup.parse(new File("./README.md"), "UTF-8");
		Element tableBody = document.getElementById("permissions-table").child(1);

		for(Element tr : tableBody.children()) {
			String node = tr.child(0).text();
			fromReadme.add(Permission.fromPath(node));
		}

		int count = 0;
		for(Permission permission : Permission.values()) {
			if(!fromReadme.contains(permission)) {
				if(count == 0) {
					System.out.println("Found missing permissions:");
				}

				System.out.println(permission.name());
				count++;
			}
		}

		if(count > 0) {
			throw new Exception("There are " + count + " missing permissions in README.md");
		}
		else {
			System.out.println("All permissions are present in README.md");
		}
	}
}
