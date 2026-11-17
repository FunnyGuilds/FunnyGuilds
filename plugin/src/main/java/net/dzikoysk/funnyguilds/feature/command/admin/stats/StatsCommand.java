package net.dzikoysk.funnyguilds.feature.command.admin.stats;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.feature.command.admin.AdminUtils;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.command.CommandSender;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

final class StatsCommand {

    private StatsCommand() {
    }

    enum Operation {
        SET,
        ADD,
        REMOVE
    }

    @FunctionalInterface
    interface StatsEventFactory<E extends FunnyEvent> {

        E create(EventCause cause, User admin, User target, int change);

    }

    static Operation requireOperation(String value) {
        for (Operation operation : Operation.values()) {
            if (operation.name().equalsIgnoreCase(value)) {
                return operation;
            }
        }

        throw new InternalValidationException(config -> config.adminInvalidStatsOperation, Replacement.string("{OPERATION}", value));
    }

    static int resolveChange(Operation operation, int amount, int current) {
        return switch (operation) {
            case ADD -> amount;
            case REMOVE -> -amount;
            default -> amount - current;
        };
    }

    static <E extends FunnyEvent> void execute(
            MessageService messageService,
            CommandSender sender,
            String[] args,
            ToIntFunction<UserRank> getter,
            ObjIntConsumer<UserRank> setter,
            StatsEventFactory<E> eventFactory,
            ToIntFunction<E> changeExtractor,
            Function<MessageConfiguration, Sendable> noAmountMessage,
            String placeholderKey,
            Function<MessageConfiguration, Sendable> changedMessage
    ) {
        when(args.length < 1, config -> config.adminNoStatsOperationGiven);
        Operation operation = requireOperation(args[0]);
        when(args.length < 2, config -> config.generalNoNickGiven);
        when(args.length < 3, noAmountMessage);

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException exception) {
            throw new InternalValidationException(config -> config.adminErrorInNumber, Replacement.string("{ERROR}", args[2]));
        }

        User admin = AdminUtils.getAdminUser(sender);
        User target = UserValidation.requireUserByName(args[1]);
        UserRank userRank = target.getRank();

        int change = resolveChange(operation, amount, getter.applyAsInt(userRank));

        E event = eventFactory.create(AdminUtils.getCause(admin), admin, target, change);
        if (!SimpleEventHandler.handle(event)) {
            return;
        }

        int finalValue = getter.applyAsInt(userRank) + changeExtractor.applyAsInt(event);
        setter.accept(userRank, finalValue);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", target.getName())
                .register(placeholderKey, finalValue);

        messageService.getMessage(changedMessage)
                .receiver(sender)
                .with(formatter)
                .send();
    }

}
