package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.element.PlayerListManager;
import net.dzikoysk.funnyguilds.util.element.PlayerListScheme;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Settings {
	
	private static Settings instance;
	private static String version = "2.2 Valor";
	private static File settings =  new File("plugins/FunnyGuilds", "config.yml");
	
	private YamlConfiguration yml;
	
	public int createNameLength;
	public int createNameMinLength;
	public int createTagLength;	
	public int createTagMinLength;
	public List<ItemStack> createItems;
	public List<ItemStack> createItemsVip;
	public int createDistance;
	public Material createMaterial;
	public String createStringMaterial;
	public int createCenterY;
	public boolean createCenterSphere;
	
	public int regionSize;
	public int regionMaxSize;
	public int regionMinDistance;
	public int regionNotificationTime;
	public int regionNotificationCooldown;
	public int regionExplode;
	public List<String> regionCommands;
	
	public boolean eventPhysics;
	
	public boolean enlargeEnable;
	public int enlargeSize;
	public List<ItemStack> enlargeItems;
	
	public int warLives;
	public long warProtection;
	public long warWait;
	
	public long validityStart;
	public long validityTime;
	public List<ItemStack> validityItems;
	
	public int inviteMembers;
	
	public String prefixOur;
	public String prefixAllies;
	public String prefixEnemies;
	public String prefixOther;
	
	public String chatGuild;
	public String chatRank;
	public String chatPoints;
	
	public String chatPriv;
	public String chatAlly;
	public String chatGlobal;
	public String chatPrivDesign;
	public String chatAllyDesign;
	public String chatGlobalDesign;
	
	public int rankStart;
	public double rankPercent;
	
	public boolean damageGuild;
	public boolean damageAlly;
	
	public boolean baseEnable;
	public int baseDelay;
	public List<ItemStack> baseItems;
	
	public boolean playerlistEnable;
	public int playerlistInterval;
	public int playerlistPing;
	public boolean playerlistPatch;
	
	public String excCreate;
	public String excDelete;
	public String excConfirm;
	public String excInvite;
	public String excJoin;
	public String excLeave;
	public String excKick;
	public String excBase;
	public String excEnlarge;
	public String excGuild;
	public String excAlly;
	public String excBreak;
	public String excInfo;
	public String excPlayer;
	public String excTop;
	public String excValidity;
	public String excLeader;
	public String excDeputy;
	
	public List<String> excCreateAliases;
	public List<String> excDeleteAliases;
	public List<String> excConfirmAliases;
	public List<String> excInviteAliases;
	public List<String> excJoinAliases;
	public List<String> excLeaveAliases;
	public List<String> excKickAliases;
	public List<String> excBaseAliases;
	public List<String> excEnlargeAliases;
	public List<String> excGuildAliases;
	public List<String> excAllyAliases;
	public List<String> excBreakAliases;
	public List<String> excInfoAliases;
	public List<String> excPlayerAliases;
	public List<String> excTopAliases;
	public List<String> excValidityAliases;
	public List<String> excLeaderAliases;
	public List<String> excDeputyAliases;
	
	public String axcMain;
	public String axcAdd;
	public String axcDelete;
	public String axcKick;
	public String axcTeleport;
	public String axcPoints;
	public String axcKills;
	public String axcDeaths;
	public String axcBan;
	public String axcLives;
	
	public int dataInterval;
	public boolean flat;
	public boolean mysql;
	public String mysqlHostname;
	public String mysqlPort;
	public String mysqlDatabase;
	public String mysqlUser;
	public String mysqlPassword;
	
	public Settings(){
		instance = this;
		this.load();
	}
	
	private void load(){
		this.update();
		this.loadCreateSection();
		this.loadRegionsSection();
		this.loadEventSection();
		this.loadEnlargeSection();
		this.loadWarSection();
		this.loadValiditySection();
		this.loadInviteSection();
		this.loadPrefixSection();
		this.loadChatSection();
		this.loadRankSection();
		this.loadDamageSection();
		this.loadBaseSection();
		this.loadPlayerListSection();
		this.loadCommandsSection();
		this.loadDataSection();
	}

	private void update(){
		this.yml =  YamlConfiguration.loadConfiguration(settings);
		String version = yml.getString("config-version");
		if(version != null && version.equals(Settings.version)) return;
		FunnyGuilds.info("Updating the plugin settings ...");
		Map<String, Object> values = yml.getValues(true);
		settings.delete();
		DataManager.loadDefaultFiles(new String[] { "config.yml" });
		yml = YamlConfiguration.loadConfiguration(settings);
		for(Entry<String, Object> entry : values.entrySet()) yml.set(entry.getKey(), entry.getValue());
		try {
			yml.save(settings);
			FunnyGuilds.info("Successfully updated settings!");
		} catch (IOException e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
	}
	
	private void loadCreateSection(){
		this.createNameLength = yml.getInt("name-length");
		this.createTagLength = yml.getInt("tag-length");
		
		this.createNameMinLength = yml.getInt("name-min-length");
		this.createTagMinLength = yml.getInt("tag-min-length");
		
		List<String> list = yml.getStringList("items");
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(String item : list){
			ItemStack itemstack = Parser.parseItem(item);
			if(itemstack != null) items.add(itemstack);
		}
		this.createItems = items;
		
		list = yml.getStringList("items-vip");
		items = new ArrayList<ItemStack>();
		for(String item : list){
			ItemStack itemstack = Parser.parseItem(item);
			if(itemstack != null) items.add(itemstack);
		}
		this.createItemsVip = items;
		
		this.createDistance = yml.getInt("create-distance");
		this.createStringMaterial = yml.getString("create-material");
		this.createMaterial = Parser.parseMaterial(createStringMaterial);
		this.createCenterY = yml.getInt("create-center-y");
		this.createCenterSphere = yml.getBoolean("create-center-sphere");
	}
	
	private void loadRegionsSection(){
		this.regionSize = yml.getInt("region-size");
		this.regionMinDistance = yml.getInt("region-min-distance");
		this.regionNotificationTime = yml.getInt("region-notification-time");
		this.regionNotificationCooldown = yml.getInt("region-notification-cooldown");
		this.regionExplode = yml.getInt("region-explode");
		this.regionCommands = yml.getStringList("region-commands");
	}
	
	private void loadEventSection(){
		if(this.createMaterial != null && this.createMaterial == Material.DRAGON_EGG)
			this.eventPhysics = true;
	}
	
	private void loadEnlargeSection() {
		this.enlargeEnable = yml.getBoolean("enlarge-enable");
		if(this.enlargeEnable){
			this.enlargeSize = yml.getInt("enlarge-size");
		
			List<String> list = yml.getStringList("enlarge-items");
			List<ItemStack> items = new ArrayList<ItemStack>();
			for(String item : list){
				ItemStack itemstack = Parser.parseItem(item);
				if(itemstack != null) items.add(itemstack);
			}
			this.enlargeItems = items;
		}
	}
	
	private void loadWarSection(){
		this.warLives = yml.getInt("war-lives");
		this.warProtection = Parser.parseTime(yml.getString("war-protection"));
		this.warWait = Parser.parseTime(yml.getString("war-wait"));
	}
	
	private void loadValiditySection(){
		this.validityStart = Parser.parseTime(yml.getString("validity-start"));
		this.validityTime = Parser.parseTime(yml.getString("validity-time"));
		List<String> list = yml.getStringList("validity-items");
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(String item : list){
			ItemStack itemstack = Parser.parseItem(item);
			if(itemstack != null) items.add(itemstack);
		}
		this.validityItems = items;
	}
	
	private void loadInviteSection(){
		this.inviteMembers = yml.getInt("invite-members");
	}
	
	private void loadPrefixSection(){
		this.prefixOur = ChatColor.translateAlternateColorCodes('&', yml.getString("prefix-our"));
		this.prefixAllies = ChatColor.translateAlternateColorCodes('&', yml.getString("prefix-allies"));
		this.prefixOther = ChatColor.translateAlternateColorCodes('&', yml.getString("prefix-other"));
	}
	
	private void loadChatSection(){
		this.chatGuild = ChatColor.translateAlternateColorCodes('&', yml.getString("chat-guild"));
		this.chatRank = ChatColor.translateAlternateColorCodes('&', yml.getString("chat-rank"));
		this.chatPoints = ChatColor.translateAlternateColorCodes('&', yml.getString("chat-points"));
		
		this.chatPriv = yml.getString("chat-priv");
		this.chatAlly = yml.getString("chat-ally");
		this.chatGlobal = yml.getString("chat-global");
		this.chatPrivDesign = ChatColor.translateAlternateColorCodes('&', yml.getString("chat-priv-design"));
		this.chatAllyDesign = ChatColor.translateAlternateColorCodes('&', yml.getString("chat-ally-design"));
		this.chatGlobalDesign = ChatColor.translateAlternateColorCodes('&', yml.getString("chat-global-design"));
	}
	
	private void loadRankSection(){
		this.rankStart = yml.getInt("rank-start");
		this.rankPercent = yml.getDouble("rank-percent");
		if(this.rankPercent == 0) this.rankPercent = 1.0;
	}
	
	private void loadDamageSection(){
		this.damageGuild = yml.getBoolean("damage-guild");
		this.damageAlly = yml.getBoolean("damage-ally");
	}
	
	private void loadBaseSection(){
		this.baseEnable = yml.getBoolean("base-enable");
		if(this.baseEnable){
			List<String> list = yml.getStringList("base-items");
			List<ItemStack> items = new ArrayList<ItemStack>();
			for(String item : list){
				ItemStack itemstack = Parser.parseItem(item);
				if(itemstack != null) items.add(itemstack);
			}
			this.baseItems = items;
			this.baseDelay = yml.getInt("base-delay");
		}
	}
	
	private void loadPlayerListSection(){
		String[] ss = new String[60];
		for(String path : yml.getConfigurationSection("player-list").getKeys(true)){
			try {
				int i = Integer.parseInt(path);
				if(i > 60) continue;
				String s = yml.getString("player-list." + path);
				if(s != null) s = ChatColor.translateAlternateColorCodes('&', s);
				ss[i-1] = s;
			} catch (NumberFormatException e){
				FunnyGuilds.parser("Unknown number: " + path);
			}
		}
		new PlayerListScheme(ss);
		this.playerlistEnable = yml.getBoolean("player-list-enable");
		this.playerlistInterval = yml.getInt("player-list-interval");
		this.playerlistPing = yml.getInt("player-list-ping");
		this.playerlistPatch = yml.getBoolean("player-list-patch");
		PlayerListManager.enable(this.playerlistEnable);
		PlayerListManager.patch(this.playerlistPatch);
		PlayerListManager.ping(this.playerlistPing);
	}
	
	private void loadCommandsSection(){
		this.excCreate = yml.getString("commands.create.name");
		this.excDelete = yml.getString("commands.delete.name");
		this.excConfirm = yml.getString("commands.confirm.name");
		this.excInvite = yml.getString("commands.invite.name");
		this.excJoin = yml.getString("commands.join.name");
		this.excLeave = yml.getString("commands.leave.name");
		this.excKick = yml.getString("commands.kick.name");
		this.excBase = yml.getString("commands.base.name");
		this.excEnlarge = yml.getString("commands.enlarge.name");
		this.excGuild = yml.getString("commands.guild.name");
		this.excAlly = yml.getString("commands.ally.name");
		this.excBreak = yml.getString("commands.break.name");
		this.excInfo = yml.getString("commands.info.name");
		this.excPlayer = yml.getString("commands.player.name");
		this.excTop = yml.getString("commands.top.name");
		this.excValidity = yml.getString("commands.validity.name");
		this.excLeader = yml.getString("commands.leader.name");
		this.excDeputy = yml.getString("commands.deputy.name");
		
		this.excCreateAliases = yml.getStringList("commands.create.aliases");
		this.excDeleteAliases = yml.getStringList("commands.delete.aliases");
		this.excConfirmAliases = yml.getStringList("commands.confirm.aliases");
		this.excInviteAliases = yml.getStringList("commands.invite.aliases");
		this.excJoinAliases = yml.getStringList("commands.join.aliases");
		this.excLeaveAliases = yml.getStringList("commands.leave.aliases");
		this.excKickAliases = yml.getStringList("commands.kick.aliases");
		this.excBaseAliases = yml.getStringList("commands.base.aliases");
		this.excEnlargeAliases = yml.getStringList("commands.enlarge.aliases");
		this.excGuildAliases = yml.getStringList("commands.guild.aliases");
		this.excAllyAliases = yml.getStringList("commands.ally.aliases");
		this.excBreakAliases = yml.getStringList("commands.break.aliases");
		this.excInfoAliases = yml.getStringList("commands.info.aliases");
		this.excPlayerAliases = yml.getStringList("commands.player.aliases");
		this.excTopAliases = yml.getStringList("commands.top.aliases");
		this.excValidityAliases = yml.getStringList("commands.validity.aliases");
		this.excLeaderAliases = yml.getStringList("commands.leader.aliases");
		this.excDeputyAliases = yml.getStringList("commands.deputy.aliases");
		
		this.axcMain = yml.getString("commands.admin.main");
		this.axcAdd = yml.getString("commands.admin.add");
		this.axcDelete = yml.getString("commands.admin.delete");
		this.axcKick = yml.getString("commands.admin.kick");
		this.axcTeleport = yml.getString("commands.admin.teleport");
		this.axcPoints = yml.getString("commands.admin.points");
		this.axcKills = yml.getString("commands.admin.kills");
		this.axcDeaths = yml.getString("commands.admin.deaths");
		this.axcBan = yml.getString("commands.admin.ban");
		this.axcLives = yml.getString("commands.admin.lives");
	}
	
	private void loadDataSection(){
		this.dataInterval = yml.getInt("data-interval");
		this.flat = yml.getBoolean("data-type.flat");
		this.mysql = yml.getBoolean("data-type.mysql");
		if(this.mysql){
			this.mysqlHostname = yml.getString("mysql.hostname");
			this.mysqlPort = yml.getString("mysql.port");
			this.mysqlDatabase = yml.getString("mysql.database");
			this.mysqlUser = yml.getString("mysql.user");
			this.mysqlPassword = yml.getString("mysql.password");
		}
	}
	
	public static Settings getInstance(){
		if(instance != null) return instance;
		return new Settings();
	}
}
