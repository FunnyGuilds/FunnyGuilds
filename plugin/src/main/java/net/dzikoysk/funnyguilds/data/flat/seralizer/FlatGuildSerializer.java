package net.dzikoysk.funnyguilds.data.flat.seralizer;

import java.io.File;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.GuildAuditSerializer;
import net.dzikoysk.funnyguilds.data.util.GuildRoleSerializer;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public final class FlatGuildSerializer {

    private FlatGuildSerializer() {
    }

    public static Optional<Guild> deserialize(File file) {
        if (file.isDirectory()) {
            return Optional.empty();
        }

        FunnyGuilds plugin = FunnyGuilds.getInstance();
        FunnyGuildsLogger logger = FunnyGuilds.getPluginLogger();
        UserManager userManager = plugin.getUserManager();
        GuildManager guildManager = plugin.getGuildManager();
        RegionManager regionManager = plugin.getRegionManager();
        PluginConfiguration config = plugin.getPluginConfiguration();

        YamlWrapper wrapper = new YamlWrapper(file);

        String id = wrapper.getString("uuid");
        String name = wrapper.getString("name");
        String tag = wrapper.getString("tag");
        String ownerName = wrapper.getString("owner");
        String deputyName = wrapper.getString("deputy");
        String hs = wrapper.getString("home");
        String regionName = wrapper.getString("region");
        boolean pvp = wrapper.getBoolean("pvp");
        Instant born = TimeUtils.positiveOrNullInstant(wrapper.getLong("born"));
        Instant validity = TimeUtils.positiveOrNullInstant(wrapper.getLong("validity"));
        Instant protection = TimeUtils.positiveOrNullInstant(wrapper.getLong("protection"));
        Instant ban = TimeUtils.positiveOrNullInstant(wrapper.getLong("ban"));
        Instant tntProtectionBypass = TimeUtils.positiveOrNullInstant(wrapper.getLong("tnt-protection-bypass"));
        int lives = wrapper.getInt("lives");
        int heartLives = wrapper.contains("heart-lives") ? wrapper.getInt("heart-lives") : config.war.heartLives;
        int warsWon = wrapper.getInt("wars-won");
        int warsLost = wrapper.getInt("wars-lost");
        String lastWar = wrapper.getString("last-war");
        int warStreak = wrapper.getInt("war-streak");
        Instant lastLifeBuy = TimeUtils.positiveOrNullInstant(wrapper.getLong("last-life-buy"));

        if (name == null) {
            logger.deserialize("Cannot deserialize guild, caused by: name is null");
            return Optional.empty();
        }

        if (tag == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: tag is null");
            return Optional.empty();
        }

        if (ownerName == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: owner is null");
            return Optional.empty();
        }

        Region region = null;
        if (config.regionsEnabled) {
            if (regionName == null) {
                logger.deserialize("Cannot deserialize guild: " + name + ", caused by: region is null");
                return Optional.empty();
            }

            Optional<Region> regionOption = regionManager.findByName(regionName);
            if (regionOption.isEmpty()) {
                logger.deserialize("Cannot deserialize guild: " + name + ", caused by: region (object) is null");
                return Optional.empty();
            }

            region = regionOption.orElseThrow();
        }

        UUID uuid = UUID.randomUUID();
        if (id != null && !id.isEmpty()) {
            uuid = UUID.fromString(id);
        }

        Optional<User> ownerOption = userManager.findByName(ownerName);
        if (ownerOption.isEmpty()) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: owner is null");
            return Optional.empty();
        }

        Set<User> deputies = ConcurrentHashMap.newKeySet(1);
        if (!FunnyStringUtils.isEmpty(deputyName)) {
            deputies = userManager.findByNames(FunnyStringUtils.fromString(deputyName));
        }

        Location home = null;
        if (region != null) {
            home = region.getCenter();

            if (hs != null) {
                home = LocationUtils.parseLocation(hs);
            }
        }

        Set<String> memberNames = loadSet(wrapper, "members");
        if (memberNames == null || memberNames.isEmpty()) {
            memberNames = new HashSet<>();
            memberNames.add(ownerName);
        }

        Set<User> members = userManager.findByNames(memberNames);
        Set<Guild> allies = guildManager.findByNames(loadSet(wrapper, "allies"));
        Set<Guild> enemies = guildManager.findByNames(loadSet(wrapper, "enemies"));

        if (born == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: born is null");
            return Optional.empty();
        }

        if (validity == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: validity is null");
            return Optional.empty();
        }

        if (protection == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: protection is null");
            return Optional.empty();
        }

        if (lives == 0) {
            lives = config.war.lives;
        }

        Object[] values = new Object[26];
        values[0] = uuid;
        values[1] = name;
        values[2] = tag;
        values[3] = ownerOption.orElseThrow();
        values[4] = home;
        values[5] = region;
        values[6] = members;
        values[7] = allies;
        values[8] = enemies;
        values[9] = born;
        values[10] = validity;
        values[11] = protection;
        values[12] = lives;
        values[13] = ban;
        values[14] = deputies;
        values[15] = tntProtectionBypass;
        values[16] = pvp;
        values[23] = loadSet(wrapper, "allied-pvp");
        values[24] = wrapper.getString("role-policy");
        values[25] = wrapper.getString("audit-log");
        values[17] = heartLives;
        values[18] = warsWon;
        values[19] = warsLost;
        values[20] = lastWar;
        values[21] = warStreak;
        values[22] = lastLifeBuy;

        return DeserializationUtils.deserializeGuild(config, guildManager, values);
    }

    public static boolean serialize(Guild guild) {
        FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();

        if (guild.getOwner() == null) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + ", caused by: owner is null");
            return false;
        }

        if (!guild.hasRegion() && FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + ", caused by: region is null");
            return false;
        }

        return dataModel.persistYaml(guild, values -> {
            values.put("uuid", guild.getUUID().toString());
            values.put("name", guild.getName());
            values.put("tag", guild.getTag());
            values.put("owner", guild.getOwner().getName());
            values.put("home", LocationUtils.toString(guild.getHome()));
            values.put("members", List.copyOf(Entity.names(guild.getMembers())));
            values.put("region", RegionUtils.toString(guild.getRegion()));
            values.put("regions", null);
            values.put("allies", List.copyOf(Entity.names(guild.getAllies())));
            values.put("enemies", List.copyOf(Entity.names(guild.getEnemies())));
            values.put("born", guild.getBorn().toEpochMilli());
            values.put("validity", guild.getValidity().toEpochMilli());
            values.put("protection", guild.getProtection().toEpochMilli());
            values.put("lives", guild.getLives());
            values.put("heart-lives", guild.getHeartLives());
            values.put("wars-won", guild.getWarsWon());
            values.put("wars-lost", guild.getWarsLost());
            values.put("last-war", guild.getLastWarGuildTag().orElse(null));
            values.put("war-streak", guild.getWarStreak());
            values.put("last-life-buy", guild.getLastLifeBuy().map(Instant::toEpochMilli).orElse(0L));
            values.put("ban", guild.getBan().map(Instant::toEpochMilli).orElse(0L));
            values.put("tnt-protection-bypass", guild.getTntProtectionBypass().map(Instant::toEpochMilli).orElse(0L));
            values.put("pvp", guild.hasPvPEnabled());
            values.put("allied-pvp", guild.getAlliedPvPGuilds().stream().map(UUID::toString).sorted().toList());
            values.put("role-policy", GuildRoleSerializer.serialize(guild.getRolePolicy()));
            values.put("audit-log", GuildAuditSerializer.serialize(guild, FunnyGuilds.getInstance().getPluginConfiguration().guildAudit));
            values.put("deputy", FunnyStringUtils.join(Entity.names(guild.getDeputies()), false));
        });
    }

    @SuppressWarnings("unchecked")
    private static Set<String> loadSet(YamlWrapper data, String key) {
        Object collection = data.get(key);

        if (collection instanceof List) {
            return new HashSet<>((List<String>) collection);
        }
        else if (collection instanceof Set) {
            return (Set<String>) collection;
        }
        else if (collection instanceof ConfigurationSection) {
            return ((ConfigurationSection) collection).getKeys(false);
        }

        return Collections.emptySet();
    }

}
