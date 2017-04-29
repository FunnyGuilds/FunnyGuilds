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

import co.marcin.novaguilds.util.IOUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommitInfoTest {
	@Test
	public void addCommitInfo() throws IOException {
		File commitInfoFile = new File("./target/classes/commit.yml");

		if(commitInfoFile.createNewFile()) {
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec("git rev-parse HEAD");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			System.out.println("Latest commit:");
			String commit = stdInput.readLine();
			System.out.println(commit);

			IOUtils.write(commitInfoFile, commit);
		}
	}
}
