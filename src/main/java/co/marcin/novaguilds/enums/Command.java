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

package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.CommandExecutor;
import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.command.CommandConfirm;
import co.marcin.novaguilds.command.CommandNovaGuilds;
import co.marcin.novaguilds.command.CommandPlayerInfo;
import co.marcin.novaguilds.command.CommandToolGet;
import co.marcin.novaguilds.command.admin.CommandAdmin;
import co.marcin.novaguilds.command.admin.CommandAdminChatSpy;
import co.marcin.novaguilds.command.admin.CommandAdminMigrate;
import co.marcin.novaguilds.command.admin.CommandAdminReload;
import co.marcin.novaguilds.command.admin.CommandAdminSave;
import co.marcin.novaguilds.command.admin.config.CommandAdminConfig;
import co.marcin.novaguilds.command.admin.config.CommandAdminConfigGet;
import co.marcin.novaguilds.command.admin.config.CommandAdminConfigReload;
import co.marcin.novaguilds.command.admin.config.CommandAdminConfigReset;
import co.marcin.novaguilds.command.admin.config.CommandAdminConfigSave;
import co.marcin.novaguilds.command.admin.config.CommandAdminConfigSet;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuild;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildAbandon;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildBankPay;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildBankWithdraw;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildInactive;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildInvite;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildKick;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildList;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildPurge;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildResetPoints;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetLeader;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetLiveRegenerationTime;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetLives;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetName;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetPoints;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetSlots;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetTag;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetTimerest;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildTeleport;
import co.marcin.novaguilds.command.admin.guild.rank.CommandAdminGuildRank;
import co.marcin.novaguilds.command.admin.guild.rank.CommandAdminGuildRankDelete;
import co.marcin.novaguilds.command.admin.guild.rank.CommandAdminGuildRankList;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologram;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramAddTop;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramDelete;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramList;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramTeleport;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramTeleportHere;
import co.marcin.novaguilds.command.admin.player.CommandAdminPlayer;
import co.marcin.novaguilds.command.admin.player.CommandAdminPlayerSetPoints;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegion;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionBuy;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionBypass;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionDelete;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionList;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionSpectate;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionTeleport;
import co.marcin.novaguilds.command.guild.CommandGuild;
import co.marcin.novaguilds.command.guild.CommandGuildAbandon;
import co.marcin.novaguilds.command.guild.CommandGuildAlly;
import co.marcin.novaguilds.command.guild.CommandGuildBankPay;
import co.marcin.novaguilds.command.guild.CommandGuildBankWithdraw;
import co.marcin.novaguilds.command.guild.CommandGuildBuyLife;
import co.marcin.novaguilds.command.guild.CommandGuildBuySlot;
import co.marcin.novaguilds.command.guild.CommandGuildChatMode;
import co.marcin.novaguilds.command.guild.CommandGuildCompass;
import co.marcin.novaguilds.command.guild.CommandGuildCreate;
import co.marcin.novaguilds.command.guild.CommandGuildEffect;
import co.marcin.novaguilds.command.guild.CommandGuildHome;
import co.marcin.novaguilds.command.guild.CommandGuildInfo;
import co.marcin.novaguilds.command.guild.CommandGuildInvite;
import co.marcin.novaguilds.command.guild.CommandGuildJoin;
import co.marcin.novaguilds.command.guild.CommandGuildKick;
import co.marcin.novaguilds.command.guild.CommandGuildLeader;
import co.marcin.novaguilds.command.guild.CommandGuildLeave;
import co.marcin.novaguilds.command.guild.CommandGuildMenu;
import co.marcin.novaguilds.command.guild.CommandGuildOpenInvitation;
import co.marcin.novaguilds.command.guild.CommandGuildPvpToggle;
import co.marcin.novaguilds.command.guild.CommandGuildRequiredItems;
import co.marcin.novaguilds.command.guild.CommandGuildSetName;
import co.marcin.novaguilds.command.guild.CommandGuildSetTag;
import co.marcin.novaguilds.command.guild.CommandGuildTop;
import co.marcin.novaguilds.command.guild.CommandGuildVaultRestore;
import co.marcin.novaguilds.command.guild.CommandGuildWar;
import co.marcin.novaguilds.command.guild.rank.CommandGuildRank;
import co.marcin.novaguilds.command.guild.rank.CommandGuildRankDelete;
import co.marcin.novaguilds.command.guild.rank.CommandGuildRankList;
import co.marcin.novaguilds.command.region.CommandRegion;
import co.marcin.novaguilds.command.region.CommandRegionBuy;
import co.marcin.novaguilds.command.region.CommandRegionDelete;
import co.marcin.novaguilds.impl.basic.CommandWrapperImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Command extends CommandWrapperImpl {
	public static final CommandWrapper ADMIN_ACCESS =                         new Command(CommandAdmin.class,                               Permission.NOVAGUILDS_ADMIN_ACCESS,                        "nga",        Message.CHAT_USAGE_NGA_ACCESS                                                     );
	public static final CommandWrapper ADMIN_RELOAD =                         new Command(CommandAdminReload.class,                         Permission.NOVAGUILDS_ADMIN_RELOAD,                                      Message.CHAT_USAGE_NGA_RELOAD                                                     );
	public static final CommandWrapper ADMIN_SAVE =                           new Command(CommandAdminSave.class,                           Permission.NOVAGUILDS_ADMIN_SAVE,                                        Message.CHAT_USAGE_NGA_SAVE                                                       );
	public static final CommandWrapper ADMIN_CHATSPY =                        new Command(CommandAdminChatSpy.class,                        Permission.NOVAGUILDS_ADMIN_CHATSPY_SELF,                                Message.CHAT_USAGE_NGA_CHATSPY,                                     Flag.NOCONSOLE);
	public static final CommandWrapper ADMIN_MIGRATE =                        new Command(CommandAdminMigrate.class,                        Permission.NOVAGUILDS_ADMIN_MIGRATE,                                     Message.CHAT_USAGE_NGA_MIGRATE                                                    );
	public static final CommandWrapper ADMIN_CONFIG_ACCESS =                  new Command(CommandAdminConfig.class,                         Permission.NOVAGUILDS_ADMIN_CONFIG_ACCESS,                               Message.CHAT_USAGE_NGA_CONFIG_ACCESS                                              );
	public static final CommandWrapper ADMIN_CONFIG_GET =                     new Command(CommandAdminConfigGet.class,                      Permission.NOVAGUILDS_ADMIN_CONFIG_GET,                                  Message.CHAT_USAGE_NGA_CONFIG_GET                                                 );
	public static final CommandWrapper ADMIN_CONFIG_RELOAD =                  new Command(CommandAdminConfigReload.class,                   Permission.NOVAGUILDS_ADMIN_CONFIG_RELOAD,                               Message.CHAT_USAGE_NGA_CONFIG_RELOAD                                              );
	public static final CommandWrapper ADMIN_CONFIG_RESET =                   new Command(CommandAdminConfigReset.class,                    Permission.NOVAGUILDS_ADMIN_CONFIG_RESET,                                Message.CHAT_USAGE_NGA_CONFIG_RESET,                  Flag.CONFIRM                );
	public static final CommandWrapper ADMIN_CONFIG_SAVE =                    new Command(CommandAdminConfigSave.class,                     Permission.NOVAGUILDS_ADMIN_CONFIG_SAVE,                                 Message.CHAT_USAGE_NGA_CONFIG_SAVE,                   Flag.CONFIRM                );
	public static final CommandWrapper ADMIN_CONFIG_SET =                     new Command(CommandAdminConfigSet.class,                      Permission.NOVAGUILDS_ADMIN_CONFIG_SET,                                  Message.CHAT_USAGE_NGA_CONFIG_SET                                                 );
	public static final CommandWrapper ADMIN_PLAYER_ACCESS =                  new Command(CommandAdminPlayer.class,                         Permission.NOVAGUILDS_ADMIN_PLAYER_ACCESS,                               Message.CHAT_USAGE_NGA_PLAYER_ACCESS                                              );
	public static final CommandWrapper ADMIN_PLAYER_SET_POINTS =              new Command(CommandAdminPlayerSetPoints.class,                Permission.NOVAGUILDS_ADMIN_PLAYER_SET_POINTS,                           Message.CHAT_USAGE_NGA_PLAYER_SET_POINTS                                          );
	public static final CommandWrapper ADMIN_GUILD_ACCESS =                   new Command(CommandAdminGuild.class,                          Permission.NOVAGUILDS_ADMIN_GUILD_ACCESS,                                Message.CHAT_USAGE_NGA_GUILD_ACCESS                                               );
	public static final CommandWrapper ADMIN_GUILD_ABANDON =                  new Command(CommandAdminGuildAbandon.class,                   Permission.NOVAGUILDS_ADMIN_GUILD_ABANDON,                               Message.CHAT_USAGE_NGA_GUILD_ABANDON,                 Flag.CONFIRM                );
	public static final CommandWrapper ADMIN_GUILD_PURGE =                    new Command(CommandAdminGuildPurge.class,                     Permission.NOVAGUILDS_ADMIN_GUILD_PURGE,                                 Message.CHAT_USAGE_NGA_GUILD_PURGE,                   Flag.CONFIRM                );
	public static final CommandWrapper ADMIN_GUILD_BANK_PAY =                 new Command(CommandAdminGuildBankPay.class,                   Permission.NOVAGUILDS_ADMIN_GUILD_BANK_PAY,                              Message.CHAT_USAGE_NGA_GUILD_BANK_PAY                                             );
	public static final CommandWrapper ADMIN_GUILD_BANK_WITHDRAW =            new Command(CommandAdminGuildBankWithdraw.class,              Permission.NOVAGUILDS_ADMIN_GUILD_BANK_WITHDRAW,                         Message.CHAT_USAGE_NGA_GUILD_BANK_WITHDRAW                                        );
	public static final CommandWrapper ADMIN_GUILD_INACTIVE =                 new Command(CommandAdminGuildInactive.class,                  Permission.NOVAGUILDS_ADMIN_GUILD_INACTIVE_LIST,                         Message.CHAT_USAGE_NGA_GUILD_INACTIVE_LIST                                        );
	public static final CommandWrapper ADMIN_GUILD_INVITE =                   new Command(CommandAdminGuildInvite.class,                    Permission.NOVAGUILDS_ADMIN_GUILD_INVITE,                                Message.CHAT_USAGE_NGA_GUILD_INVITE                                               );
	public static final CommandWrapper ADMIN_GUILD_KICK =                     new Command(CommandAdminGuildKick.class,                      Permission.NOVAGUILDS_ADMIN_GUILD_KICK,                                  Message.CHAT_USAGE_NGA_GUILD_KICK                                                 );
	public static final CommandWrapper ADMIN_GUILD_LIST =                     new Command(CommandAdminGuildList.class,                      Permission.NOVAGUILDS_ADMIN_GUILD_LIST,                                  Message.CHAT_USAGE_NGA_GUILD_LIST                                                 );
	public static final CommandWrapper ADMIN_GUILD_SET_LEADER =               new Command(CommandAdminGuildSetLeader.class,                 Permission.NOVAGUILDS_ADMIN_GUILD_SET_LEADER,                            Message.CHAT_USAGE_NGA_GUILD_SET_LEADER                                           );
	public static final CommandWrapper ADMIN_GUILD_SET_LIVEREGENERATIONTIME = new Command(CommandAdminGuildSetLiveRegenerationTime.class,   Permission.NOVAGUILDS_ADMIN_GUILD_SET_LIVEREGENERATIONTIME,              Message.CHAT_USAGE_NGA_GUILD_SET_LIVEREGENERATIONTIME                             );
	public static final CommandWrapper ADMIN_GUILD_SET_LIVES =                new Command(CommandAdminGuildSetLives.class,                  Permission.NOVAGUILDS_ADMIN_GUILD_SET_LIVES,                             Message.CHAT_USAGE_NGA_GUILD_SET_LIVES                                            );
	public static final CommandWrapper ADMIN_GUILD_SET_NAME =                 new Command(CommandAdminGuildSetName.class,                   Permission.NOVAGUILDS_ADMIN_GUILD_SET_NAME,                              Message.CHAT_USAGE_NGA_GUILD_SET_NAME                                             );
	public static final CommandWrapper ADMIN_GUILD_SET_POINTS =               new Command(CommandAdminGuildSetPoints.class,                 Permission.NOVAGUILDS_ADMIN_GUILD_SET_POINTS,                            Message.CHAT_USAGE_NGA_GUILD_SET_POINTS                                           );
	public static final CommandWrapper ADMIN_GUILD_SET_TAG =                  new Command(CommandAdminGuildSetTag.class,                    Permission.NOVAGUILDS_ADMIN_GUILD_SET_TAG,                               Message.CHAT_USAGE_NGA_GUILD_SET_TAG                                              );
	public static final CommandWrapper ADMIN_GUILD_SET_TIMEREST =             new Command(CommandAdminGuildSetTimerest.class,               Permission.NOVAGUILDS_ADMIN_GUILD_SET_TIMEREST,                          Message.CHAT_USAGE_NGA_GUILD_SET_TIMEREST                                         );
	public static final CommandWrapper ADMIN_GUILD_SET_SLOTS =                new Command(CommandAdminGuildSetSlots.class,                  Permission.NOVAGUILDS_ADMIN_GUILD_SET_SLOTS,                             Message.CHAT_USAGE_NGA_GUILD_SET_SLOTS                                            );
	public static final CommandWrapper ADMIN_GUILD_TELEPORT =                 new Command(CommandAdminGuildTeleport.class,                  Permission.NOVAGUILDS_ADMIN_GUILD_TELEPORT_SELF,                         Message.CHAT_USAGE_NGA_GUILD_TP                                                   );
	public static final CommandWrapper ADMIN_GUILD_RESET_POINTS =             new Command(CommandAdminGuildResetPoints.class,               Permission.NOVAGUILDS_ADMIN_GUILD_RESET_POINTS,                          Message.CHAT_USAGE_NGA_GUILD_RESET_POINTS,            Flag.CONFIRM                );
	public static final CommandWrapper ADMIN_REGION_ACCESS =                  new Command(CommandAdminRegion.class,                         Permission.NOVAGUILDS_ADMIN_REGION_ACCESS,                               Message.CHAT_USAGE_NGA_REGION_ACCESS                                              );
	public static final CommandWrapper ADMIN_REGION_BYPASS =                  new Command(CommandAdminRegionBypass.class,                   Permission.NOVAGUILDS_ADMIN_REGION_BYPASS_SELF,                          Message.CHAT_USAGE_NGA_REGION_BYPASS                                              );
	public static final CommandWrapper ADMIN_REGION_DELETE =                  new Command(CommandAdminRegionDelete.class,                   Permission.NOVAGUILDS_ADMIN_REGION_DELETE,                               Message.CHAT_USAGE_NGA_REGION_DELETE,                 Flag.CONFIRM                );
	public static final CommandWrapper ADMIN_REGION_LIST =                    new Command(CommandAdminRegionList.class,                     Permission.NOVAGUILDS_ADMIN_REGION_LIST,                                 Message.CHAT_USAGE_NGA_REGION_LIST                                                );
	public static final CommandWrapper ADMIN_REGION_SPECTATE =                new Command(CommandAdminRegionSpectate.class,                 Permission.NOVAGUILDS_ADMIN_REGION_CHANGE_SPECTATE_SELF,                 Message.CHAT_USAGE_NGA_REGION_SPECTATE                                            );
	public static final CommandWrapper ADMIN_REGION_TELEPORT =                new Command(CommandAdminRegionTeleport.class,                 Permission.NOVAGUILDS_ADMIN_REGION_TELEPORT_SELF,                        Message.CHAT_USAGE_NGA_REGION_TELEPORT                                            );
	public static final CommandWrapper ADMIN_REGION_BUY =                     new Command(CommandAdminRegionBuy.class,                      Permission.NOVAGUILDS_ADMIN_REGION_BUY,                                  Message.CHAT_USAGE_NGA_REGION_BUY,                    Flag.NOCONSOLE              );
	public static final CommandWrapper ADMIN_GUILD_RANK_ACCESS =              new Command(CommandAdminGuildRank.class,                      Permission.NOVAGUILDS_ADMIN_GUILD_RANK_ACCESS,                           Message.CHAT_USAGE_NGA_GUILD_RANK_ACCESS                                          );
	public static final CommandWrapper ADMIN_GUILD_RANK_DELETE =              new Command(CommandAdminGuildRankDelete.class,                Permission.NOVAGUILDS_ADMIN_GUILD_RANK_DELETE,                           Message.CHAT_USAGE_NGA_GUILD_RANK_DELETE                                          );
	public static final CommandWrapper ADMIN_GUILD_RANK_LIST =                new Command(CommandAdminGuildRankList.class,                  Permission.NOVAGUILDS_ADMIN_GUILD_RANK_LIST,                             Message.CHAT_USAGE_NGA_GUILD_RANK_LIST                                            );
	public static final CommandWrapper ADMIN_HOLOGRAM_ACCESS =                new Command(CommandAdminHologram.class,                       Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ACCESS,                             Message.CHAT_USAGE_NGA_HOLOGRAM_ACCESS                                            );
	public static final CommandWrapper ADMIN_HOLOGRAM_LIST =                  new Command(CommandAdminHologramList.class,                   Permission.NOVAGUILDS_ADMIN_HOLOGRAM_LIST,                               Message.CHAT_USAGE_NGA_HOLOGRAM_LIST                                              );
	public static final CommandWrapper ADMIN_HOLOGRAM_TELEPORT =              new Command(CommandAdminHologramTeleport.class,               Permission.NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT,                           Message.CHAT_USAGE_NGA_HOLOGRAM_TELEPORT_SELF,        Flag.NOCONSOLE              );
	public static final CommandWrapper ADMIN_HOLOGRAM_DELETE =                new Command(CommandAdminHologramDelete.class,                 Permission.NOVAGUILDS_ADMIN_HOLOGRAM_DELETE,                             Message.CHAT_USAGE_NGA_HOLOGRAM_DELETE                                            );
	public static final CommandWrapper ADMIN_HOLOGRAM_ADDTOP =                new Command(CommandAdminHologramAddTop.class,                 Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ADDTOP,                             Message.CHAT_USAGE_NGA_HOLOGRAM_ADDTOP,               Flag.NOCONSOLE              );
//	public static final CommandWrapper ADMIN_HOLOGRAM_ADD =                   new Command(null,                                             Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ADD,                                Message.CHAT_USAGE_NGA_ADD,                           Flag.NOCONSOLE              );
	public static final CommandWrapper ADMIN_HOLOGRAM_TELEPORT_HERE =         new Command(CommandAdminHologramTeleportHere.class,           Permission.NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT_HERE,                      Message.CHAT_USAGE_NGA_HOLOGRAM_TELEPORT_HERE,        Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_ACCESS =                         new Command(CommandGuild.class,                               Permission.NOVAGUILDS_GUILD_ACCESS,                        "guild",      Message.CHAT_USAGE_GUILD_ACCESS                                                   );
	public static final CommandWrapper GUILD_ABANDON =                        new Command(CommandGuildAbandon.class,                        Permission.NOVAGUILDS_GUILD_ABANDON,                       "abandon",    Message.CHAT_USAGE_GUILD_ABANDON,                     Flag.NOCONSOLE, Flag.CONFIRM);
	public static final CommandWrapper GUILD_ALLY =                           new Command(CommandGuildAlly.class,                           Permission.NOVAGUILDS_GUILD_ALLY,                                        Message.CHAT_USAGE_GUILD_ALLY,                        Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_BANK_PAY =                       new Command(CommandGuildBankPay.class,                        Permission.NOVAGUILDS_GUILD_BANK_PAY,                                    Message.CHAT_USAGE_GUILD_BANK_PAY,                    Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_BANK_WITHDRAW =                  new Command(CommandGuildBankWithdraw.class,                   Permission.NOVAGUILDS_GUILD_BANK_WITHDRAW,                               Message.CHAT_USAGE_GUILD_BANK_WITHDRAW,               Flag.NOCONSOLE              );
//	public static final CommandWrapper GUILD_BOSS =                           new Command(null,                                             Permission.NOVAGUILDS_GUILD_BOSS,                                        Message.CHAT_USAGE_GUILD_BOSS,                        Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_BUYLIFE =                        new Command(CommandGuildBuyLife.class,                        Permission.NOVAGUILDS_GUILD_BUYLIFE,                                     Message.CHAT_USAGE_GUILD_BUY_LIFE,                    Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_BUYSLOT =                        new Command(CommandGuildBuySlot.class,                        Permission.NOVAGUILDS_GUILD_BUYSLOT,                                     Message.CHAT_USAGE_GUILD_BUY_SLOT,                    Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_CHATMODE =                       new Command(CommandGuildChatMode.class,                       Permission.NOVAGUILDS_GUILD_CHATMODE,                                    Message.CHAT_USAGE_GUILD_CHATMODE,                    Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_COMPASS =                        new Command(CommandGuildCompass.class,                        Permission.NOVAGUILDS_GUILD_COMPASS,                                     Message.CHAT_USAGE_GUILD_COMPASS,                     Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_CREATE =                         new Command(CommandGuildCreate.class,                         Permission.NOVAGUILDS_GUILD_CREATE,                        "create",     Message.CHAT_USAGE_GUILD_CREATE,                      Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_EFFECT =                         new Command(CommandGuildEffect.class,                         Permission.NOVAGUILDS_GUILD_EFFECT,                                      Message.CHAT_USAGE_GUILD_EFFECT,                      Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_HOME =                           new Command(CommandGuildHome.class,                           Permission.NOVAGUILDS_GUILD_HOME,                                        Message.CHAT_USAGE_GUILD_HOME_TELEPORT,               Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_INFO =                           new Command(CommandGuildInfo.class,                           Permission.NOVAGUILDS_GUILD_INFO,                          "gi",         Message.CHAT_USAGE_GUILD_INFO                                                     );
	public static final CommandWrapper GUILD_INVITE =                         new Command(CommandGuildInvite.class,                         Permission.NOVAGUILDS_GUILD_INVITE,                        "invite",     Message.CHAT_USAGE_GUILD_INVITE,                      Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_JOIN =                           new Command(CommandGuildJoin.class,                           Permission.NOVAGUILDS_GUILD_JOIN,                          "join",       Message.CHAT_USAGE_GUILD_JOIN,                        Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_KICK =                           new Command(CommandGuildKick.class,                           Permission.NOVAGUILDS_GUILD_KICK,                                        Message.CHAT_USAGE_GUILD_KICK,                        Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_LEADER =                         new Command(CommandGuildLeader.class,                         Permission.NOVAGUILDS_GUILD_LEADER,                                      Message.CHAT_USAGE_GUILD_LEADER,                      Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_LEAVE =                          new Command(CommandGuildLeave.class,                          Permission.NOVAGUILDS_GUILD_LEAVE,                         "leave",      Message.CHAT_USAGE_GUILD_LEAVE,                       Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_MENU =                           new Command(CommandGuildMenu.class,                           Permission.NOVAGUILDS_GUILD_MENU,                          "guildmenu",  Message.CHAT_USAGE_GUILD_MENU,                        Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_PVPTOGGLE =                      new Command(CommandGuildPvpToggle.class,                      Permission.NOVAGUILDS_GUILD_PVPTOGGLE,                                   Message.CHAT_USAGE_GUILD_PVPTOGGLE,                   Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_REQUIREDITEMS =                  new Command(CommandGuildRequiredItems.class,                  Permission.NOVAGUILDS_GUILD_REQUIREDITEMS,                               Message.CHAT_USAGE_GUILD_REQUIREDITEMS,               Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_TOP =                            new Command(CommandGuildTop.class,                            Permission.NOVAGUILDS_GUILD_TOP,                                         Message.CHAT_USAGE_GUILD_TOP                                                      );
	public static final CommandWrapper GUILD_VAULT_RESTORE =                  new Command(CommandGuildVaultRestore.class,                   Permission.NOVAGUILDS_GUILD_VAULT_RESTORE,                               Message.CHAT_USAGE_GUILD_VAULT_RESTORE,               Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_WAR =                            new Command(CommandGuildWar.class,                            Permission.NOVAGUILDS_GUILD_WAR,                                         Message.CHAT_USAGE_GUILD_WAR,                         Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_OPENINVITATION =                 new Command(CommandGuildOpenInvitation.class,                 Permission.NOVAGUILDS_GUILD_OPENINVITATION,                              Message.CHAT_USAGE_GUILD_OPENINVITATION,              Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_SET_NAME =                       new Command(CommandGuildSetName.class,                        Permission.NOVAGUILDS_GUILD_SET_NAME,                                    Message.CHAT_USAGE_GUILD_SET_NAME,                    Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_SET_TAG =                        new Command(CommandGuildSetTag.class,                         Permission.NOVAGUILDS_GUILD_SET_TAG,                                     Message.CHAT_USAGE_GUILD_SET_TAG,                     Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_RANK_ACCESS =                    new Command(CommandGuildRank.class,                           Permission.NOVAGUILDS_GUILD_RANK_ACCESS,                                 Message.CHAT_USAGE_GUILD_RANK_ACCESS,                 Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_RANK_DELETE =                    new Command(CommandGuildRankDelete.class,                     Permission.NOVAGUILDS_GUILD_RANK_DELETE,                                 Message.CHAT_USAGE_GUILD_RANK_DELETE,                 Flag.NOCONSOLE              );
	public static final CommandWrapper GUILD_RANK_LIST =                      new Command(CommandGuildRankList.class,                       Permission.NOVAGUILDS_GUILD_RANK_LIST,                                   Message.CHAT_USAGE_GUILD_RANK_LIST,                   Flag.NOCONSOLE              );
	public static final CommandWrapper REGION_ACCESS =                        new Command(CommandRegion.class,                              Permission.NOVAGUILDS_REGION_ACCESS,                                     Message.CHAT_USAGE_REGION_ACCESS,                     Flag.NOCONSOLE              );
	public static final CommandWrapper REGION_BUY =                           new Command(CommandRegionBuy.class,                           Permission.NOVAGUILDS_REGION_CREATE,                                     Message.CHAT_USAGE_REGION_BUY,                        Flag.NOCONSOLE              );
	public static final CommandWrapper REGION_DELETE =                        new Command(CommandRegionDelete.class,                        Permission.NOVAGUILDS_REGION_DELETE,                                     Message.CHAT_USAGE_REGION_DELETE,                     Flag.NOCONSOLE, Flag.CONFIRM);
	public static final CommandWrapper TOOL_GET =                             new Command(CommandToolGet.class,                             Permission.NOVAGUILDS_TOOL_GET,                                          Message.CHAT_USAGE_TOOL,                              Flag.NOCONSOLE              );
	public static final CommandWrapper PLAYERINFO =                           new Command(CommandPlayerInfo.class,                          Permission.NOVAGUILDS_PLAYERINFO,                          "playerinfo", Message.CHAT_USAGE_PLAYER_INFO                                                    );
	public static final CommandWrapper NOVAGUILDS =                           new Command(CommandNovaGuilds.class,                          Permission.NOVAGUILDS_NOVAGUILDS,                          "novaguilds"                                                                                    );
	public static final CommandWrapper CONFIRM =                              new Command(CommandConfirm.class,                             Permission.NOVAGUILDS_CONFIRM,                             "confirm",    Message.CHAT_USAGE_CONFIRM                                                        );

	private static final Map<String, CommandWrapper> map = new HashMap<>();

	static {
		for(Field field : Command.class.getFields()) {
			if(field.getType() != CommandWrapper.class) {
				continue;
			}

			try {
				CommandWrapper commandWrapper = (CommandWrapper) field.get(null);
				commandWrapper.setName(field.getName());
				map.put(field.getName(), commandWrapper);

				CommandExecutor commandExecutor = commandWrapper.getExecutorClass().newInstance();
				NovaGuilds.getInstance().getCommandManager().registerExecutor(commandWrapper, commandExecutor);

				commandWrapper.setExecutor(commandExecutor);
			}
			catch(IllegalAccessException | InstantiationException e) {
				LoggerUtils.exception(e);
			}
		}
	}

	/**
	 * The constructor
	 *
	 * @param permission     the permission
	 * @param genericCommand the generic command string
	 * @param usageMessage   the usage message
	 * @param tabCompleter   tab completer instance
	 * @param flags          command flags
	 */
	private Command(Class<? extends CommandExecutor> commandExecutorClass, Permission permission, String genericCommand, MessageWrapper usageMessage, TabCompleter tabCompleter, Flag... flags) {
		this.permission = permission;
		this.usageMessage = usageMessage;
		this.genericCommand = genericCommand;
		this.tabCompleter = tabCompleter;
		this.clazz = commandExecutorClass;
		Collections.addAll(this.flags, flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission     the permission
	 * @param genericCommand the generic command string
	 * @param usageMessage   the usage message
	 * @param flags          command flags
	 */
	private Command(Class<? extends CommandExecutor> commandExecutorClass, Permission permission, String genericCommand, MessageWrapper usageMessage, Flag... flags) {
		this(commandExecutorClass, permission, genericCommand, usageMessage, null, flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission     the permission
	 * @param genericCommand the generic command string
	 * @param tabCompleter   tab completer instance
	 * @param flags          command flags
	 */
	private Command(Class<? extends CommandExecutor> commandExecutorClass, Permission permission, String genericCommand, TabCompleter tabCompleter, Flag... flags) {
		this(commandExecutorClass, permission, genericCommand, null, tabCompleter, flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission   the permission
	 * @param usageMessage the usage message
	 * @param flags        command flags
	 */
	private Command(Class<? extends CommandExecutor> commandExecutorClass, Permission permission, MessageWrapper usageMessage, Flag... flags) {
		this(commandExecutorClass, permission, null, usageMessage, null, flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission     the permission
	 * @param genericCommand the generic command string
	 * @param flags          command flags
	 */
	private Command(Class<? extends CommandExecutor> commandExecutorClass, Permission permission, String genericCommand, Flag... flags) {
		this(commandExecutorClass, permission, genericCommand, null, null, flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission the permission
	 * @param flags      command flags
	 */
	private Command(Class<? extends CommandExecutor> commandExecutorClass, Permission permission, Flag... flags) {
		this(commandExecutorClass, permission, null, null, null, flags);
	}

	@Override
	public void setPermission(Permission permission) {
		throw new IllegalArgumentException("Not allowed for built in commands");
	}

	@Override
	public void setExecutor(CommandExecutor executor) {
		if(getExecutor() == null) {
			super.setExecutor(executor);
		}
		else {
			throw new IllegalArgumentException("Not allowed for built in commands");
		}
	}

	@Override
	public void setUsageMessage(MessageWrapper message) {
		throw new IllegalArgumentException("Not allowed for built in commands");
	}

	@Override
	public void setGenericCommand(String genericCommand) {
		throw new IllegalArgumentException("Not allowed for built in commands");
	}

	@Override
	public void setTabCompleter(TabCompleter tabCompleter) {
		throw new IllegalArgumentException("Not allowed for built in commands");
	}

	@Override
	public void setName(String name) {
		if(getName() == null) {
			super.setName(name);
		}
		else {
			throw new IllegalArgumentException("Not allowed for built in commands");
		}
	}

	@Override
	public void setFlags(Flag... flags) {
		throw new IllegalArgumentException("Not allowed for built in commands");
	}

	/**
	 * Gets a wrapper by executor
	 *
	 * @param executor the executor
	 * @return the wrapper
	 */
	public static CommandWrapper getCommand(CommandExecutor executor) {
		for(CommandWrapper wrapper : values()) {
			if(wrapper.getExecutor().equals(executor)) {
				return wrapper;
			}
		}

		return null;
	}

	/**
	 * Gets a wrapper by its generic command
	 *
	 * @param genericCommand generic command string
	 * @return the wrapper
	 */
	public static CommandWrapper getByGenericCommand(String genericCommand) {
		for(CommandWrapper wrapper : values()) {
			if(wrapper.hasGenericCommand() && wrapper.getGenericCommand().equalsIgnoreCase(genericCommand)) {
				return wrapper;
			}
		}

		return null;
	}

	/**
	 * Gets all commands
	 *
	 * @return array of CommandWrappers
	 */
	public static CommandWrapper[] values() {
		return map.values().toArray(new CommandWrapper[map.size()]);
	}

	/**
	 * This does nothing but forces the class to load it's fields
	 */
	public static void init() {

	}
}
