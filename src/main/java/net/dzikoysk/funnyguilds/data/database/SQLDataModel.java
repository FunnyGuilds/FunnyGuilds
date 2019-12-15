package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;

import java.util.HashSet;
import java.util.Set;

public class SQLDataModel implements DataModel {

    private static SQLDataModel instance;

    public SQLDataModel() {
        instance = this;
    }

    public static SQLDataModel getInstance() {
        if (instance != null) {
            return instance;
        }

        return new SQLDataModel();
    }

    public void load() {
        Database db = Database.getInstance();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        usersTable(db);
        regionsTable(db);
        guildsTable(db);

        Database.getInstance().executeQuery("SELECT * FROM `" + config.mysql.usersTableName + "`", usersResult -> {
            try {
                while (usersResult.next()) {

                    String userName = usersResult.getString("name");
                    if (!UserUtils.validateUsername(userName)) {
                        FunnyGuilds.getInstance().getPluginLogger().warning("Skipping loading of user '" + userName + "'. Name is invalid.");
                        continue;
                    }

                    User user = DatabaseUser.deserialize(usersResult);
                    if (user != null) {
                        user.wasChanged();
                    }
                }

                FunnyGuilds.getInstance().getPluginLogger().info("Loaded users: " + UserUtils.getUsers().size());
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not load users from database", ex);
            }
        });


        if (FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            Database.getInstance().executeQuery("SELECT * FROM `" + config.mysql.regionsTableName + "`", regionsResult -> {
                try {
                    while (regionsResult.next()) {
                        Region region = DatabaseRegion.deserialize(regionsResult);
                        if (region != null) {
                            region.wasChanged();
                        }
                    }

                    FunnyGuilds.getInstance().getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size());
                }
                catch (Exception ex) {
                    FunnyGuilds.getInstance().getPluginLogger().error("Could not load regions from database", ex);
                }
            });

        } else {
            FunnyGuilds.getInstance().getPluginLogger().info("Regions are disabled and thus - not loaded");
        }

        Database.getInstance().executeQuery("SELECT * FROM `" + config.mysql.guildsTableName + "`", guildsResult -> {
            try {
                while (guildsResult.next()) {
                    Guild guild = DatabaseGuild.deserialize(guildsResult);
                    if (guild != null) {
                        guild.wasChanged();
                    }
                }

                FunnyGuilds.getInstance().getPluginLogger().info("Loaded guilds: " + GuildUtils.getGuilds().size());
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not load guilds from database", ex);
            }
        });

        Database.getInstance().executeQuery("SELECT `tag`, `allies` FROM `" + config.mysql.guildsTableName + "`", result -> {
            try {
                while (result.next()) {
                    Guild guild = GuildUtils.getByTag(result.getString("tag"));

                    if (guild == null) {
                        continue;
                    }

                    String alliesList = result.getString("allies");

                    Set<Guild> allies = new HashSet<>();

                    if (alliesList != null && !alliesList.equals("")) {
                        allies = GuildUtils.getGuilds(ChatUtils.fromString(alliesList));
                    }

                    guild.setAllies(allies);
                }
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not load allies from database", ex);
            }
        });

        // TODO
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getOwner() != null) {
                continue;
            }

            GuildUtils.deleteGuild(guild);
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalUpdateRequest());
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        Database db = Database.getInstance();
        for (User user : UserUtils.getUsers()) {
            if (ignoreNotChanged) {
                if (! user.wasChanged()) {
                    continue;
                }
            }
            
            try {
                new DatabaseUser(user).save(db);
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not save user to database", ex);
            }
        }

        if (FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            for (Region region : RegionUtils.getRegions()) {
                if (ignoreNotChanged) {
                    if (! region.wasChanged()) {
                        continue;
                    }
                }
                
                try {
                    new DatabaseRegion(region).save(db);
                }
                catch (Exception ex) {
                    FunnyGuilds.getInstance().getPluginLogger().error("Could not save region to database", ex);
                }
            }
        }
        
        for (Guild guild : GuildUtils.getGuilds()) {
            if (ignoreNotChanged) {
                if (! guild.wasChanged()) {
                    continue;
                }
            }
            
            try {
                new DatabaseGuild(guild).save(db);
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not save guild to database", ex);
            }
        }
    }

    public void guildsTable(Database db) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("create table if not exists `");
        sb.append(FunnyGuilds.getInstance().getPluginConfiguration().mysql.guildsTableName);
        sb.append("`(`uuid` varchar(100) not null,");
        sb.append("`name` text not null,");
        sb.append("`tag` text not null,");
        sb.append("`owner` text not null,");
        sb.append("`home` text not null,");
        sb.append("`region` text not null,");
        sb.append("`regions` text not null,");
        sb.append("`members` text not null,");
        sb.append("`points` int not null,");
        sb.append("`lives` int not null,");
        sb.append("`ban` bigint not null,");
        sb.append("`born` bigint not null,");
        sb.append("`validity` bigint not null,");
        sb.append("`pvp` boolean not null,");
        sb.append("`attacked` bigint,");
        sb.append("`allies` text,");
        sb.append("`info` text,");
        sb.append("`deputy` text,");
        sb.append("primary key (uuid));");
        
        db.executeUpdate(sb.toString());
    }

    public void regionsTable(Database db) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("create table if not exists `");
        sb.append(FunnyGuilds.getInstance().getPluginConfiguration().mysql.regionsTableName);
        sb.append("`(`name` varchar(100) not null,");
        sb.append("`center` text not null,");
        sb.append("`size` int not null,");
        sb.append("`enlarge` int not null,");
        sb.append("primary key (name));");
        
        db.executeUpdate(sb.toString());
    }

    public void usersTable(Database db) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("create table if not exists `");
        sb.append(FunnyGuilds.getInstance().getPluginConfiguration().mysql.usersTableName);
        sb.append("`(`uuid` varchar(36) not null,");
        sb.append("`name` text not null,");
        sb.append("`points` int not null,");
        sb.append("`kills` int not null,");
        sb.append("`deaths` int not null,");
        sb.append("`guild` varchar(100),");
        sb.append("`ban` bigint,");
        sb.append("`reason` text,");
        sb.append("primary key (uuid));");
        
        db.executeUpdate(sb.toString());
    }
    
}
