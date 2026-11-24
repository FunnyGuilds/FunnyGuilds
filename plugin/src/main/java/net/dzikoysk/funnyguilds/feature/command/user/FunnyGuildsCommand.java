package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.telemetry.FunnybinAsyncTask;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;
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
                this.messageService.getMessage(config -> config.funnyguildsHelp)
                        .receiver(sender)
                        .send();
                break;
            default:
                this.messageService.getMessage(config -> config.funnyguildsVersion)
                        .receiver(sender)
                        .with("{VERSION}", this.plugin.getVersion().getFullVersion())
                        .send();
                break;
        }

    }

    private void saveAll(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.admin"), config -> config.permission);

        this.messageService.getMessage(config -> config.saveallSaving)
                .receiver(sender)
                .send();
        Instant startTime = Instant.now();

        DataModel dataModel = this.dataModel;
        try {
            dataModel.save(false);
            this.plugin.getInvitationPersistenceHandler().saveInvitations();
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("An error occurred while saving plugin data!", exception);
            return;
        }

        String time = TimeUtils.formatTimeSimple(Duration.between(startTime, Instant.now()));
        this.messageService.getMessage(config -> config.saveallSaved)
                .receiver(sender)
                .with("{TIME}", time)
                .send();
    }

    private void post(CommandSender sender, String[] args) {
        when(!sender.hasPermission("funnyguilds.admin"), config -> config.permission);

        FunnybinAsyncTask.of(sender, args)
                .ifPresentOrElse(
                        task -> this.plugin.scheduleFunnyTasks(task),
                        () -> this.messageService.getMessage(config -> config.funnybinHelp)
                                .receiver(sender)
                                .send()
                );
    }

    private void reload(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.reload"), config -> config.permission);

        this.messageService.getMessage(config -> config.reloadReloading)
                .receiver(sender)
                .send();
        this.plugin.scheduleFunnyTasks(new ReloadAsyncTask(this.plugin, sender));
    }

    static final class ReloadAsyncTask extends AsyncFunnyTask {

        private final FunnyGuilds plugin;
        private final CommandSender sender;
        private final Instant startTime;

        public ReloadAsyncTask(FunnyGuilds plugin, CommandSender sender) {
            this.plugin = plugin;
            this.sender = sender;
            this.startTime = Instant.now();
        }

        @Override
        public void execute() {
            AtomicReference<String> itemsError = new AtomicReference<>();
            this.plugin.reloadConfiguration(error -> {
                itemsError.set(error);
                this.plugin.getMessageService()
                        .getMessage(config -> config.reloadItemsError)
                        .receiver(this.sender)
                        .with("{ERROR}", error)
                        .send();
            });

            try {
                this.plugin.getDataPersistenceHandler().reloadHandler();
                this.plugin.getDynamicListenerManager().reloadAll();
                this.plugin.reloadTablistRendering();
            }
            catch (Exception exception) {
                FunnyGuilds.getPluginLogger().error("An error occurred while reloading plugin data!", exception);
                return;
            }

            String time = TimeUtils.formatTimeSimple(Duration.between(this.startTime, Instant.now()));
            reportReloadCompleted(this.plugin.getMessageService(), this.sender, time, itemsError.get());
        }

        static void reportReloadCompleted(MessageService messageService, CommandSender sender, String time, String itemsError) {
            if (itemsError != null) {
                messageService.getMessage(config -> config.reloadItemsPartial)
                        .receiver(sender)
                        .with("{TIME}", time)
                        .with("{ERROR}", itemsError)
                        .send();
                return;
            }
            messageService.getMessage(config -> config.reloadTime)
                    .receiver(sender)
                    .with("{TIME}", time)
                    .send();
        }

    }

}
