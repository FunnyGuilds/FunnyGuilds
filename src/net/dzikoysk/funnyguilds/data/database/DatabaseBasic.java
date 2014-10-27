package net.dzikoysk.funnyguilds.data.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;

public class DatabaseBasic {
	
	private static DatabaseBasic instance;
	
	public DatabaseBasic(){
		instance = this;
		Database db = Database.getInstance();
		try {
			load(db);
		} catch (Exception e) {
			FunnyGuilds.exception(e.getCause());
		}
	}
	
	public void load(Database db) throws Exception {
		db.openConnection();
		
		usersTable(db);
		ResultSet users = Database.getInstance().executeQuery("SELECT * FROM users");
		while(users.next()) DatabaseUser.deserialize(users);
		FunnyGuilds.info("Loaded users: " + UserUtils.getUsers().size());
		
		regionsTable(db);
		ResultSet regions = Database.getInstance().executeQuery("SELECT * FROM regions");
		while(regions.next()) DatabaseRegion.deserialize(regions);
		FunnyGuilds.info("Loaded regions: " + RegionUtils.getRegions().size());
		
		guildsTable(db);
		ResultSet guilds = Database.getInstance().executeQuery("SELECT * FROM guilds");
		while(guilds.next()) DatabaseGuild.deserialize(guilds);
		FunnyGuilds.info("Loaded guilds: " + GuildUtils.getGuilds().size());
		
		db.closeConnection();
		IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE);
	}
	
	public void save() throws ClassNotFoundException, SQLException{
		Database db = Database.getInstance();
		db.openConnection();
		for(User user : UserUtils.getUsers()){
			try {
				new DatabaseUser(user).save(db);
			} catch (Exception e){
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		for(Region region : RegionUtils.getRegions()){
			try {
				new DatabaseRegion(region).save(db);
			} catch (Exception e){
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		for(Guild guild : GuildUtils.getGuilds()){
			try {
				new DatabaseGuild(guild).save(db);
			} catch (Exception e){
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		db.closeConnection();
	}
	
	public void guildsTable(Database db) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("create table if not exists guilds(");
		sb.append("uuid varchar(100) not null,");
		sb.append("name varchar(50) not null,");
		sb.append("tag varchar(50) not null,");
		sb.append("owner varchar(50) not null,");
		sb.append("home varchar(50) not null,");
		sb.append("region varchar(50) not null,");
		sb.append("members text not null,");
		sb.append("regions text not null,");
		sb.append("points int not null,");
		sb.append("allies text,");
		sb.append("enemies text,");
		sb.append("info text,");
		sb.append("deputy varchar(50),");
		sb.append("pvp boolean,");
		sb.append("primary key (uuid));");
		db.executeUpdate(sb.toString());
	}
	
	public void regionsTable(Database db) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("create table if not exists regions(");
		sb.append("name varchar(50) not null,");
		sb.append("center varchar(100) not null,");
		sb.append("size int not null,");
		sb.append("enlarge int not null,");
		sb.append("primary key (name));");
		db.executeUpdate(sb.toString());
	}
	
	public void usersTable(Database db) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("create table if not exists users(");
		sb.append("uuid varchar(100) not null,");
		sb.append("name varchar(50) not null,");
		sb.append("points int not null,");
		sb.append("kills int not null,");
		sb.append("deaths int not null,");
		sb.append("primary key (uuid));");
		db.executeUpdate(sb.toString());
	}

	public static DatabaseBasic getInstance(){
		if(instance != null) return instance;
		return new DatabaseBasic();
	}
}
