package net.dzikoysk.funnyguilds.basic.util;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.funnyguilds.basic.Rank;

public class RankUtils {
	
	private static List<Rank> list = new ArrayList<>();
	
	public static void add(Rank rank){
		list.add(rank);
	}
	
	public static void remove(Rank rank){
		list.remove(rank);
	}
	
	public static List<Rank> getAll(){
		return new ArrayList<Rank>(list);
	}

}
