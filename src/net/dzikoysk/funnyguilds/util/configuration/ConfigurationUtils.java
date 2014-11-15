package net.dzikoysk.funnyguilds.util.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationUtils {
	
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
	            sb.append("\n");
	            line = br.readLine();
	        }
	        br.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
		}
	    return sb.toString();
	}

	public static String[] getLines(File file){
		List<String> lines = new ArrayList<>();
		try {
			lines = Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8);
		} catch (Exception e){
			e.printStackTrace();
		}
		return (String[]) lines.toArray();
	}
}
