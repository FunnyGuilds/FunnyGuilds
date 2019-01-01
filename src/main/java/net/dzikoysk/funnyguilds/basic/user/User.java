package net.dzikoysk.funnyguilds.basic.user;

import com.google.common.base.Charsets;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.AbstractBasic;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest;
import net.dzikoysk.funnyguilds.util.commons.bukkit.PingUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends AbstractBasic {

    private static final Set<UUID> ONLINE_USERS_CACHE = new HashSet<>();

    private final UUID                  uuid;
    private final String                name;
    private final UserCache             cache;
    private       WeakReference<Player> playerRef;
    private       Guild                 guild;
    private       Rank                  rank;
    private       UserBan               ban;

    private User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.cache = new UserCache(this);
        this.playerRef = new WeakReference<>(Bukkit.getPlayer(this.uuid));
        this.updateCache();
        this.changes();
    }

    private User(String name) {
        this(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)), name);
    }

    private User(Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public void removeGuild() {
        this.guild = null;
        this.changes();

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new RankUpdateUserRequest(this));
    }

    public boolean hasGuild() {
        return this.guild != null;
    }

    private void updateCache() {
        UserUtils.addUser(this);
    }

    public void removeFromCache() {
        ONLINE_USERS_CACHE.remove(this.uuid);
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
        this.changes();
    }

    public void setRank(Rank r) {
        this.rank = r;
        this.changes();
    }

    public void setBan(UserBan ban) {
        this.ban = ban;
    }

    public boolean isOwner() {
        if (!hasGuild()) {
            return false;
        }

        return this.guild.getOwner().equals(this);
    }

    public boolean isDeputy() {
        if (!hasGuild()) {
            return false;
        }

        return this.guild.getDeputies().contains(this);
    }

    public boolean isOnline() {
        if (this.name == null) {
            return false;
        }

        if (!ONLINE_USERS_CACHE.contains(this.uuid)) {
            final Player player = Bukkit.getPlayer(this.uuid);
            if (player != null) {
                ONLINE_USERS_CACHE.add(this.uuid);
                return true;
            }

            return false;
        }

        return true;
    }

    public boolean isBanned() {
        return this.ban != null && this.ban.isBanned();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public Rank getRank() {
        if (this.rank != null) {
            return this.rank;
        }

        this.rank = new Rank(this);
        RankManager.getInstance().update(this);
        this.changes();

        return this.rank;
    }

    public UserBan getBan() {
        return ban;
    }

    public Player getPlayer() {
        if (!isOnline()) {
            return null;
        }

        Player player = this.playerRef.get();

        if (player != null) {
            return player;
        }

        player = Bukkit.getPlayer(this.uuid);

        if (player != null) {
            this.playerRef = new WeakReference<>(player);
            return player;
        }

        return null;
    }

    public int getPing() {
        return PingUtils.getPing(getPlayer());
    }

    public UserCache getCache() {
        return this.cache;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public BasicType getType() {
        return BasicType.USER;
    }

    public void updateReference(Player player) {
        Validate.notNull(player, "you can't update reference with null player!");

        this.playerRef = new WeakReference<>(player);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (uuid == null ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        User user = (User) obj;

        if (!user.getUUID().equals(this.uuid)) {
            return false;
        }

        return user.getName().equals(this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static User get(UUID uuid, String name) {
        User user = UserUtils.get(uuid);
        return user != null ? user : new User(uuid, name);
    }

    public static User get(Player player) {
        User user = UserUtils.get(player.getUniqueId());
        return user != null ? user : new User(player);
    }

    public static User get(OfflinePlayer offline) {
        User user = UserUtils.get(offline.getName());
        return user != null ? user : new User(offline.getName());
    }

    public static User get(String name) {
        User user = UserUtils.get(name);
        return user != null ? user : new User(name);
    }

}
