package net.dzikoysk.funnyguilds.user;

import java.util.Optional;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.IdentifiableEntity;
import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class User extends AbstractMutableEntity implements IdentifiableEntity {

    private final UUID uuid;
    private String name;

    private final UserCache cache;
    private final UserRank rank;
    private volatile Optional<Guild> guild = Optional.empty();
    private Optional<UserBan> ban = Optional.empty();
    private String lastIP;

    private final UserProfile profile;

    User(UUID uuid, String name, UserProfile profile) {
        this.uuid = uuid;
        this.name = name;
        this.profile = profile;

        this.cache = new UserCache(this);
        this.rank = new UserRank(this);

        this.markChanged();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    void setName(String name) {
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
    
    public void sendMessage(Component message) {
        this.profile.sendMessage(message);
    }

    public Optional<Guild> getGuild() {
        return this.guild;
    }

    public boolean hasGuild() {
        return this.guild.isPresent();
    }

    public void setGuild(@Nullable Guild guild) {
        Guild previous = this.guild.orElse(null);
        if (previous == guild) {
            return;
        }
        this.guild = Optional.ofNullable(guild);
        this.markChanged();
        if (previous != null) {
            previous.removeMember(this);
        }
        if (guild != null) {
            guild.addMember(this);
        }
    }

    public void removeGuild() {
        this.setGuild(null);
    }

    public boolean isOwner() {
        return this.guild
                .map(guild -> guild.isOwner(this))
                .orElse(false);
    }

    public boolean isDeputy() {
        return this.guild
                .map(guild -> guild.isDeputy(this))
                .orElse(false);
    }

    public Optional<UserBan> getBan() {
        return this.ban;
    }

    public boolean isBanned() {
        return this.ban
                .map(UserBan::isBanned)
                .orElse(false);
    }

    public void setBan(@Nullable UserBan ban) {
        this.ban = Optional.ofNullable(ban);
    }

    public String getLastIP() {
        return this.lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
        this.markChanged();
    }

    public UserProfile getProfile() {
        return this.profile;
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
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
        return "User{uuid=" + this.uuid + ", name='" + this.name + "'}";
    }

}
