/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.event.RegionDeleteEvent;
import co.marcin.novaguilds.api.util.IConverter;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.util.NonNullArrayList;
import co.marcin.novaguilds.impl.util.bossbar.BossBarUtils;
import co.marcin.novaguilds.impl.util.converter.NameToGuildConverterImpl;
import co.marcin.novaguilds.impl.util.converter.UUIDToGuildConverterImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.manager.RankManager;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.apache.commons.lang.Validate;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class NovaGuildImpl extends AbstractResource implements NovaGuild {
	private int id;
	private String name;
	private String tag;
	private final List<NovaRegion> regions = new ArrayList<>();
	private String leaderName;
	private NovaPlayer leader;
	private Location homeLocation;
	private double money = 0;
	private int points;
	private NovaRaid raid;
	private long timeRest;
	private long lostLiveTime;
	private long inactiveTime;
	private long timeCreated;
	private int lives;
	private int slots;
	private BannerMeta bannerMeta;
	private boolean openInvitation = false;
	private boolean friendlyPvp = false;
	private Location vaultLocation;
	private Hologram vaultHologram;
	private final LoadingWrapper loadingWrapper;

	private final List<NovaPlayer> players = new ArrayList<>();

	private final List<NovaGuild> allies = new NonNullArrayList<>();
	private final List<NovaGuild> allyInvitations = new NonNullArrayList<>();
	private final List<NovaGuild> war = new NonNullArrayList<>();
	private final List<NovaGuild> noWarInvited = new NonNullArrayList<>();
	private final List<NovaPlayer> invitedPlayers = new ArrayList<>();
	private final List<NovaRank> ranks = new ArrayList<>();

	/**
	 * The constructor
	 *
	 * @param uuid the UUID
	 */
	public NovaGuildImpl(UUID uuid) {
		this(uuid, null);
	}

	/**
	 * The constructor
	 *
	 * @param uuid           the uuid
	 * @param loadingWrapper loading wrapper instance
	 */
	public NovaGuildImpl(UUID uuid, LoadingWrapper loadingWrapper) {
		super(uuid);

		if(loadingWrapper != null) {
			this.loadingWrapper = loadingWrapper;
		}
		else {
			this.loadingWrapper = new LoadingWrapperImpl<>(new UUIDToGuildConverterImpl());
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPoints() {
		return points;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public List<NovaRegion> getRegions() {
		return regions;
	}

	@Override
	public NovaRegion getRegion(int index) {
		if(regions.size() >= index) {
			return regions.get(index - 1);
		}

		return null;
	}

	@Override
	public Hologram getVaultHologram() {
		return vaultHologram;
	}

	@Override
	public List<NovaGuild> getAllies() {
		return allies;
	}

	@Override
	public List<NovaGuild> getAllyInvitations() {
		return allyInvitations;
	}

	@Override
	public List<NovaGuild> getWars() {
		return war;
	}

	@Override
	public List<NovaGuild> getNoWarInvitations() {
		return noWarInvited;
	}

	@Override
	public List<NovaPlayer> getPlayers() {
		return players;
	}

	@Override
	public List<NovaPlayer> getOnlineNovaPlayers() {
		final List<NovaPlayer> list = new ArrayList<>();

		for(NovaPlayer nPlayer : getPlayers()) {
			if(nPlayer.isOnline()) {
				list.add(nPlayer);
			}
		}

		return list;
	}

	@Override
	public List<Player> getOnlinePlayers() {
		final List<Player> list = new ArrayList<>();

		for(NovaPlayer nPlayer : getOnlineNovaPlayers()) {
			list.add(nPlayer.getPlayer());
		}

		return list;
	}

	@Override
	public List<NovaPlayer> getInvitedPlayers() {
		return invitedPlayers;
	}

	@Override
	public List<NovaRank> getRanks() {
		return ranks;
	}

	@Override
	public Location getVaultLocation() {
		return vaultLocation;
	}

	@Override
	public NovaPlayer getLeader() {
		return leader;
	}

	/**
	 * Gets leader name as string
	 *
	 * @return leader name
	 */
	public String getLeaderName() {
		return leaderName;
	}

	@Override
	public Location getHome() {
		return homeLocation;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public double getMoney() {
		return money;
	}

	@Override
	public NovaRaid getRaid() {
		return raid;
	}

	@Override
	public int getLives() {
		return lives;
	}

	@Override
	public long getTimeRest() {
		return timeRest;
	}

	@Override
	public long getLostLiveTime() {
		return lostLiveTime;
	}

	@Override
	public long getInactiveTime() {
		return inactiveTime;
	}

	@Override
	public long getTimeCreated() {
		return timeCreated;
	}

	@Override
	public boolean getFriendlyPvp() {
		return friendlyPvp;
	}

	@Override
	public int getSlots() {
		return slots;
	}

	@Override
	public BannerMeta getBannerMeta() {
		return bannerMeta;
	}

	@Override
	public NovaRank getDefaultRank() {
		for(NovaRank rank : getRanks()) {
			if(rank.isDefault()) {
				return rank;
			}
		}

		return RankManager.getDefaultRank();
	}

	@Override
	public NovaRank getCloneOfGenericRank(NovaRank genericRank) {
		if(genericRank != null && genericRank.isGeneric()) {
			for(NovaRank rank : getRanks()) {
				if(rank.isClone() && genericRank.getName().equals(rank.getName())) {
					return rank;
				}
			}
		}

		return null;
	}

	@Override
	public void setVaultHologram(Hologram hologram) {
		vaultHologram = hologram;
	}

	@Override
	public void setName(String n) {
		name = n;
		setChanged();
	}

	@Override
	public void setTag(String t) {
		tag = t;
		setChanged();
	}

	@Override
	public void setRegions(List<NovaRegion> list) {
		for(NovaRegion region : getRegions()) {
			removeRegion(region);
		}

		for(NovaRegion region : list) {
			addRegion(region);
		}
	}

	@Override
	public void addRegion(NovaRegion region) {
		Validate.notNull(region);
		regions.add(region);
		region.setIndex(regions.size());
		region.setGuild(this);
	}

	@Override
	public void removeRegion(NovaRegion region) {
		Validate.notNull(region);
		regions.remove(region);
		region.setIndex(0);
	}

	@Override
	public void setLeaderName(String name) {
		leaderName = name;
	}

	@Override
	public void setVaultLocation(Location location) {
		vaultLocation = location;
		setChanged();
	}

	@Override
	public void setLeader(NovaPlayer nPlayer) {
		if(leader != null) {
			leader.setGuildRank(getDefaultRank());
		}

		leader = nPlayer;
		leader.setGuildRank(RankManager.getLeaderRank());
		setChanged();
	}

	@Override
	public void setHome(Location location) {
		homeLocation = location;
		setChanged();
	}

	@Override
	public void setId(int i) {
		id = i;
		setChanged();
	}

	@Override
	public void setMoney(double m) {
		money = m;
		setChanged();
	}

	@Override
	public void setAllies(Collection<NovaGuild> list) {
		allies.clear();
		allies.addAll(list);

		setChanged();
	}

	@Override
	public void setAllyInvitations(Collection<NovaGuild> list) {
		allyInvitations.clear();
		allyInvitations.addAll(list);

		setChanged();
	}

	@Override
	public void setWars(Collection<NovaGuild> list) {
		war.clear();
		war.addAll(list);

		setChanged();
	}

	@Override
	public void setNoWarInvitations(Collection<NovaGuild> list) {
		noWarInvited.clear();
		noWarInvited.addAll(list);

		setChanged();
	}

	@Override
	public void setPoints(int points) {
		this.points = points;
		setChanged();
	}

	@Override
	public void setOpenInvitation(boolean openInvitation) {
		this.openInvitation = openInvitation;
		setChanged();
	}

	@Override
	public void updateTimeRest() {
		timeRest = NumberUtils.systemSeconds();
		setChanged();
	}

	@Override
	public void updateLostLive() {
		lostLiveTime = NumberUtils.systemSeconds();
		setChanged();
	}

	@Override
	public void updateInactiveTime() {
		inactiveTime = NumberUtils.systemSeconds();
		setChanged();
	}

	@Override
	public void setLostLiveTime(long time) {
		lostLiveTime = time;
		setChanged();
	}

	@Override
	public void setInactiveTime(long time) {
		inactiveTime = time;
		setChanged();
	}

	@Override
	public void resetLostLiveTime() {
		lostLiveTime = 0;
		setChanged();
	}

	@Override
	public void setLives(int lives) {
		this.lives = lives;
		setChanged();
	}

	@Override
	public void setTimeRest(long time) {
		this.timeRest = time;
		setChanged();
	}

	@Override
	public void setFriendlyPvp(boolean pvp) {
		friendlyPvp = pvp;
		setChanged();
	}

	@Override
	public void setTimeCreated(long time) {
		this.timeCreated = time;
		setChanged();
	}

	@Override
	public void setSlots(int slots) {
		this.slots = slots;
		setChanged();
	}

	@Override
	public void setBannerMeta(BannerMeta bannerMeta) {
		this.bannerMeta = bannerMeta;
		setChanged();
	}

	@Override
	public void setRanks(List<NovaRank> ranks) {
		this.ranks.clear();
		this.ranks.addAll(ranks);

		setChanged();
	}

	@Override
	public boolean isInvitedToAlly(NovaGuild guild) {
		return allyInvitations.contains(guild);
	}

	@Override
	public boolean isWarWith(NovaGuild guild) {
		return war.contains(guild);
	}

	@Override
	public boolean isNoWarInvited(NovaGuild guild) {
		return noWarInvited.contains(guild);
	}

	@Override
	public boolean isLeader(NovaPlayer nPlayer) {
		return nPlayer.equals(leader) || (leaderName != null && nPlayer.getName().equals(leaderName));
	}

	@Override
	public boolean hasRegion() {
		return !regions.isEmpty();
	}

	@Override
	public boolean ownsRegion(NovaRegion region) {
		return regions.contains(region);
	}

	@Override
	public boolean isAlly(NovaGuild guild) {
		return guild != null && allies.contains(guild);
	}

	@Override
	public boolean isRaid() {
		return raid != null && raid.getResult() == NovaRaid.Result.DURING;
	}

	@Override
	public boolean isFull() {
		return getPlayers().size() >= slots;
	}

	@Override
	public boolean isOpenInvitation() {
		return openInvitation;
	}

	@Override
	public boolean hasMoney(double money) {
		return this.money >= money;
	}

	@Override
	public void addAlly(NovaGuild guild) {
		if(guild != null && !isAlly(guild)) {
			allies.add(guild);
			setChanged();
		}
	}

	@Override
	public void addAllyInvitation(NovaGuild guild) {
		if(guild != null && !isInvitedToAlly(guild)) {
			allyInvitations.add(guild);
			setChanged();
		}
	}

	@Override
	public void addWar(NovaGuild guild) {
		if(guild != null && !isWarWith(guild)) {
			war.add(guild);
			setChanged();
		}
	}

	@Override
	public void addNoWarInvitation(NovaGuild guild) {
		if(guild != null && !isNoWarInvited(guild)) {
			noWarInvited.add(guild);
			setChanged();
		}
	}

	@Override
	public void addPlayer(NovaPlayer nPlayer) {
		if(nPlayer == null) {
			LoggerUtils.info("Tried to add null player to a guild! " + name);
			return;
		}

		if(!players.contains(nPlayer)) {
			players.add(nPlayer);
			nPlayer.setGuild(this);

			if(getLeaderName() != null) {
				try {
					UUID leaderUUID = UUID.fromString(getLeaderName());

					if(leaderUUID.equals(nPlayer.getUUID())) {
						setLeader(nPlayer);
						leaderName = null;
					}
				}
				catch(IllegalArgumentException e) {
					if(getLeaderName().equalsIgnoreCase(nPlayer.getName())) {
						setLeader(nPlayer);
						leaderName = null;
					}
				}
			}

			if(NovaGuilds.getInstance().getRankManager().isLoaded() && !nPlayer.isLeader()) {
				nPlayer.setGuildRank(getDefaultRank());
			}
		}
	}

	@Override
	public void addMoney(double money) {
		if(money == 0) {
			return;
		}

		if(money < 0) {
			throw new IllegalArgumentException("Cannot add negative amount of money");
		}

		this.money += money;
		setChanged();
	}

	@Override
	public void addPoints(int points) {
		if(points == 0) {
			return;
		}

		if(points < 0) {
			throw new IllegalArgumentException("Cannot add negative amount of points");
		}

		this.points += points;
		setChanged();
	}

	@Override
	public void addSlot() {
		slots++;
	}

	@Override
	public void addRank(NovaRank rank) {
		if(!ranks.contains(rank)) {
			ranks.add(rank);
			if(rank.getGuild() == null || !rank.getGuild().equals(this)) {
				rank.setGuild(this);
			}
		}
	}

	@Override
	public void removePlayer(NovaPlayer nPlayer) {
		if(players.contains(nPlayer)) {
			players.remove(nPlayer);
			nPlayer.setGuild(null);
			nPlayer.setGuildRank(null);
		}
	}

	@Override
	public void removeAlly(NovaGuild guild) {
		if(allies.contains(guild)) {
			allies.remove(guild);
			setChanged();
		}
	}

	@Override
	public void removeWar(NovaGuild guild) {
		if(war.contains(guild)) {
			war.remove(guild);
			setChanged();
		}
	}

	@Override
	public void removeNoWarInvitation(NovaGuild guild) {
		if(noWarInvited.contains(guild)) {
			noWarInvited.remove(guild);
			setChanged();
		}
	}

	@Override
	public void removeRank(NovaRank rank) {
		ranks.remove(rank);
		rank.setGuild(null);
	}

	@Override
	public void takeMoney(double money) {
		if(money == 0) {
			return;
		}

		if(money < 0) {
			throw new IllegalArgumentException("Cannot take negative amount of money");
		}

		this.money -= money;
		setChanged();
	}

	@Override
	public void takeLive() {
		lives--;
		setChanged();
	}

	@Override
	public void addLive() {
		lives++;
		setChanged();
	}

	@Override
	public void takePoints(int points) {
		if(points == 0) {
			return;
		}

		if(points < 0) {
			throw new IllegalArgumentException("Cannot add negative amount of points");
		}

		this.points -= points;
		setChanged();
	}

	@Override
	public void removeAllyInvitation(NovaGuild guild) {
		if(isInvitedToAlly(guild)) {
			allyInvitations.remove(guild);
			setChanged();
		}
	}

	@Override
	public void createRaid(NovaGuild attacker) {
		raid = new NovaRaidImpl(attacker, this);
	}

	@Override
	public boolean isMember(NovaPlayer nPlayer) {
		return players.contains(nPlayer);
	}

	@Override
	public void destroy() {
		destroy(AbandonCause.PLAYER);
	}

	@Override
	public void destroy(AbandonCause cause) {
		final NovaGuilds plugin = NovaGuilds.getInstance();

		//remove players
		for(NovaPlayer nPlayer : getPlayers()) {
			nPlayer.cancelToolProgress();
			nPlayer.setGuild(null);
			nPlayer.setGuildRank(null);

			if(nPlayer.isOnline()) {
				//Close GUI
				if(nPlayer.getGuiInventory() != null) {
					nPlayer.getGuiInventoryHistory().clear();
					nPlayer.getPlayer().closeInventory();
				}
			}
		}

		for(NovaPlayer nPlayer : plugin.getPlayerManager().getPlayers()) {
			//remove guild invitations
			if(nPlayer.isInvitedTo(this)) {
				nPlayer.deleteInvitation(this);
			}

			//remove from raid
			if(nPlayer.isPartRaid()) {
				nPlayer.getPartRaid().removePlayerOccupying(nPlayer);
			}

			if(nPlayer.isOnline() && nPlayer.isAtRegion()) {
				NovaRegion atRegion = nPlayer.getAtRegion();

				//Exit from region
				if(atRegion.getGuild().equals(this)) {
					plugin.getRegionManager().playerExitedRegion(nPlayer.getPlayer());
				}
			}
		}

		//remove allies and wars
		for(NovaGuild nGuild : plugin.getGuildManager().getGuilds()) {
			//ally
			if(nGuild.isAlly(this)) {
				nGuild.removeAlly(this);
			}

			//ally invitation
			if(nGuild.isInvitedToAlly(this)) {
				nGuild.removeAllyInvitation(this);
			}

			//war
			if(nGuild.isWarWith(this)) {
				nGuild.removeWar(this);
			}

			//no war invitation
			if(nGuild.isNoWarInvited(this)) {
				nGuild.removeNoWarInvitation(this);
			}
		}

		//Remove from dynmap
		plugin.getDynmapManager().removeGuild(this);

		if(isRaid()) {
			getRaid().setResult(NovaRaid.Result.DESTROYED);
		}

		//bank and hologram
		if(this.getVaultHologram() != null) {
			this.getVaultHologram().delete();
		}

		GuildManager.checkVaultDestroyed(this);
		if(cause != AbandonCause.UNLOADED && getVaultLocation() != null) {
			getVaultLocation().getBlock().breakNaturally();
			getVaultLocation().getWorld().playEffect(getVaultLocation(), Effect.SMOKE, 1000);
		}

		if(getLeader() != null) {
			if(getLeader().isOnline() && getLeader().getPlayer().getGameMode() != GameMode.CREATIVE) {
				while(InventoryUtils.containsAtLeast(getLeader().getPlayer().getInventory(), Config.VAULT_ITEM.getItemStack(), 1)) {
					getLeader().getPlayer().getInventory().removeItem(Config.VAULT_ITEM.getItemStack());
				}
			}

			//Give all the money to the leader
			getLeader().addMoney(getMoney());
		}

		//Delete ranks
		plugin.getRankManager().delete(this);

		//Delete region
		for(NovaRegion region : new ArrayList<>(getRegions())) {
			RegionDeleteEvent event = new RegionDeleteEvent(region, RegionDeleteEvent.Cause.fromGuildAbandonCause(cause));
			ListenerManager.getLoggedPluginManager().callEvent(event);

			if(!event.isCancelled()) {
				plugin.getRegionManager().remove(region);
			}
		}

		//Refresh top holograms
		plugin.getHologramManager().refreshTopHolograms();

		//Update tab
		TabUtils.refresh(this);
		TagUtils.refresh(this);
	}

	@Override
	public void showVaultHologram(Player player) {
		if(vaultHologram != null) {
			vaultHologram.getVisibilityManager().showTo(player);
		}
	}

	@Override
	public void hideVaultHologram(Player player) {
		if(vaultHologram != null) {
			vaultHologram.getVisibilityManager().hideTo(player);
		}
	}

	@Override
	public void removeRaidBar() {
		if(Config.BOSSBAR_ENABLED.getBoolean()) {
			for(Player player : getOnlinePlayers()) {
				BossBarUtils.removeBar(player);
			}
		}
	}

	@Override
	public void postSetUp() {
		setAllies(loadingWrapper.convert(loadingWrapper.getAllies()));
		setAllyInvitations(loadingWrapper.convert(loadingWrapper.getAllyInvitations()));
		setNoWarInvitations(loadingWrapper.convert(loadingWrapper.getNoWarInvitations()));
		setWars(loadingWrapper.convert(loadingWrapper.getWars()));

		setUnchanged();

		if(loadingWrapper.getAllies().size() != getAllies().size()
				|| loadingWrapper.getAllyInvitations().size() != getAllyInvitations().size()
				|| loadingWrapper.getNoWarInvitations().size() != getNoWarInvitations().size()
				|| loadingWrapper.getWars().size() != getWars().size()) {
			setChanged();
		}
	}

	@Override
	public LoadingWrapper getLoadingWrapper() {
		return loadingWrapper;
	}

	public static class LoadingWrapperImpl<T> implements LoadingWrapper<T> {
		protected final List<T> allies = new ArrayList<>();
		protected final List<T> allyInvitations = new ArrayList<>();
		protected final List<T> wars = new ArrayList<>();
		protected final List<T> noWarInvitations = new ArrayList<>();
		protected final IConverter<T, NovaGuild> converter;

		/**
		 * The constructor
		 *
		 * @param converter the converter
		 */
		public LoadingWrapperImpl(IConverter<T, NovaGuild> converter) {
			this.converter = converter;
		}

		@Override
		public Collection<T> getAllies() {
			return allies;
		}

		@Override
		public Collection<T> getAllyInvitations() {
			return allyInvitations;
		}

		@Override
		public Collection<T> getWars() {
			return wars;
		}

		@Override
		public Collection<T> getNoWarInvitations() {
			return noWarInvitations;
		}

		@Override
		public Collection<NovaGuild> convert(Collection<T> list) {
			return converter.convert(list);
		}

		@Override
		public void setAllies(Collection<T> list) {
			allies.clear();
			allies.addAll(list);
		}

		@Override
		public void setAllyInvitations(Collection<T> list) {
			allyInvitations.clear();
			allyInvitations.addAll(list);
		}

		@Override
		public void setWars(Collection<T> list) {
			wars.clear();
			wars.addAll(list);
		}

		@Override
		public void setNoWarInvitations(Collection<T> list) {
			noWarInvitations.clear();
			noWarInvitations.addAll(list);
		}
	}

	public static class LoadingWrapper37MigrationImpl extends LoadingWrapperImpl<String> {
		public LoadingWrapper37MigrationImpl() {
			super(new NameToGuildConverterImpl());
		}
	}
}
