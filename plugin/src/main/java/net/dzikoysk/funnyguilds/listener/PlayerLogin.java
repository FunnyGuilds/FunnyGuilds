package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.feature.ban.BanUtils;
import net.dzikoysk.funnyguilds.shared.FunnyValidator;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerLogin extends AbstractFunnyListener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() != Result.ALLOWED) {
            return;
        }

        String name = event.getPlayer().getName();
        switch (FunnyValidator.validateUsername(this.config, name)) {
            case TOO_SHORT:
                event.disallow(Result.KICK_OTHER, this.messages.loginNickTooShort);
                break;
            case TOO_LONG:
                event.disallow(Result.KICK_OTHER, this.messages.loginNickTooLong);
                break;
            case INVALID:
                event.disallow(Result.KICK_OTHER, this.messages.loginNickInvalid);
                break;
            case VALID:
                break;
        }

        this.userManager.findByPlayer(event.getPlayer())
                .peek(BanUtils::checkIfBanShouldExpire)
                .filter(User::isBanned)
                .peek(user -> event.disallow(Result.KICK_BANNED, BanUtils.getBanMessage(user)));
    }

}
