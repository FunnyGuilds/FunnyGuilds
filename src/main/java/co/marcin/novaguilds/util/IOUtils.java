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

import com.google.common.io.CharStreams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public final class IOUtils {
	private IOUtils() {

	}

	/**
	 * Converts input stream to a string
	 *
	 * @param inputStream input stream
	 * @return the string
	 * @throws IOException when something goes wrong
	 */
	public static String inputStreamToString(InputStream inputStream) throws IOException {
		return CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
	}

	/**
	 * Saves input stream to a file
	 *
	 * @param inputStream input stream
	 * @param file        target file
	 * @throws IOException when something goes wrong
	 */
	public static void saveInputStreamToFile(InputStream inputStream, File file) throws IOException {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);

			int read;
			byte[] bytes = new byte[1024];

			while((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}
		finally {
			if(inputStream != null) {
				inputStream.close();
			}

			if(outputStream != null) {
				outputStream.close();
			}
		}
	}

	/**
	 * Gets a list of names of files
	 * excluding the extension
	 *
	 * @param directory target directory
	 * @return list of files
	 */
	public static List<String> getFilesWithoutExtension(File directory) {
		final List<String> list = new ArrayList<>();
		File[] filesList = directory.listFiles();

		if(filesList != null) {
			for(File file : filesList) {
				if(file.isFile()) {
					String name = file.getName();
					if(org.apache.commons.lang.StringUtils.contains(name, '.')) {
						name = org.apache.commons.lang.StringUtils.split(name, '.')[0];
						list.add(name);
					}
				}
			}
		}

		return list;
	}

	/**
	 * Converts an input stream to a string with an encoding
	 *
	 * @param in       input stream
	 * @param encoding encoding
	 * @return the string
	 * @throws IOException when something goes wrong
	 */
	public static String toString(InputStream in, String encoding) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len;

		while((len = in.read(buf)) != -1) {
			byteArrayOutputStream.write(buf, 0, len);
		}

		return new String(byteArrayOutputStream.toByteArray(), encoding);
	}

	/**
	 * Reads a file to a string
	 *
	 * @param file file
	 * @return string
	 * @throws IOException when something goes wrong
	 */
	public static String read(File file) throws IOException {
		return inputStreamToString(new FileInputStream(file));
	}

	/**
	 * Writes string to a file
	 *
	 * @param file   target file
	 * @param string the string
	 */
	public static void write(File file, String string) {
		try(PrintWriter out = new PrintWriter(file)) {
			out.println(string);
		}
		catch(FileNotFoundException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Lists files inside the root recursively
	 *
	 * @param root root directory
	 * @return list of files
	 */
	public static List<File> listFilesRecursively(File root) {
		File[] list = root.listFiles();
		final List<File> fileList = new ArrayList<>();

		if(list == null) {
			return fileList;
		}

		for(File f : list) {
			if(f.isDirectory()) {
				fileList.addAll(listFilesRecursively(f));
			}
			else {
				fileList.add(f);
			}
		}

		return fileList;
	}

	/**
	 * Gets content of a website
	 *
	 * @param urlString url
	 * @return page contents
	 * @throws IOException when connection fails
	 */
	public static String getContent(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		return IOUtils.toString(in, encoding);
	}
}
