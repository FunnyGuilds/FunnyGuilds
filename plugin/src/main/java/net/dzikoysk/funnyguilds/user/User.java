package net.dzikoysk.funnyguilds.user;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarProvider;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.PingUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class User extends AbstractMutableEntity {

    private final UUID uuid;
    private String name;

    private final UserCache cache;
    private final UserRank rank;
    private WeakReference<Player> playerRef;
    private Option<Guild> guild = Option.none();
    private Option<UserBan> ban = Option.none();
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

    User(Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public void sendMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        this.getPlayer()
                .peek(player -> {
                    player.sendMessage(message);
                });
    }

    public boolean hasPermission(String permission) {
        return this.getPlayer()
                .map(player -> player.hasPermission(permission))
                .orElseGet(false);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserCache getCache() {
        return this.cache;
    }

    public UserRank getRank() {
        return this.rank;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.uuid);
    }

    /**
     * @return bukkit player
     */
    public Option<Player> getPlayer() {
        Player player = this.playerRef.get();
        if (player != null) {
            return Option.of(player);
        }

        player = Bukkit.getPlayer(this.uuid);
        if (player != null) {
            this.playerRef = new WeakReference<>(player);
            return Option.of(player);
        }

        return Option.none();
    }

    public void updateReference(Player player) {
        Validate.notNull(player, "you can't update reference with null player!");
        this.playerRef = new WeakReference<>(player);
    }

    public boolean isOnline() {
        if (this.name == null) {
            return false;
        }

        return Bukkit.getPlayer(this.uuid) != null;
    }

    public boolean isVanished() {
        if (!isOnline()) {
            return false;
        }

        // Should work with VanishNoPacket, SuperVanish and PremiumVanish
        return getPlayer()
                .map(player -> player.getMetadata("vanished"))
                .map(metadata -> metadata.stream().anyMatch(MetadataValue::asBoolean))
                .orElseGet(false);
    }

    public int getPing() {
        return getPlayer()
                .map(PingUtils::getPing)
                .orElseGet(0);
    }

    /**
     * @return user's guild
     */
    public Option<Guild> getGuild() {
        return this.guild;
    }

    public boolean hasGuild() {
        return this.guild.isPresent();
    }

    public void setGuild(@Nullable Guild guild) {
        this.guild = Option.of(guild);
        this.markChanged();
    }

    public void removeGuild() {
        this.guild = Option.none();
        this.markChanged();
    }

    public boolean canManage() {
        return isOwner() || isDeputy();
    }

    public boolean isOwner() {
        if (!hasGuild()) {
            return false;
        }

        return this.guild
                .map(Guild::getOwner)
                .map(owner -> owner.equals(this))
                .orElseGet(false);
    }

    public boolean isDeputy() {
        if (!hasGuild()) {
            return false;
        }

        return this.guild
                .map(Guild::getDeputies)
                .map(deputies -> deputies.contains(this))
                .orElseGet(false);
    }

    /**
     * @return user's ban
     */
    public Option<UserBan> getBan() {
        return this.ban;
    }

    public boolean isBanned() {
        return this.ban
                .map(UserBan::isBanned)
                .orElseGet(false);
    }

    public void setBan(@Nullable UserBan ban) {
        this.ban = Option.of(ban);
    }

    public BossBarProvider getBossBar() {
        return this.bossBarProvider;
    }

    @Override
    public EntityType getType() {
        return EntityType.USER;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        User user = (User) obj;

        return this.uuid.equals(user.uuid);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
