package net.dzikoysk.funnyguilds.data.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.util.Parser;

public class DatabaseRegion {

	Region region;
	
	public DatabaseRegion(Region region){
		this.region = region;
	}
	
	public void save(Database db) {
		db.executeUpdate(getInsert());
	}
	
	public void delete() {
		Database db = Database.getInstance();
		db.openConnection();
		StringBuilder update = new StringBuilder();
		update.append("DELETE FROM regions WHERE name='");
		update.append(region.getName());
		update.append("';");
		db.executeUpdate(update.toString());
		db.closeConnection();
	}
	
	public String getInsert(){
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO regions (name, center, size, enlarge) VALUES (");
		sb.append("'" + region.getName() + "',");
		sb.append("'" + Parser.toString(region.getCenter()) + "',");
		sb.append("'" + region.getSize() + "',");
		sb.append("'" + region.getEnlarge() + "'");
		sb.append(") ON DUPLICATE KEY UPDATE ");
		sb.append("center='" + Parser.toString(region.getCenter()) + "',");
		sb.append("size=" + region.getSize() + ",");
		sb.append("enlarge=" + region.getEnlarge() + ";");
		return sb.toString();
	}
	
	public static Region deserialize(ResultSet rs) throws SQLException {
		if(rs == null) return null;
		
		String name = rs.getString("name");
		String center = rs.getString("center");
		int size = rs.getInt("size");
		int enlarge = rs.getInt("enlarge");
		
		Object[] values = new Object[4];
		values[0] = name;
		values[1] = Parser.parseLocation(center);
		values[2] = size;
		values[3] = enlarge;
		Region region = Region.deserialize(values);
		return region;
	}
}
