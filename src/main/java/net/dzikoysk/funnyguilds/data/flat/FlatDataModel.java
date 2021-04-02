package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseFixAlliesRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;

public class FlatDataModel implements DataModel {

    private final File guildsFolderFile;
    private final File regionsFolderFile;
    private final File usersFolderFile;

    public FlatDataModel(FunnyGuilds funnyGuilds) {
        this.guildsFolderFile = new File(funnyGuilds.getPluginDataFolder(), "guilds");
        this.regionsFolderFile = new File(funnyGuilds.getPluginDataFolder(), "regions");
        this.usersFolderFile = new File(funnyGuilds.getPluginDataFolder(), "users");

        FlatPatcher flatPatcher = new FlatPatcher();
        flatPatcher.patch(this);
    }

    public File getGuildsFolder() {
        return this.guildsFolderFile;
    }

    public File getRegionsFolder() {
        return this.regionsFolderFile;
    }

    public File getUsersFolder() {
        return this.usersFolderFile;
    }

    File loadCustomFile(BasicType type, String name) {
        switch (type) {
            case GUILD: {
                File file = new File(this.guildsFolderFile, name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            case REGION: {
                File file = new File(this.regionsFolderFile, name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            case USER: {
                File file = new File(this.usersFolderFile, name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
        }

        return null;
    }

    public File getUserFile(User user) {
        return new File(this.usersFolderFile, user.getName() + ".yml");
    }

    public File getRegionFile(Region region) {
        return new File(this.regionsFolderFile, region.getName() + ".yml");
    }

    public File getGuildFile(Guild guild) {
        return new File(this.guildsFolderFile, guild.getName() + ".yml");
    }

    @Override
    public void load() {
        this.loadUsers();
        this.loadRegions();
        this.loadGuilds();

        this.validateLoadedData();
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        this.saveUsers(ignoreNotChanged);
        this.saveRegions(ignoreNotChanged);
        this.saveGuilds(ignoreNotChanged);
    }

    private void saveUsers(boolean ignoreNotChanged) {
        if (UserUtils.getUsers().isEmpty()) {
            return;
        }

        int errors = 0;

        for (User user : UserUtils.getUsers()) {
            if (user.getUUID() == null || user.getName() == null) {
                errors++;
                continue;
            }

            if (ignoreNotChanged && !user.wasChanged()) {
                continue;
            }

            new FlatUser(user).serialize(this);
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Users save errors " + errors);
        }
    }

    private void loadUsers() {
        File[] path = usersFolderFile.listFiles();
        int errors = 0;

        if (path == null) {
            return;
        }

        for (File file : path) {
            if (file.length() == 0) {
                continue;
            }

            if (!UserUtils.validateUsername(StringUtils.removeEnd(file.getName(), ".yml"))) {
                FunnyGuilds.getPluginLogger().warning("Skipping loading of user file '" + file.getName() + "'. Name is invalid.");
                continue;
            }

            User user = FlatUser.deserialize(file);

            if (user == null) {
                errors++;
                continue;
            }

            user.wasChanged();
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Users load errors " + errors);
        }

        FunnyGuilds.getPluginLogger().info("Loaded users: " + UserUtils.getUsers().size());
    }

    private void saveRegions(boolean ignoreNotChanged) {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return;
        }

        int errors = 0;

        for (Region region : RegionUtils.getRegions()) {
            if (ignoreNotChanged && !region.wasChanged()) {
                continue;
            }

            if (!new FlatRegion(region).serialize(this)) {
                errors++;
            }
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Regions save errors " + errors);
        }
    }

    private void loadRegions() {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        File[] path = regionsFolderFile.listFiles();
        int errors = 0;

        if (path != null) {
            for (File file : path) {
                Region region = FlatRegion.deserialize(file);

                if (region == null) {
                    errors++;
                    continue;
                }

                region.wasChanged();
                RegionUtils.addRegion(region);
            }
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Guild load errors " + errors);
        }

        FunnyGuilds.getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size());
    }

    private void saveGuilds(boolean ignoreNotChanged) {
        int errors = 0;

        for (Guild guild : GuildUtils.getGuilds()) {
            if (ignoreNotChanged && ! guild.wasChanged()) {
                continue;
            }

            if (!new FlatGuild(guild).serialize(this)) {
                errors++;
            }
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Guilds save errors: " + errors);
        }
    }

    private void loadGuilds() {
        GuildUtils.getGuilds().clear();
        File[] path = guildsFolderFile.listFiles();
        int errors = 0;

        if (path != null) {
            for (File file : path) {
                Guild guild = FlatGuild.deserialize(file);

                if (guild == null) {
                    continue;
                }

                guild.wasChanged();
            }
        }

        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getOwner() != null) {
                errors++;
                continue;
            }

            FunnyGuilds.getPluginLogger().error("In guild " + guild.getTag() + " owner not exist!");
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Guild load errors " + errors);
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new DatabaseFixAlliesRequest(), new PrefixGlobalUpdateRequest());

        FunnyGuilds.getPluginLogger().info("Loaded guilds: " + GuildUtils.getGuilds().size());
    }

}
