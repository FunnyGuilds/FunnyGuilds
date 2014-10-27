package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.element.PlayerListManager;
import net.dzikoysk.funnyguilds.util.element.PlayerListScheme;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Config {
	
	private static Config instance;
	
	private YamlConfiguration yml;
	
	public int createNameLength;
	public int createNameMinLength;
	public int createTagLength;	
	public int createTagMinLength;
	public List<ItemStack> createItems;
	public int createDistance;
	public Material createMaterial;
	
	public int regionSize;
	public int regionMaxSize;
	public int regionMinDistance;
	public int regionNotificationTime;
	public int regionNotificationCooldown;
	public int regionExplode;
	
	public boolean enlargeEnable;
	public int enlargeSize;
	public List<ItemStack> enlargeItems;
	
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
	public int rankDeath;
	public int rankKill;
	
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
	public String excPlayer;
	public String excTop;
	public String excG;
	
	public int dataInterval;
	public boolean flat;
	public boolean mysql;
	public String mysqlHostname;
	public String mysqlPort;
	public String mysqlDatabase;
	public String mysqlUser;
	public String mysqlPassword;
	
	public boolean patchScoreboard;
	public boolean mcstats;
	public boolean debug;
	
	public Config(){
		instance = this;
		this.yml =  YamlConfiguration.loadConfiguration(new File(FunnyGuilds.getInstance().getDataFolder(), "config.yml"));
		this.loadCreateSection();
		this.loadRegionsSection();
		this.loadEnlargeSection();
		this.loadInviteSection();
		this.loadPrefixSection();
		this.loadChatSection();
		this.loadRankSection();
		this.loadDamageSection();
		this.loadBaseSection();
		this.loadPlayerListSection();
		this.loadCommandsSection();
		this.loadDataSection();
		this.loadUtilsSection();
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
		this.createDistance = yml.getInt("create-distance");
		this.createMaterial = Parser.parseMaterial(yml.getString("create-material"));
	}
	
	private void loadRegionsSection(){
		this.regionSize = yml.getInt("region-size");
		this.regionMinDistance = yml.getInt("region-min-distance");
		this.regionNotificationTime = yml.getInt("region-notification-time");
		this.regionNotificationCooldown = yml.getInt("region-notification-cooldown");
		this.regionExplode = yml.getInt("region-explode");
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
	
	private void loadInviteSection(){
		this.inviteMembers = yml.getInt("invite-members");
	}
	
	private void loadPrefixSection(){
		this.prefixOur = ChatColor.translateAlternateColorCodes('&', yml.getString("prefix-our"));
		this.prefixAllies = ChatColor.translateAlternateColorCodes('&', yml.getString("prefix-allies"));
		this.prefixEnemies = ChatColor.translateAlternateColorCodes('&', yml.getString("prefix-enemies"));
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
		this.rankKill = yml.getInt("rank-kill");
		this.rankDeath = yml.getInt("rank-death");
	}
	
	private void loadDamageSection(){
		this.damageGuild = yml.getBoolean("damage-guild");
		this.damageAlly = yml.getBoolean("damage-ally");
	}
	
	private void loadBaseSection(){
		this.baseEnable = yml.getBoolean("base-enable");
		if(this.baseEnable){
			List<String> list = yml.getStringList("items");
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
		this.excCreate = yml.getString("commands.create");
		this.excDelete = yml.getString("commands.delete");
		this.excConfirm = yml.getString("commands.confirm");
		this.excInvite = yml.getString("commands.invite");
		this.excJoin = yml.getString("commands.join");
		this.excLeave = yml.getString("commands.leave");
		this.excKick = yml.getString("commands.kick");
		this.excBase = yml.getString("commands.base");
		this.excEnlarge = yml.getString("commands.enlarge");
		this.excGuild = yml.getString("commands.guild");
		this.excG = yml.getString("commands.g");
		this.excAlly = yml.getString("commands.ally");
		this.excBreak = yml.getString("commands.break");
		this.excPlayer = yml.getString("commands.player");
		this.excTop = yml.getString("commands.top");
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
	
	private void loadUtilsSection() {
		this.patchScoreboard = yml.getBoolean("patch-scoreboard");
		this.mcstats = yml.getBoolean("mcstats");
		this.debug = yml.getBoolean("debug");
	}
	
	public static Config getInstance(){
		if(instance != null) return instance;
		return new Config();
	}
}
