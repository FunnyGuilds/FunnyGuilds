package net.dzikoysk.panda.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
	
	public static String getContent(File file){
		StringBuilder sb = new StringBuilder();
		try{
			if(!file.exists()){
				file.getParentFile().mkdirs();
				file.createNewFile();
			}	
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String[] getLines(File file){
		try{
			if(!file.exists()){
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			String[] ss = new String[countLines(file.getPath()) + 1];
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			int i = 0;
			while (line != null) {
				ss[i] = line;
				line = br.readLine();
				i++;
			}
			br.close();
			return ss;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

}
