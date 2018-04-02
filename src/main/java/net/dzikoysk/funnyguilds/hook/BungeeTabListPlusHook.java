package net.dzikoysk.funnyguilds.hook;

import codecrafter47.bungeetablistplus.api.bukkit.BungeeTabListPlusBukkitAPI;
import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.RandomUtils;
import net.dzikoysk.funnyguilds.util.commons.StringUtils;
import org.bukkit.entity.Player;

import java.util.Locale;

public class BungeeTabListPlusHook {

    public static void initVariableHook() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();
        
        // Guilds number
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_guilds") {

            @Override
            public String getReplacement(Player player) {
                return String.valueOf(GuildUtils.getGuilds().size());
            }
        });
        
        // Users number
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_users") {

            @Override
            public String getReplacement(Player player) {
                return String.valueOf(UserUtils.getUsers().size());
            }
        });
        
        // User deaths
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_deaths") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                return String.valueOf(u.getRank().getDeaths());
            }
        });

        // User KDR
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_kdr") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                return String.format(Locale.US, "%.2f", u.getRank().getKDR());
            }
        });

        // User kills
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_kills") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                return String.valueOf(u.getRank().getKills());
            }
        });

        // User points (formatted)
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_points-format") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                return IntegerRange.inRange(u.getRank().getPoints(), config.pointsFormat).replace("{POINTS}", String.valueOf(u.getRank().getPoints()));
            }
        });

        // User points
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_points") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                return String.valueOf(u.getRank().getPoints());
            }
        });

        // User position
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_position") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                return String.valueOf(u.getRank().getPosition());
            }
        });

        // Guild allies number
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-allies") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? "0" : String.valueOf(g.getAllies().size());
            }
        });

        // Guild deaths
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-deaths") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? "0" : String.valueOf(g.getRank().getDeaths());
            }
        });

        // Guild deputies number
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-deputies") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? messages.gDeputiesNoValue : (g.getDeputies().isEmpty() ? messages.gDeputiesNoValue : StringUtils.toString(UserUtils.getNames(g.getDeputies()), false));
            }
        });

        // Guild deputy (random name)
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-deputy") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? messages.gDeputyNoValue : (g.getDeputies().isEmpty() ? messages.gDeputyNoValue : g.getDeputies().get(RandomUtils.RANDOM_INSTANCE.nextInt(g.getDeputies().size())).getName());
            }
        });

        // Guild KDR
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-kdr") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? "0.00" : String.format(Locale.US, "%.2f", g.getRank().getKDR());
            }
        });

        // Guild kills
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-kills") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? "0" : String.valueOf(g.getRank().getKills());
            }
        });

        // Guild lives
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-lives") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? "0" : String.valueOf(g.getLives());
            }
        });

        // Guild members number
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-members-all") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? "0" : String.valueOf(g.getMembers().size());
            }
        });

        // Guild online members number
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-members-online") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? "0" : String.valueOf(g.getOnlineMembers().size());
            }
        });

        // Guild name
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-name") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? messages.gNameNoValue : g.getName();
            }
        });

        // Guild owner name
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-owner") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? messages.gOwnerNoValue : g.getOwner().getName();
            }
        });

        // Guild points (formatted)
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-points-format") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? IntegerRange.inRange(0, config.pointsFormat).replace("{POINTS}", "0") : IntegerRange.inRange(g.getRank().getPoints(), config.pointsFormat).replace("{POINTS}", String.valueOf(g.getRank().getPoints()));
            }
        });

        // Guild points
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-points") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? "0" : String.valueOf(g.getRank().getPoints());
            }
        });

        // Guild position
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-position") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? "0" : String.valueOf(g.getRank().getPosition());
            }
        });

        // Guild region size
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-region-size") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? messages.minMembersToIncludeNoValue : String.valueOf(g.getRegionData().getSize());
            }
        });

        // Guild tag
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-tag") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? messages.gTagNoValue : g.getTag();
            }
        });
        
        // Guild validity
        BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_g-validity") {

            @Override
            public String getReplacement(Player player) {
                User u = User.get(player);
                if (u == null) {
                    return "";
                }

                Guild g = u.getGuild();
                return g == null ? messages.gValidityNoValue : config.dateFormat.format(g.getValidityDate());
            }
        });
        
        // Guild TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_gtop-" + index) {

                @Override
                public String getReplacement(Player player) {
                    return Parser.parseRank("{GTOP-" + index + "}");
                }
            });
        }
        
        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_ptop-" + index) {

                @Override
                public String getReplacement(Player player) {
                    return Parser.parseRank("{PTOP-" + index + "}");
                }
            });
        }
        
        FunnyLogger.info("BungeeTabListPlus hook has been enabled!");
    }

}
