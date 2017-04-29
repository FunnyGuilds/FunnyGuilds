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

package co.marcin.novaguilds.impl.util.exceptionparser;

import co.marcin.novaguilds.api.util.exceptionparser.Block;
import co.marcin.novaguilds.api.util.exceptionparser.ErrorSignature;
import co.marcin.novaguilds.api.util.exceptionparser.IError;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class ErrorSignatureImpl implements ErrorSignature {
	private final Collection<Block> blocks = new LinkedHashSet<>();

	/**
	 * The constructor
	 *
	 * @param error error instance
	 */
	public ErrorSignatureImpl(IError error) {
		addBlock(new BlockImpl(error.getException()));

		for(Throwable cause : error.getCauses()) {
			addBlock(new BlockImpl(cause));
		}
	}

	@Override
	public Collection<Block> getBlocks() {
		return blocks;
	}

	@Override
	public void addBlock(Block... block) {
		Collections.addAll(blocks, block);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		Iterator<Block> iterator = getBlocks().iterator();
		while(iterator.hasNext()) {
			Block block = iterator.next();
			builder.append(block.getName())
			       .append("|")
			       .append(block.getMessage())
			       .append("|")
			       .append(block.getStackTraceElement());

			if(iterator.hasNext()) {
				builder.append("|");
			}
		}

		return builder.toString();
	}
}
