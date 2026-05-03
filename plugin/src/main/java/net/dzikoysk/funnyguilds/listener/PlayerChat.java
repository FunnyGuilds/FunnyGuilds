package net.dzikoysk.funnyguilds.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildChatEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildChatEvent.Type;
import net.dzikoysk.funnyguilds.event.guild.GuildPreChatEvent;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.permission.GenericGuildPermissions;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionChecker;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class PlayerChat extends AbstractFunnyListener {
    
    private static final PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText();

    @Inject
    private GuildManager guildManager;

    @Inject
    private GuildPermissionChecker permissionChecker;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        Option<User> userOption = this.userManager.findByPlayer(player);
        if (userOption.isEmpty()) {
            return;
        }
        User user = userOption.get();

        Component message = event.originalMessage();
        user.getGuild()
                .map(guild -> this.sendGuildMessage(
                        user,
                        player,
                        guild,
                        message
                ))
                .peekIf(sent -> sent, ignored -> {
                    event.setCancelled(true);
                    if (this.config.logGuildChat) {
                        String plainMessage = PLAIN_TEXT_SERIALIZER.serialize(message);
                        FunnyGuilds.getPluginLogger().info("[Guild Chat] " + player.getName() + ": " + plainMessage);
                    }
                });
    }

    private boolean sendGuildMessage(
            User user,
            Player player,
            Guild guild,
            Component message
    ) {
        if (this.sendMessageToAllGuilds(
                user,
                player,
                guild,
                message
        )) {
            return true;
        }

        if (this.sendMessageToGuildAllies(
                user,
                player,
                guild,
                message
        )) {
            return true;
        }

        return this.sendMessageToGuildMembers(
                user,
                player,
                guild,
                message
        );
    }

    private boolean sendMessageToGuildMembers(
            User user,
            Player player,
            Guild guild,
            Component message
    ) {
        return this.sendMessageToGuilds(
                user,
                player,
                guild,
                this.config.chatPrivDesign,
                this.config.chatPriv,
                message,
                Collections.singleton(guild),
                Type.PRIVATE
        );
    }

    private boolean sendMessageToGuildAllies(
            User user,
            Player player,
            Guild guild,
            Component message
    ) {
        Set<Guild> allies = new HashSet<>(guild.getAllies());
        allies.add(guild);

        return this.sendMessageToGuilds(
                user,
                player,
                guild,
                this.config.chatAllyDesign,
                this.config.chatAlly,
                message,
                allies,
                Type.ALLY
        );
    }

    private boolean sendMessageToAllGuilds(
            User user,
            Player player,
            Guild guild,
            Component message
    ) {
        return this.sendMessageToGuilds(
                user,
                player,
                guild,
                this.config.chatGlobalDesign,
                this.config.chatGlobal,
                message,
                this.guildManager.getGuilds(),
                Type.ALL
        );
    }

    private boolean sendMessageToGuilds(
            User user,
            Player player,
            Guild playerGuild,
            Component chatDesign,
            String prefix,
            Component inputMessage,
            Set<Guild> receivers,
            Type type
    ) {
        String plainInputMessage = PLAIN_TEXT_SERIALIZER.serialize(inputMessage);
        int prefixLength = prefix.length();

        if (plainInputMessage.length() > prefixLength && plainInputMessage.substring(
                0,
                prefixLength
        ).equalsIgnoreCase(prefix)) {
            if (!this.handleUsePermission(
                    user,
                    playerGuild,
                    type
            )) {
                return true;
            }
            
            String subMessage = plainInputMessage.substring(prefixLength).trim();
            Component formattedMessage = this.formatChatDesign(
                    user,
                    player,
                    playerGuild,
                    chatDesign,
                    subMessage
            );

            GuildPreChatEvent preChatEvent = new GuildPreChatEvent(
                    EventCause.USER,
                    user,
                    playerGuild,
                    type,
                    receivers,
                    inputMessage,
                    formattedMessage
            );
            if (!SimpleEventHandler.handle(preChatEvent)) {
                return true;
            }

            this.spy(
                    user,
                    player,
                    playerGuild,
                    plainInputMessage
            );
            preChatEvent.getReceivers().forEach(guild -> this.sendMessageToGuild(
                    guild,
                    formattedMessage,
                    type
            ));

            SimpleEventHandler.handle(new GuildChatEvent(
                    EventCause.USER,
                    user,
                    playerGuild,
                    type,
                    receivers,
                    inputMessage,
                    formattedMessage
            ));

            return true;
        }

        return false;
    }

    private boolean handleUsePermission(
            User user,
            Guild guild,
            Type type
    ) {
        GuildPermission<Boolean> permission = ChatType.getChatType(type).getUsePermission();
        return this.permissionChecker.handlePermission(
                guild,
                user,
                permission
        );
    }

    private void sendMessageToGuild(
            Guild guild,
            Component message,
            Type type
    ) {
        PandaStream.of(guild.getMembers())
                .filterNot(member -> member.getCache().isSpy())
                .filter(member -> this.checkSeePermission(
                        member,
                        guild,
                        type
                ))
                .forEach(member -> member.sendMessage(message));
    }

    private boolean checkSeePermission(
            User user,
            Guild guild,
            Type type
    ) {
        ChatType chatType = ChatType.getChatType(type);
        return this.permissionChecker.getPermissionValue(
                        guild,
                        user,
                        chatType.getSeePermission()
                )
                .orElse(() -> this.permissionChecker.getPermissionValue(
                        guild,
                        user,
                        chatType.getUsePermission()
                ))
                .orElseGet(false);
    }

    private void spy(
            User user,
            Player player,
            Guild playerGuild,
            String message
    ) {
        Component spyMessage = this.formatChatDesign(
                user,
                player,
                playerGuild,
                this.config.chatSpyDesign,
                message
        );

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(onlinePlayer -> this.userManager.findByPlayer(onlinePlayer))
                .filter(onlineUser -> onlineUser.getCache().isSpy())
                .forEach(onlineUser -> onlineUser.sendMessage(spyMessage));
    }

    private Component formatChatDesign(
            User user,
            Player player,
            Guild playerGuild,
            Component chatDesign,
            String message
    ) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", player.getName())
                .register("{TAG}", playerGuild.getTag())
                .register("{POS}", this.config.chatPosition)
                .register("{POS}", UserUtils.getUserPosition(this.permissionChecker, user))
                .register("{MESSAGE}", message);
        formatter.register(HookUtils.placeholdersReplaceable(player));

        return this.messageService.replaceInComponent(user, chatDesign, formatter);
    }

    private enum ChatType {
        PRIVATE(
                GenericGuildPermissions.GUILD_CHAT_USE,
                GenericGuildPermissions.GUILD_CHAT_SEE
        ),
        ALLY(
                GenericGuildPermissions.ALLY_CHAT_USE,
                GenericGuildPermissions.ALLY_CHAT_SEE
        ),
        ALL(
                GenericGuildPermissions.GLOBAL_CHAT_USE,
                GenericGuildPermissions.GLOBAL_CHAT_SEE
        );

        private final GuildPermission<Boolean> usePermission;
        private final GuildPermission<Boolean> seePermission;

        ChatType(
                GuildPermission<Boolean> usePermission,
                GuildPermission<Boolean> seePermission
        ) {
            this.usePermission = usePermission;
            this.seePermission = seePermission;
        }

        private GuildPermission<Boolean> getUsePermission() {
            return this.usePermission;
        }

        private GuildPermission<Boolean> getSeePermission() {
            return this.seePermission;
        }

        private static ChatType getChatType(Type type) {
            return switch (type) {
                case PRIVATE -> PRIVATE;
                case ALLY -> ALLY;
                case ALL -> ALL;
            };
        }
    }

}
