package net.dzikoysk.funnyguilds.concurrency.requests.war;

import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartAttackEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent.Click;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.feature.war.WarSystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.std.Pair;
import panda.std.stream.PandaStream;

public class WarAttackRequest extends DefaultConcurrencyRequest {

    private final FunnyGuilds plugin;
    private final GuildEntityHelper guildEntityHelper;

    private final User user;
    private final int entityId;

    public WarAttackRequest(FunnyGuilds plugin, GuildEntityHelper guildEntityHelper, User user, final int entityId) {
        this.plugin = plugin;
        this.guildEntityHelper = guildEntityHelper;

        this.user = user;
        this.entityId = entityId;
    }

    @Override
    public void execute() throws Exception {
        try (PandaStream<Entry<Guild, FakeEntity>> entries = PandaStream.of(this.guildEntityHelper.getGuildEntities().entrySet())) {
            entries.filter(entry -> entry.getValue().getId() == this.entityId)
                    .map(Entry::getKey)
                    .mapOpt(guild -> plugin.getFunnyServer().getPlayer(user.getUUID()).map(player -> Pair.of(player, guild)))
                    .forEach(playerToGuild -> this.attackGuild(playerToGuild.getFirst(), playerToGuild.getSecond()));
        }
    }

    private void attackGuild(Player player, Guild guild) {
        GuildHeartInteractEvent interactEvent = new GuildHeartInteractEvent(EventCause.USER, user, guild, Click.LEFT,
                !SecuritySystem.onHitCrystal(player, guild));
        SimpleEventHandler.handle(interactEvent);

        if (interactEvent.isCancelled() || !interactEvent.isSecurityCheckPassed()) {
            return;
        }

        if (!SimpleEventHandler.handle(new GuildHeartAttackEvent(EventCause.USER, user, guild))) {
            return;
        }

        WarSystem.getInstance().attack(player, guild);
    }

}
