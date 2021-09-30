package net.dzikoysk.funnyguilds.user;

import com.google.common.base.Charsets;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest;
import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarProvider;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.PingUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class User extends AbstractMutableEntity {

    private final UUID uuid;
    private String name;
    private final UserCache cache;
    private final UserRank rank;
    private WeakReference<Player> playerRef;
    private Guild guild;
    private UserBan ban;
    private final BossBarProvider bossBarProvider;

    User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.cache = new UserCache(this);
        this.rank = new UserRank(this, FunnyGuilds.getInstance().getPluginConfiguration().rankStart);
        this.playerRef = new WeakReference<>(Bukkit.getPlayer(this.uuid));
        this.bossBarProvider = BossBarProvider.getBossBar(this);
        this.markChanged();
    }

    private User(String name) {
        this(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)), name);
    }

    User(Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public void removeGuild() {
        this.guild = null;
        this.markChanged();

        FunnyGuilds plugin = FunnyGuilds.getInstance();
        plugin.getConcurrencyManager().postRequests(new RankUpdateUserRequest(plugin.getRankManager(), plugin.getUserManager()));
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

    public boolean canManage() {
        return isOwner() || isDeputy();
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

    public UserRank getRank() {
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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public EntityType getType() {
        return EntityType.USER;
    }

    public void updateReference(Player player) {
        Validate.notNull(player, "you can't update reference with null player!");

        this.playerRef = new WeakReference<>(player);
    }

    public boolean sendMessage(String message) {
        Player player = getPlayer();

        if (player == null) {
            return false;
        }

        player.sendMessage(message);
        return true;
    }

    public BossBarProvider getBossBar() {
        return this.bossBarProvider;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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

        return user.getUUID().equals(this.uuid);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
