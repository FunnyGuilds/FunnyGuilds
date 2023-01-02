package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Locale;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.telemetry.FunnybinAsyncTask;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.stream.PandaStream;

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
                this.messages.funnyguildsHelp.forEach(line -> this.sendMessage(sender, line));
                break;
            default:
                this.sendMessage(sender, FunnyFormatter.format(this.messages.funnyguildsVersion, "{VERSION}",
                        this.plugin.getVersion().getFullVersion()));
                break;
        }

    }

    private void saveAll(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.admin"), this.messages.permission);

        this.sendMessage(sender, this.messages.saveallSaving);
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
        this.sendMessage(sender, FunnyFormatter.format(this.messages.saveallSaved, "{TIME}", time));
    }

    private void post(CommandSender sender, String[] args) {
        when(!sender.hasPermission("funnyguilds.admin"), this.messages.permission);

        FunnybinAsyncTask.of(sender, args)
                .onEmpty(() -> this.messages.funnybinHelp.forEach(line -> this.sendMessage(sender, line)))
                .peek(task -> this.plugin.scheduleFunnyTasks(task));
    }

    private void reload(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.reload"), this.messages.permission);

        this.sendMessage(sender, this.messages.reloadReloading);
        this.plugin.scheduleFunnyTasks(new ReloadAsyncTask(this.plugin, sender));
    }

    private static final class ReloadAsyncTask extends AsyncFunnyTask {

        private final FunnyGuilds plugin;
        private final CommandSender sender;
        private final long startTime;

        public ReloadAsyncTask(FunnyGuilds plugin, CommandSender sender) {
            this.plugin = plugin;
            this.sender = sender;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public void execute() {
            this.plugin.reloadConfiguration();
            this.plugin.getDataPersistenceHandler().reloadHandler();
            this.plugin.getDynamicListenerManager().reloadAll();

            if (this.plugin.getTablistConfiguration().playerListEnable) {
                TablistConfiguration tablistConfig = this.plugin.getTablistConfiguration();
                UserManager userManager = this.plugin.getUserManager();

                PandaStream.of(Bukkit.getOnlinePlayers())
                        .flatMap(userManager::findByPlayer)
                        .forEach(user -> {
                            IndividualPlayerList playerList = new IndividualPlayerList(
                                    user,
                                    this.plugin.getNmsAccessor().getPlayerListAccessor(),
                                    this.plugin.getFunnyServer(),
                                    tablistConfig.playerList,
                                    tablistConfig.playerListHeader, tablistConfig.playerListFooter,
                                    tablistConfig.playerListAnimated, tablistConfig.pages,
                                    tablistConfig.heads.textures,
                                    tablistConfig.playerListPing,
                                    tablistConfig.playerListFillCells
                            );

                            user.getCache().setPlayerList(playerList);
                        });
            }

            long endTime = System.currentTimeMillis();
            String diff = String.format("%.2f", (endTime - this.startTime) / 1000.0D);

            String message = FunnyFormatter.format(this.plugin.getMessageConfiguration().reloadTime, "{TIME}", diff);
            ChatUtils.sendMessage(this.sender, message);
        }

    }

}
