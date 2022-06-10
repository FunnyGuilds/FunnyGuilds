package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Locale;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.FunnybinRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.ReloadRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class FunnyGuildsCommand extends AbstractFunnyCommand {

    @Inject
    public DataModel dataModel;

    @FunnyCommand(
            name = "${user.funnyguilds.name}",
            description = "${user.funnyguilds.description}",
            aliases = "${user.funnyguilds.aliases}",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        String parameter = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "";

        switch (parameter) {
            case "reload":
            case "rl":
                this.reload(sender);
                break;
            case "check":
            case "update":
                this.plugin.getVersion().isNewAvailable(sender, true);
                break;
            case "save-all":
                this.saveAll(sender);
                break;
            case "funnybin":
                this.post(sender, args);
                break;
            case "help":
                this.messages.funnyguildsHelp.forEach(line -> sendMessage(sender, line));
                break;
            default:
                sendMessage(sender, FunnyFormatter.format(this.messages.funnyguildsVersion, "{VERSION}",
                        this.plugin.getVersion().getFullVersion()));
                break;
        }

    }

    private void reload(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.reload"), this.messages.permission);

        sendMessage(sender, this.messages.reloadReloading);
        this.plugin.getConcurrencyManager().postRequests(new ReloadRequest(this.plugin, sender));
    }

    private void saveAll(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.admin"), this.messages.permission);

        sendMessage(sender, this.messages.saveallSaving);
        long currentTime = System.currentTimeMillis();

        DataModel dataModel = this.dataModel;
        try {
            dataModel.save(false);
            this.plugin.getInvitationPersistenceHandler().saveInvitations();
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("An error occurred while saving plugin data!", exception);
            return;
        }

        String time = String.format("%.2f", (System.currentTimeMillis() - currentTime) / 1000.0D);
        sendMessage(sender, FunnyFormatter.format(this.messages.saveallSaved, "{TIME}", time));
    }

    private void post(CommandSender sender, String[] args) {
        when(!sender.hasPermission("funnyguilds.admin"), this.messages.permission);

        Option<FunnybinRequest> request = FunnybinRequest.of(sender, args);
        if (!request.isPresent()) {
            this.messages.funnybinHelp.forEach(line -> sendMessage(sender, line));
            return;
        }

        this.plugin.getConcurrencyManager().postRequests(request.get());
    }

}
