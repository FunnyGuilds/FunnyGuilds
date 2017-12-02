package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseBasic {

    private static DatabaseBasic instance;

    public DatabaseBasic() {
        instance = this;
    }

    public static DatabaseBasic getInstance() {

        if (instance != null) {
            return instance;
        }

        return new DatabaseBasic();
    }

    public void load() {

        Database db = Database.getInstance();

        usersTable(db);
        regionsTable(db);
        guildsTable(db);

        ResultSet users = Database.getInstance().executeQuery("SELECT * FROM `users`");
        try {
            while (users.next()) {
                User user = DatabaseUser.deserialize(users);
                if (user != null) {
                    user.changed();
                }
            }
            FunnyGuilds.info("Loaded users: " + UserUtils.getUsers().size());
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }

        ResultSet regions = Database.getInstance().executeQuery("SELECT * FROM `regions`");
        try {
            while (regions.next()) {
                Region region = DatabaseRegion.deserialize(regions);
                if (region != null) {
                    region.changed();
                }
            }
            FunnyGuilds.info("Loaded regions: " + RegionUtils.getRegions().size());
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }

        ResultSet guilds = Database.getInstance().executeQuery("SELECT * FROM `guilds`");
        try {
            while (guilds.next()) {
                Guild guild = DatabaseGuild.deserialize(guilds);
                if (guild != null) {
                    guild.changed();
                }
            }
            FunnyGuilds.info("Loaded guilds: " + GuildUtils.getGuilds().size());
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }

        // TODO
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getOwner() != null) {
                continue;
            }

            GuildUtils.deleteGuild(guild);
        }

        IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE);
    }

    public void save(boolean b) throws ClassNotFoundException, SQLException {
        Database db = Database.getInstance();
        for (User user : UserUtils.getUsers()) {
            if (!b) {
                if (!user.changed()) {
                    continue;
                }
            }
            try {
                new DatabaseUser(user).save(db);
            } catch (Exception e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
        for (Region region : RegionUtils.getRegions()) {
            if (!b) {
                if (!region.changed()) {
                    continue;
                }
            }
            try {
                new DatabaseRegion(region).save(db);
            } catch (Exception e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
        for (Guild guild : GuildUtils.getGuilds()) {
            if (!b) {
                if (!guild.changed()) {
                    continue;
                }
            }
            try {
                new DatabaseGuild(guild).save(db);
            } catch (Exception e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void guildsTable(Database db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists `guilds`(");
        sb.append("`uuid` varchar(100) not null,");
        sb.append("`name` text not null,");
        sb.append("`tag` text not null,");
        sb.append("`owner` text not null,");
        sb.append("`home` text not null,");
        sb.append("`region` text not null,");
        sb.append("`members` text not null,");
        sb.append("`regions` text not null,");
        sb.append("`points` int not null,");
        sb.append("`lives` int not null,");
        sb.append("`ban` bigint not null,");
        sb.append("`born` bigint not null,");
        sb.append("`validity` bigint not null,");
        sb.append("`pvp` boolean not null,");
        sb.append("`attacked` bigint,");
        sb.append("`allies` text,");
        sb.append("`enemies` text,");
        sb.append("`info` text,");
        sb.append("`deputy` text,");
        sb.append("primary key (uuid));");
        db.executeUpdate(sb.toString());
        /*db.executeUpdate("alter table `guilds` add `born` bigint not null;");
        db.executeUpdate("alter table `guilds` add `validity` bigint not null;");
        db.executeUpdate("alter table `guilds` add `attacked` bigint not null;");
        db.executeUpdate("alter table `guilds` add `lives` int not null;");
        db.executeUpdate("alter table `guilds` add `ban` bigint not null;");
        db.executeUpdate("alter table `guilds` add `pvp` boolean not null;");
        db.executeUpdate("alter table `guilds` add `deputy` text;");
        db.executeUpdate("alter table guilds add constraint deputy foreign key (deputy) references users (name) on update cascade;");*/
    }

    public void regionsTable(Database db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists `regions`(");
        sb.append("`name` varchar(100) not null,");
        sb.append("`center` text not null,");
        sb.append("`size` int not null,");
        sb.append("`enlarge` int not null,");
        sb.append("primary key (name));");
        db.executeUpdate(sb.toString());
    }

    public void usersTable(Database db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists `users`(");
        sb.append("`uuid` varchar(100) not null,");
        sb.append("`name` text not null,");
        sb.append("`points` int not null,");
        sb.append("`kills` int not null,");
        sb.append("`deaths` int not null,");
        sb.append("`guild` varchar(100),");
        sb.append("`ban` bigint,");
        sb.append("`reason` text,");
        sb.append("primary key (uuid));");
        db.executeUpdate(sb.toString());
        /*db.executeUpdate("alter table `users` add `ban` bigint;");
        db.executeUpdate("alter table `users` add `reason` text;");
        db.executeUpdate("alter table `users` add `guild` varchar(100);");*/
    }
}
