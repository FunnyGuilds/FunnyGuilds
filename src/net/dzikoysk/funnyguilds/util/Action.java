package net.dzikoysk.funnyguilds.util;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.data.DataManager;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseUser;
import net.dzikoysk.funnyguilds.util.element.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.util.element.PlayerListManager;

public class Action {
	
	private final ActionType action;
	private Object[] values;

	public Action(ActionType action){
		this.action = action;
	}
	
	public Action(ActionType action, Object... values){
		this.action = action;
		this.values = values;
	}
	
	public ActionType getActionType(){
		return action;
	}
	
	public Object[] getValues(){
		return values;
	}
	
	public void execute(){
		if(action == ActionType.SAVE_DATA) DataManager.getInstance().save();
		
		else if(action == ActionType.MYSQL_UPDATE_USER_POINTS) new DatabaseUser((User)values[0]).updatePoints();
		else if(action == ActionType.MYSQL_UPDATE_GUILD_POINTS) new DatabaseGuild((Guild)values[0]).updatePoints();
		
		else if(action == ActionType.RANK_UPDATE_USER) RankManager.getInstance().update((User)values[0]);
		else if(action == ActionType.RANK_UPDATE_GUILD) RankManager.getInstance().update((Guild)values[0]);
		
		else if(action == ActionType.PLAYERLIST_SEND) ((User) values[0]).getPlayerList().send();
		else if(action == ActionType.PLAYERLIST_GLOBAL_UPDATE) PlayerListManager.updatePlayers();
		
		else if(action == ActionType.PREFIX_UPDATE_GUILD) ((User) values[0]).getIndividualPrefix().addGuild((Guild)values[1]);
		else if(action == ActionType.PREFIX_GLOBAL_UPDATE) IndividualPrefixManager.updatePlayers();
		else if(action == ActionType.PREFIX_GLOBAL_UPDATE_PLAYER) IndividualPrefixManager.updatePlayer((Player)values[0]);
		else if(action == ActionType.PREFIX_GLOBAL_ADD_GUILD) IndividualPrefixManager.addGuild((Guild)values[0]);
		else if(action == ActionType.PREFIX_GLOBAL_ADD_PLAYER) IndividualPrefixManager.addPlayer((OfflinePlayer)values[0]);
		else if(action == ActionType.PREFIX_GLOBAL_REMOVE_GUILD) IndividualPrefixManager.removeGuild((Guild)values[0]);
		else if(action == ActionType.PREFIX_GLOBAL_REMOVE_PLAYER) IndividualPrefixManager.removePlayer((OfflinePlayer)values[0]);
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
		if(o == null) return false;
		if(this.getClass() != o.getClass()) return false;
		Action a = (Action) o;
		if(action != a.getActionType()) return false;
		return Arrays.equals(values, a.getValues());
	}
	
}
