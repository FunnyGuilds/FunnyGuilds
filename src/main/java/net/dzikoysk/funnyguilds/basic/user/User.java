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
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.BossBarProvider;
import net.dzikoysk.funnyguilds.util.commons.bukkit.PingUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.regex.Pattern;

public class User extends AbstractBasic {

    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^\\w{3,16}$");

    private final UUID                  uuid;
    private final String                name;
    private final UserCache             cache;
    private final Rank                  rank;
    private       WeakReference<Player> playerRef;
    private       Guild                 guild;
    private       UserBan               ban;
    private final BossBarProvider       bossBarProvider;

    private User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.cache = new UserCache(this);
        this.rank = new Rank(this);
        this.playerRef = new WeakReference<>(Bukkit.getPlayer(this.uuid));
        this.bossBarProvider = BossBarProvider.getBossBar(this);
        this.markChanged();
    }

    private User(String name) {
        this(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)), name);
    }

    private User(Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public void removeGuild() {
        this.guild = null;
        this.markChanged();

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new RankUpdateUserRequest(this));
    }

    public boolean hasGuild() {
        return this.guild != null;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
        this.markChanged();
    }

    public void setBan(UserBan ban) {
        this.ban = ban;
    }

    public boolean isOwner() {
        if (! hasGuild()) {
            return false;
        }

        return this.guild.getOwner().equals(this);
    }

    public boolean isDeputy() {
        if (! hasGuild()) {
            return false;
        }

        return this.guild.getDeputies().contains(this);
    }

    public boolean isOnline() {
        if (this.name == null) {
            return false;
        }

        return Bukkit.getPlayer(this.uuid) != null;
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
        return this.rank;
    }

    public UserBan getBan() {
        return ban;
    }

    public Player getPlayer() {
        if (! isOnline()) {
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

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.uuid);
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

    public BossBarProvider getBossBar() {
        return this.bossBarProvider;
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

        if (! user.getUUID().equals(this.uuid)) {
            return false;
        }

        return user.getName().equals(this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static User create(UUID uuid, String name) {
        Validate.notNull(uuid, "uuid can't be null!");
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.isTrue(VALID_USERNAME_PATTERN.matcher(name).matches(), "name is not valid!");

        User user = new User(uuid, name);
        UserUtils.addUser(user);
        RankManager.getInstance().update(user);

        return user;
    }

    public static User create(Player player) {
        Validate.notNull(player, "player can't be null!");

        User user = new User(player);
        UserUtils.addUser(user);
        RankManager.getInstance().update(user);

        return user;
    }

    public static User get(UUID uuid) {
        return UserUtils.get(uuid);
    }

    public static User get(Player player) {
        return UserUtils.get(player.getUniqueId());
    }

    public static User get(OfflinePlayer offline) {
        return UserUtils.get(offline.getName());
    }

    public static User get(String name) {
        return UserUtils.get(name);
    }
}
