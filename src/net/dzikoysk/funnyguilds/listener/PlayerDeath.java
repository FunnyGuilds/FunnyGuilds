package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		Player v = event.getEntity();
		Player a = event.getEntity().getKiller();
		
		if(v == null || a == null) return;
		
		Config config = Config.getInstance();
		Messages msg = Messages.getInstance();
		
		User victim = User.get(v);
		User attacker = User.get(a);
		
		victim.getRank().addDeath();
		victim.getRank().removePoints(config.rankDeath);
		
		attacker.getRank().addKill();
		attacker.getRank().addPoints(config.rankKill);
		
		IndependentThread.actions(ActionType.RANK_UPDATE_USER, victim);
		IndependentThread.action(ActionType.RANK_UPDATE_USER, attacker);
		
		if(Config.getInstance().mysql){
			if(victim.hasGuild()) IndependentThread.actions(ActionType.MYSQL_UPDATE_GUILD_POINTS, victim.getGuild());
			if(attacker.hasGuild()) IndependentThread.actions(ActionType.MYSQL_UPDATE_GUILD_POINTS, attacker.getGuild());
			IndependentThread.actions(ActionType.MYSQL_UPDATE_USER_POINTS, victim);
			IndependentThread.action(ActionType.MYSQL_UPDATE_USER_POINTS, attacker);
		}
		
		String death = msg.getMessage("rankDeathMessage");
		death = StringUtils.replace(death, "{ATTACKER}", attacker.getName());
		death = StringUtils.replace(death, "{VICTIM}", victim.getName());
		death = StringUtils.replace(death, "{-}", Integer.toString(config.rankDeath));
		death = StringUtils.replace(death, "{+}", Integer.toString(config.rankKill));
		death = StringUtils.replace(death, "{POINTS}", Integer.toString(victim.getRank().getPoints()));
		event.setDeathMessage(death);
	}

}
