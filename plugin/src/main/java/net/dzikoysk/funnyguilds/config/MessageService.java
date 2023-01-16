package net.dzikoysk.funnyguilds.config;

import java.util.Collections;
import java.util.Locale;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import pl.peridot.yetanothermessageslibrary.SimpleSendableMessageService;
import pl.peridot.yetanothermessageslibrary.adventure.BukkitAudienceSupplier;
import pl.peridot.yetanothermessageslibrary.locale.StaticLocaleProvider;

public class MessageService extends SimpleSendableMessageService<CommandSender, MessageConfiguration> {

    public MessageService(BukkitAudiences adventure, MessageConfiguration messageConfiguration) {
        super(new BukkitAudienceSupplier(adventure), Locale.ENGLISH, StaticLocaleProvider.of(Locale.forLanguageTag("pl")), Collections.singletonMap(Locale.forLanguageTag("pl"), messageConfiguration)); //TODO load message configuration per locale
    }

}
