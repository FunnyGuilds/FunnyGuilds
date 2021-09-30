package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.FunnybinRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.ReloadRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Optional;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class FunnyGuildsCommand extends AbstractFunnyCommand {

    @Inject public DataModel dataModel;

    @FunnyCommand(
        name = "${user.funnyguilds.name}",
        description = "${user.funnyguilds.description}",
        aliases = "${user.funnyguilds.aliases}",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        String parameter = args.length > 0
                ? args[0].toLowerCase()
                : "";

        switch (parameter) {
            case "reload":
            case "rl":
                reload(sender);
                break;
            case "check":
            case "update":
                plugin.getVersion().isNewAvailable(sender, true);
                break;
            case "save-all":
                saveAll(sender);
                break;
            case "funnybin":
                post(sender, args);
                break;
            case "help":
                sender.sendMessage(ChatColor.AQUA + "FunnyGuilds Help:");
                sender.sendMessage(ChatColor.GRAY + "/funnyguilds (reload|rl) - przeladuj plugin");
                sender.sendMessage(ChatColor.GRAY + "/funnyguilds (update|check) - sprawdz dostepnosc aktualizacji");
                sender.sendMessage(ChatColor.GRAY + "/funnyguilds save-all - zapisz wszystko");
                sender.sendMessage(ChatColor.GRAY + "/funnyguilds funnybin - zapisz konfigurację online (~ usprawnia pomoc na https://github.com/FunnyGuilds/FunnyGuilds/issues)");
                break;
            default:
                sender.sendMessage(ChatColor.GRAY + "FunnyGuilds " + ChatColor.AQUA + plugin.getVersion().getFullVersion() + ChatColor.GRAY + " by " + ChatColor.AQUA + "FunnyGuilds Team");
                break;
        }

    }

    private void reload(CommandSender sender) {
        when (!sender.hasPermission("funnyguilds.reload"), messages.permission);

        sender.sendMessage(ChatColor.GRAY + "Przeladowywanie...");
        this.plugin.getConcurrencyManager().postRequests(new ReloadRequest(this.plugin, sender));
    }

    private void saveAll(CommandSender sender) {
        when (!sender.hasPermission("funnyguilds.admin"), messages.permission);

        sender.sendMessage(ChatColor.GRAY + "Zapisywanie...");
        long currentTime = System.currentTimeMillis();

        DataModel dataModel = this.dataModel;

        try {
            dataModel.save(false);
            this.plugin.getInvitationPersistenceHandler().saveInvitations();
        }
        catch (Exception e) {
            FunnyGuilds.getPluginLogger().error("An error occurred while saving plugin data!", e);
            return;
        }

        sender.sendMessage(ChatColor.GRAY + "Zapisano (" + ChatColor.AQUA + (System.currentTimeMillis() - currentTime) / 1000.0F + "s" + ChatColor.GRAY + ")!");
    }

    private void post(CommandSender sender, String[] args) {
        when (!sender.hasPermission("funnyguilds.admin"), messages.permission);
        Optional<FunnybinRequest> request = FunnybinRequest.of(sender, args);

        if (request.isPresent()) {
            this.plugin.getConcurrencyManager().postRequests(request.get());
            return;
        }

        sender.sendMessage(ChatColor.RED + "Uzycie: ");
        sender.sendMessage(ChatColor.RED + "/fg funnybin - domyslnie wysyla FunnyGuilds/config.yml i logs/latest.log");
        sender.sendMessage(ChatColor.RED + "/fg funnybin config - wysyla FunnyGuilds/config.yml");
        sender.sendMessage(ChatColor.RED + "/fg funnybin log - wysyla logs/latest.log");
        sender.sendMessage(ChatColor.RED + "/fg funnybin custom <path> - wysyla dowolny plik z folderu serwera na funnybina");
        sender.sendMessage(ChatColor.RED + "/fg funnybin bundle <file1> <fileN...> - wysyla dowolne pliki z folderu serwera na funnybina");
    }

}
