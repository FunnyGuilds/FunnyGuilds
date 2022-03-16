package net.dzikoysk.funnyguilds.feature.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;

@FunnyComponent
final class GuildInvitationsCompleter implements Completer {

    private final UserManager userManager;

    GuildInvitationsCompleter(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return this.userManager.findByName(context.getCommandSender().getName())
                .map(User::getUUID)
                .map(uuid -> CommandUtils.collectCompletions(InvitationList.getInvitationGuildTags(uuid), prefix, limit, ArrayList::new, (guildName) -> guildName))
                .orElseGet(Collections.emptyList());
    }

    @Override
    public String getName() {
        return "guild-invitations";
    }

}
