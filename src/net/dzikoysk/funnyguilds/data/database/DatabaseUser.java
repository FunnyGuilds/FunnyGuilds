package net.dzikoysk.funnyguilds.data.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.dzikoysk.funnyguilds.basic.User;

public class DatabaseUser {

	private User user;
	
	public DatabaseUser(User user){
		this.user = user;
	}
	
	public void save(Database db){
		String update = getInsert();
		if(update != null) db.executeUpdate(update);
	}
	
	public void updatePoints(){
		Database db = Database.getInstance();
		db.openConnection();
		StringBuilder update = new StringBuilder();
		update.append("UPDATE users SET points=");
		update.append(user.getRank().getPoints());
		update.append(" WHERE uuid='");
		update.append(user.getUUID().toString());
		update.append("';");
		db.executeUpdate(update.toString());
		db.closeConnection();
	}
	
	public String getInsert(){
		if(user.getUUID() == null) return null;
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO users (uuid, name, points, kills, deaths) VALUES (");
		sb.append("'" + user.getUUID().toString() + "',");
		sb.append("'" + user.getName() + "',");
		sb.append("'" + user.getRank().getPoints() + "',");
		sb.append("'" + user.getRank().getKills() + "',");
		sb.append("'" + user.getRank().getDeaths() + "'");
		sb.append(") ON DUPLICATE KEY UPDATE ");
		sb.append("name='" + user.getName() + "',");
		sb.append("points=" + user.getRank().getPoints() + ",");
		sb.append("kills=" + user.getRank().getKills() + ",");
		sb.append("deaths=" + user.getRank().getDeaths() + ";");
		return sb.toString();
	}
	
	public static User deserialize(ResultSet rs) throws SQLException{
		if(rs == null) return null;
		
		String uuid = rs.getString("uuid");
		String name = rs.getString("name");
		int points = rs.getInt("points");
		int kills = rs.getInt("kills");
		int deaths = rs.getInt("deaths");
		
		User user = User.get(UUID.fromString(uuid));
		user.setName(name);
		user.getRank().setKills(kills);
		user.getRank().setDeaths(deaths);
		user.getRank().setPoints(points);
		
		return user;
	}
}
