package net.dzikoysk.funnyguilds.config.message;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.config.ConfigurationFactory;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.stream.PandaStream;
import pl.peridot.yetanothermessageslibrary.SimpleSendableMessageService;
import pl.peridot.yetanothermessageslibrary.viewer.BukkitViewerDataSupplier;
import pl.peridot.yetanothermessageslibrary.viewer.SimpleViewer;
import pl.peridot.yetanothermessageslibrary.viewer.SimpleViewerService;

public class MessageService extends SimpleSendableMessageService<CommandSender, MessageConfiguration, FunnyMessageDispatcher> {

    private final BukkitAudiences adventure;

    public MessageService(BukkitAudiences adventure, BiConsumer<Runnable, Long> schedule) {
        super(
                new SimpleViewerService<>(
                        new BukkitViewerDataSupplier(adventure),
                        (receiver, audience, console) -> new SimpleViewer(audience, console, schedule)
                ),
                (viewerService, localeSupplier, messageSupplier) -> new FunnyMessageDispatcher(viewerService, localeSupplier, messageSupplier, user -> Bukkit.getPlayer(user.getUUID()))
        );
        this.adventure = adventure;
    }

    public void reload() {
        this.getMessageRepositories().forEach((locale, repository) -> repository.load());
    }

    public void playerQuit(Player player) {
        this.getViewerService().removeViewer(player);
    }

    public void close() {
        this.adventure.close();
    }

    public static MessageService prepareMessageService(FunnyGuilds plugin, File languageFolder) {
        FunnyGuildsLogger logger = plugin.getPluginLogger();
        PluginConfiguration config = plugin.getPluginConfiguration();

        MessageService messageService = new MessageService(
                BukkitAudiences.create(plugin),
                (runnable, delay) -> Bukkit.getScheduler().runTaskLater(plugin, runnable, delay)
        );
        messageService.setDefaultLocale(config.defaultLocale);
        messageService.registerLocaleProvider(new PlayerLocaleProvider());
        messageService.registerLocaleProvider(new UserLocaleProvider(plugin.getFunnyServer()));

        // TODO: Remove in 5.0
        File oldMessagesFile = new File(plugin.getDataFolder(), "messages.yml");
        File newMessagesFile = new File(languageFolder, config.defaultLocale.toString() + ".yml");
        if (oldMessagesFile.exists() && !newMessagesFile.exists() && !oldMessagesFile.renameTo(newMessagesFile)) {
            logger.warning("Could not copy legacy messages.yml to new lang directory");
        }

        PandaStream.of(config.availableLocales).forEach(locale -> {
            String localeName = locale.toString();
            File localeFile = new File(languageFolder, localeName + ".yml");
            if (!localeFile.exists()) {
                try {
                    FunnyIOUtils.copyFileFromResources(FunnyGuilds.class.getResourceAsStream("/lang/" + localeName + ".yml"), localeFile, true);
                } catch (IOException | NullPointerException ex) {
                    logger.warning("Could not copy default language file: " + localeName);
                    logger.warning("New language file will be created with default values");
                }
            }
            messageService.registerRepository(locale, ConfigurationFactory.createMessageConfiguration(localeFile));
        });
        return messageService;
    }

}
