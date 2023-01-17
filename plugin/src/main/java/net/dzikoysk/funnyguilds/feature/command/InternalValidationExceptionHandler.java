package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.DetailedExceptionHandler;
import net.dzikoysk.funnyguilds.config.message.MessageService;

class InternalValidationExceptionHandler implements DetailedExceptionHandler<InternalValidationException> {

    private final MessageService messageService;

    InternalValidationExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Class<InternalValidationException> getExceptionType() {
        return InternalValidationException.class;
    }

    @Override
    public Boolean apply(Context context, InternalValidationException ex) {
        this.messageService.getMessage(ex.getMessageSupplier())
                .with(ex.getReplacements())
                .receiver(context.getCommandSender())
                .send();
        return true;
    }

}
