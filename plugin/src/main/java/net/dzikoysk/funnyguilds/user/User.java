package net.dzikoysk.funnyguilds.user;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

    private final UserProfile profile;

    User(UUID uuid, String name, UserProfile profile) {
        this.uuid = uuid;
        this.name = name;
        this.profile = profile;

        this.cache = new UserCache(this);
        this.rank = new UserRank(this, FunnyGuilds.getInstance().getPluginConfiguration().rankStart);
        this.playerRef = new WeakReference<>(Bukkit.getPlayer(this.uuid));

        this.markChanged();
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

    @Override
    public EntityType getType() {
        return EntityType.USER;
    }

    public UserCache getCache() {
        return this.cache;
    }

    public UserRank getRank() {
        return this.rank;
    }

    public boolean isOnline() {
        return this.profile.isOnline();
    }

    public boolean isVanished() {
        return this.profile.isVanished();
    }

    public boolean hasPermission(String permission) {
        return this.profile.hasPermission(permission);
    }

    public int getPing() {
        return this.profile.getPing();
    }

    public void sendMessage(String message) {
        this.profile.sendMessage(message);
    }

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
        return this.guild
                .map(guild -> guild.isOwner(this))
                .orElseGet(false);
    }

    public boolean isDeputy() {
        return this.guild
                .map(guild -> guild.isDeputy(this))
                .orElseGet(false);
    }

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

    public UserProfile getProfile() {
        return this.profile;
    }

    // Deprecated methods
    //  - use UserGameProfile
    //  - add methods to UserGameProfile and use them
    //  - get Player/OfflinePlayer in the another way

    @Deprecated
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

    @Deprecated
    public void updateReference(Player player) {
        Validate.notNull(player, "you can't update reference with null player!");
        this.playerRef = new WeakReference<>(player);
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
        return "User{" +
                "uuid=" + this.uuid +
                ", name='" + this.name + '\'' +
                '}';
    }

}
