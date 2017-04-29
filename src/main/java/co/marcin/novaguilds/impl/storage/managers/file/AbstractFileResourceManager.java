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

package co.marcin.novaguilds.impl.storage.managers.file;

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.impl.storage.AbstractFileStorage;
import co.marcin.novaguilds.impl.storage.managers.AbstractResourceManager;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileResourceManager<T extends Resource> extends AbstractResourceManager<T> {
	private final File directory;

	/**
	 * The constructor
	 *
	 * @param storage       the storage
	 * @param clazz         manager type class
	 * @param directoryPath resource directory path
	 */
	protected AbstractFileResourceManager(Storage storage, Class<T> clazz, String directoryPath) {
		super(storage, clazz);
		directory = new File(getStorage().getDirectory(), directoryPath);
	}

	@Override
	protected AbstractFileStorage getStorage() {
		if(!(super.getStorage() instanceof AbstractFileStorage)) {
			throw new IllegalArgumentException("Invalid storage type");
		}

		return (AbstractFileStorage) super.getStorage();
	}

	@Override
	public void add(T t) {
		try {
			createFileIfNotExists(getFile(t));
			t.setAdded();
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Gets resource directory
	 *
	 * @return the directory
	 */
	protected File getDirectory() {
		return directory;
	}

	/**
	 * Gets resource's file
	 *
	 * @param t instance
	 * @return the file
	 */
	public abstract File getFile(T t);

	/**
	 * Gets all stored files
	 *
	 * @return list of files
	 */
	protected List<File> getFiles() {
		File[] files = getDirectory().listFiles();
		final List<File> list = new ArrayList<>();

		if(files != null) {
			list.addAll(Arrays.asList(files));
		}

		return list;
	}

	/**
	 * Creates file if doesn't exist
	 *
	 * @param file the file
	 * @throws IOException if failed
	 */
	private void createFileIfNotExists(File file) throws IOException {
		if(!file.exists()) {
			if(!file.createNewFile()) {
				throw new IOException("File creating failed (" + file.getPath() + ")");
			}
		}
	}

	/**
	 * Trims extension from file's name
	 *
	 * @param file the file
	 * @return trimmed name
	 */
	protected final String trimExtension(File file) {
		return StringUtils.substring(file.getName(), 0, StringUtils.lastIndexOf(file.getName(), '.'));
	}
}
