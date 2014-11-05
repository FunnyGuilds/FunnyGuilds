package net.dzikoysk.funnyguilds.util.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.PacketUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerListManager {
	
	private static final String[] colorsCode = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	private static String[] scheme  = new String[60];
	private static boolean enable;
	private static int ping;
	private static boolean patch;

	public static void updatePlayers(){
		for(Player player : Bukkit.getOnlinePlayers()){
			User.get(player).getPlayerList().send();
		}
	}
	
	public static void send(Player player) {
		if(!enable) return;
		User user = User.get(player);
		Scoreboard sb = user.getScoreboard();
		PlayerList pl = user.getPlayerList();
		String[] prefix = pl.getPrefix();
		String[] suffix = pl.getSuffix();
		for(int i = 0; i < 60; i++){
			Team team = sb.getTeam(scheme[i]);
			if(team == null){
				team = sb.registerNewTeam(scheme[i]);
				team.addPlayer(new OfflineUser(scheme[i]));
			}
			if(prefix[i] != null) team.setPrefix(prefix[i]);
			if(suffix[i] != null) team.setSuffix(suffix[i]);
		}
		if(!pl.getInit()){
			Player[] ps = Bukkit.getOnlinePlayers();
			String[] ss = new String[ps.length];
			for(int i = 0; i < ps.length; i++) ss[i] = ps[i].getPlayerListName();
			pl.init(true);
			PacketUtils.sendPacket(player, packets(ss, false));
			PacketUtils.sendPacket(player, packets(scheme, true));
		}
		if(patch){
			Player[] ps = Bukkit.getOnlinePlayers();
			String[] ss = new String[ps.length];
			for(int i = 0; i < ps.length; i++) ss[i] = ps[i].getPlayerListName();
			PacketUtils.sendPacket(player, packets(ss, false));
		}
		player.setScoreboard(sb);
		user.setScoreboard(sb);
	}
	
	private static Object[] packets(String[] ss, boolean b) {
		Object[] packets = new Object[ss.length];
		for(int i = 0; i < ss.length; i++) packets[i] = PacketUtils.getPacket(ss[i], b, ping);
		return packets;
	}
	
	public static void scheme(String[] ss){
		String[] clone = ss.clone();
		for(int i = 0; i < clone.length; i++)
			if(clone[i] != null) clone[i] = ChatColor.translateAlternateColorCodes('.', clone[i]);
		scheme = clone;
	}
	
	public static String[] scheme(){
		return scheme;
	}
	
	public static void enable(boolean e){
		enable = e;
	}
	
	public static void ping(int i){
		ping = i;
	}
	
	public static void patch(boolean p){
		patch = p;
	}
	
	public static String[] uniqueFields(){
		List<String> fields = new ArrayList<>();
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		sb.append(".r");
		for(int i = 0; i < 60; i++){
			while(fields.contains(sb.toString())){
				sb.setLength(0);
				for(int x = 0; x < 3; x++){
					String r = colorsCode[random.nextInt(colorsCode.length)];
					sb.append(".");
					sb.append(r);
				}
			}
			fields.add(sb.toString());
		}
		return fields.toArray(new String[60]);
	}
}
