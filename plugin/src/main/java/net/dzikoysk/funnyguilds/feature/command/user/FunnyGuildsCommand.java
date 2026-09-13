package net.dzikoysk.funnyguilds.feature.command.user;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.upgrades.UpgradeDefinition;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.telemetry.FunnybinAsyncTask;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
            case "additem":
                this.additem(sender, args);
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

    private void additem(CommandSender sender, String[] args) {
        when(!sender.hasPermission("funnyguilds.admin"), config -> config.permission);
        when(!(sender instanceof Player), config -> config.upgradesAdditemPlayerOnly);

        when(args.length < 4 || !"upgrade".equalsIgnoreCase(args[1]), config -> config.upgradesAdditemUsage);

        String key = args[2];
        UpgradeDefinition definition = this.config.guildUpgrades.findUpgrade(key).orElse(null);
        if (definition == null) {
            throw new InternalValidationException(
                    config -> config.upgradesAdditemUnknownUpgrade,
                    Replacement.string("{UPGRADE}", key),
                    Replacement.string("{UPGRADES}", String.join(", ", this.config.guildUpgrades.getUpgrades().keySet()))
            );
        }

        int level = parseLevel(args[3]);
        if (level < 1 || level > definition.getMaxLevel()) {
            throw new InternalValidationException(
                    config -> config.upgradesAdditemInvalidLevel,
                    Replacement.string("{LEVEL}", args[3]),
                    Replacement.string("{MAX_LEVEL}", String.valueOf(definition.getMaxLevel()))
            );
        }

        Player player = (Player) sender;
        ItemStack handItem = player.getInventory().getItemInMainHand();
        when(handItem.getType() == Material.AIR, config -> config.upgradesAdditemNoItemInHand);

        this.upgradeItemStore.setRequiredItem(key, level, handItem.clone());

        this.messageService.getMessage(config -> config.upgradesAdditemSaved)
                .receiver(sender)
                .with("{UPGRADE}", key)
                .with("{LEVEL}", level)
                .send();
    }

    private static int parseLevel(String raw) {
        try {
            return Integer.parseInt(raw);
        }
        catch (NumberFormatException exception) {
            return -1;
        }
    }

    private void post(CommandSender sender, String[] args) {
        when(!sender.hasPermission("funnyguilds.admin"), config -> config.permission);

        FunnybinAsyncTask.of(sender, args)
                .onEmpty(() -> this.messageService.getMessage(config -> config.funnybinHelp)
                        .receiver(sender)
                        .send())
                .peek(task -> this.plugin.scheduleFunnyTasks(task));
    }

    private void reload(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.reload"), config -> config.permission);

        this.messageService.getMessage(config -> config.reloadReloading)
                .receiver(sender)
                .send();
        this.plugin.scheduleFunnyTasks(new ReloadAsyncTask(this.plugin, sender));
    }

    private static final class ReloadAsyncTask extends AsyncFunnyTask {

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
            this.plugin.reloadConfiguration();
            this.plugin.getDataPersistenceHandler().reloadHandler();
            this.plugin.getDynamicListenerManager().reloadAll();
            this.plugin.reloadTablistRendering();
            
            String time = TimeUtils.formatTimeSimple(Duration.between(this.startTime, Instant.now()));
            FunnyGuilds.getInstance().getMessageService().getMessage(config -> config.reloadTime)
                    .receiver(this.sender)
                    .with("{TIME}", time)
                    .send();
        }

    }

}
