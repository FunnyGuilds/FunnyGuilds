package net.dzikoysk.funnyguilds.concurrency.requests.war;

import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartAttackEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent.Click;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.feature.war.WarSystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.std.Pair;
import panda.std.stream.PandaStream;

public class WarAttackRequest extends DefaultConcurrencyRequest {

    private final FunnyServer funnyServer;
    private final GuildEntityHelper guildEntityHelper;
    private final User user;
    private final int entityId;

    public WarAttackRequest(FunnyServer funnyServer, GuildEntityHelper guildEntityHelper, User user, int entityId) {
        this.funnyServer = funnyServer;
        this.guildEntityHelper = guildEntityHelper;
        this.user = user;
        this.entityId = entityId;
    }

    @Override
    public void execute() throws Exception {
        PandaStream.of(this.guildEntityHelper.getGuildEntities().entrySet())
                .filter(entry -> entry.getValue().getId() == this.entityId)
                .map(Entry::getKey)
                .mapOpt(guild -> this.funnyServer.getPlayer(this.user)
                        .map(player -> Pair.of(player, guild))
                )
                .forEach(playerToGuild -> this.attackGuild(playerToGuild.getFirst(), playerToGuild.getSecond()));
    }

    private void attackGuild(Player player, Guild guild) {
        GuildHeartInteractEvent interactEvent = new GuildHeartInteractEvent(
                EventCause.USER,
                this.user,
                guild,
                Click.LEFT,
                !SecuritySystem.onHitCrystal(player, guild)
        );

        SimpleEventHandler.handle(interactEvent);
        if (interactEvent.isCancelled() || !interactEvent.isSecurityCheckPassed()) {
            return;
        }

        //TODO: [FG 5.0] remove deprecated event call
        if (!SimpleEventHandler.handle(new GuildHeartAttackEvent(EventCause.USER, this.user, guild))) {
            return;
        }

        WarSystem.getInstance().attack(player, guild);
    }

}
