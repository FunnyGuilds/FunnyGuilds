package net.dzikoysk.funnyguilds.util.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

}
