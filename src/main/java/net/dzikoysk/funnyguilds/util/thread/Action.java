package net.dzikoysk.funnyguilds.util.thread;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.util.element.DummyManager;
import net.dzikoysk.funnyguilds.util.element.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.util.element.PlayerListManager;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Action {

    private final ActionType action;
    private Object[] values;

    public Action(ActionType action) {
        this.action = action;
    }

    public Action(ActionType action, Object... values) {
        this.action = action;
        this.values = values;
    }

    public ActionType getActionType() {
        return this.action;
    }

    public Object[] getValues() {
        return this.values;
    }

    public void execute() {
        switch (action) {
            case PLAYERLIST_SEND:
                ((User) values[0]).getPlayerList().send();
                break;
            case PLAYERLIST_GLOBAL_UPDATE:
                PlayerListManager.updatePlayers();
                break;
            case SAVE_DATA:
                Manager.getInstance().save();
                break;
            case MYSQL_UPDATE_USER_POINTS:
                //new DatabaseUser((User) values[0]).updatePoints();
                break;
            case MYSQL_UPDATE_GUILD_POINTS:
                //new DatabaseGuild((Guild) values[0]).updatePoints();
                break;
            case DUMMY_GLOBAL_UPDATE:
                DummyManager.updatePlayers();
                break;
            case DUMMY_GLOBAL_UPDATE_USER:
                DummyManager.updateScore((User) values[0]);
                break;
            case DUMMY_UPDATE_USER:
                ((User) values[0]).getDummy();
                break;
            case RANK_UPDATE_USER:
                RankManager.getInstance().update((User) values[0]);
                break;
            case RANK_UPDATE_GUILD:
                RankManager.getInstance().update((Guild) values[0]);
                break;
            case PREFIX_GLOBAL_ADD_GUILD:
                IndividualPrefixManager.addGuild((Guild) values[0]);
                break;
            case PREFIX_GLOBAL_ADD_PLAYER:
                IndividualPrefixManager.addPlayer((OfflineUser) values[0]);
                break;
            case PREFIX_GLOBAL_REMOVE_GUILD:
                IndividualPrefixManager.removeGuild((Guild) values[0]);
                break;
            case PREFIX_GLOBAL_REMOVE_PLAYER:
                IndividualPrefixManager.removePlayer((OfflineUser) values[0]);
                break;
            case PREFIX_GLOBAL_UPDATE:
                IndividualPrefixManager.updatePlayers();
                break;
            case PREFIX_GLOBAL_UPDATE_PLAYER:
                IndividualPrefixManager.updatePlayer((Player) values[0]);
                break;
            case PREFIX_UPDATE_GUILD:
                ((User) values[0]).getIndividualPrefix().addGuild((Guild) values[1]);
                break;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this.getClass() != o.getClass())
            return false;
        Action a = (Action) o;
        if (action != a.getActionType())
            return false;
        if (values == null && a.getValues() == null)
            return true;
        return false;
    }

}
