package net.dzikoysk.funnyguilds.data.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class DatabaseGuild {
	
	private final Guild guild;
	
	public DatabaseGuild(Guild guild){
		this.guild = guild;
	}
	
	public void save(Database db) {
		String update = getInsert();
		if(update != null) db.executeUpdate(update);
	}
	
	public void delete() {
		if(guild == null) return;
		if(guild.getUUID() != null){
			Database db = Database.getInstance();
			db.openConnection();
			StringBuilder update = new StringBuilder();
			update.append("DELETE FROM guilds WHERE uuid='");
			update.append(guild.getUUID().toString());
			update.append("';");
			db.executeUpdate(update.toString());
			db.closeConnection();
		}else if(guild.getName() != null){
			Database db = Database.getInstance();
			db.openConnection();
			StringBuilder update = new StringBuilder();
			update.append("DELETE FROM guilds WHERE name='");
			update.append(guild.getName());
			update.append("';");
			db.executeUpdate(update.toString());
			db.closeConnection();
		}
		guild.delete();
	}
	
	public void updatePoints(){
		Database db = Database.getInstance();
		db.openConnection();
		StringBuilder update = new StringBuilder();
		update.append("UPDATE guilds SET points=");
		update.append(guild.getRank().getPoints());
		update.append(" WHERE uuid='");
		update.append(guild.getUUID().toString());
		update.append("';");
		db.executeUpdate(update.toString());
		db.closeConnection();
	}

	public String getInsert(){
		StringBuilder sb = new StringBuilder();
		String members = StringUtils.toString(UserUtils.getNames(guild.getMembers()), false);
		String regions = StringUtils.toString(guild.getRegions(), false);
		String allies = StringUtils.toString(GuildUtils.getNames(guild.getAllies()), false);
		String enemies = StringUtils.toString(GuildUtils.getNames(guild.getEnemies()), false);
		sb.append("INSERT INTO guilds (");
		sb.append("uuid, name, tag, owner, home, region, members, regions, allies, enemies, points, ");
		sb.append("born, validity, attacked, ban, lives");
		sb.append(") VALUES (");
		sb.append("'" + guild.getUUID().toString() + "',");
		sb.append("'" + guild.getName() + "',");
		sb.append("'" + guild.getTag() + "',");
		sb.append("'" + guild.getOwner().getName() + "',");
		sb.append("'" + Parser.toString(guild.getHome()) + "',");
		sb.append("'" + guild.getRegion() + "',");
		sb.append("'" + members + "',");
		sb.append("'" + regions + "',");
		sb.append("'" + allies + "',");
		sb.append("'" + enemies + "',");
		sb.append("" + guild.getRank().getPoints() + ",");
		sb.append("" + guild.getBorn() + ",");
		sb.append("" + guild.getValidity() + ",");
		sb.append("" + guild.getAttacked() + ",");
		sb.append("" + guild.getBan() + ",");
		sb.append("" + guild.getLives() + "");
		sb.append(") ON DUPLICATE KEY UPDATE ");
		sb.append("name='" + guild.getName() + "',");
		sb.append("tag='" + guild.getTag() + "',");
		sb.append("owner='" + guild.getOwner().getName() + "',");
		sb.append("home='" + Parser.toString(guild.getHome()) + "',");
		sb.append("region='" + guild.getRegion() + "',");
		sb.append("members='" + members + "',");
		sb.append("regions='" + regions + "',");
		sb.append("allies='" + allies + "',");
		sb.append("enemies='" + enemies + "',");
		sb.append("points=" + guild.getRank().getPoints() + ",");
		sb.append("born=" + guild.getBorn() + ",");
		sb.append("validity=" + guild.getValidity() + ",");
		sb.append("attacked=" + guild.getAttacked() + ",");
		sb.append("ban=" + guild.getBan() + ",");
		sb.append("lives=" + guild.getLives() + ";");
		return sb.toString();
	}
	
	public static Guild deserialize(ResultSet rs) throws SQLException{
		if(rs == null) return null;
		Object[] values = new Object[15];
		
		String id = rs.getString("uuid");
		String name = rs.getString("name");
		String tag = rs.getString("tag");
		String os = rs.getString("owner");
		String home = rs.getString("home");
		String region = rs.getString("region");
		String m = rs.getString("members");
		String rgs = rs.getString("regions");
		String als = rs.getString("allies");
		String ens = rs.getString("enemies");
		long born = rs.getLong("born");
		long validity = rs.getLong("validity");
		long attacked = rs.getLong("attacked");
		long ban = rs.getLong("ban");
		int lives = rs.getInt("lives");
		
		if(name == null || tag == null || os == null){
			FunnyGuilds.error("Cannot deserialize guild! Caused by: uuid/name/tag/owner is null");
			return null;
		}
		
		UUID uuid = UUID.randomUUID();
		if(id != null) uuid = UUID.fromString(id);
		
		User owner = User.get(os);
		List<User> members = new ArrayList<>();
		if(m != null && !m.equals("")) members = UserUtils.getUsers(StringUtils.fromString(m));
		List<String> regions = StringUtils.fromString(rgs);
		List<Guild> allies = new ArrayList<>();
		if(als != null && !als.equals("")) allies = GuildUtils.getGuilds(StringUtils.fromString(als));
		List<Guild> enemies = new ArrayList<>();
		if(ens != null && !ens.equals("")) enemies = GuildUtils.getGuilds(StringUtils.fromString(ens));

		if(born == 0) born = System.currentTimeMillis(); 
		if(validity == 0) validity = System.currentTimeMillis() + Config.getInstance().validityStart; 
		if(lives == 0) lives = Config.getInstance().warLives;
		
		values[0] = uuid;
		values[1] = name;
		values[2] = tag;
		values[3] = owner;
		values[4] = Parser.parseLocation(home);
		values[5] = region;
		values[6] = members;
		values[7] = regions;
		values[8] = allies;
		values[9] = enemies;
		values[10] = born;
		values[11] = validity;
		values[12] = attacked;
		values[13] = lives;
		values[14] = ban;
		return DeserializationUtils.deserializeGuild(values);
	}

}
