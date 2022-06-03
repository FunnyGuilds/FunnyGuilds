package net.dzikoysk.funnyguilds.feature.notification.bossbar.provider;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import panda.std.stream.PandaStream;

public final class BossBarOptions {

    private final BarColor barColor;
    private final BarStyle barStyle;
    private final Set<BarFlag> barFlags;

    private BossBarOptions(BarColor barColor, BarStyle barStyle, Set<BarFlag> barFlags) {
        this.barColor = barColor;
        this.barStyle = barStyle;
        this.barFlags = barFlags;
    }

    BarColor getColor() {
        return this.barColor;
    }

    BarStyle getStyle() {
        return this.barStyle;
    }

    Set<BarFlag> getFlags() {
        return this.barFlags;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private BarColor barColor = BarColor.RED;
        private BarStyle barStyle = BarStyle.SOLID;
        private Set<BarFlag> barFlags = Sets.newHashSet();

        public Builder color(String barColor) {
            try {
                this.barColor = BarColor.valueOf(barColor.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException exception) {
                FunnyGuilds.getPluginLogger().warning("No BarColor of value " + barColor + " found");
            }

            return this;
        }

        public Builder style(String barStyle) {
            try {
                this.barStyle = BarStyle.valueOf(barStyle.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException exception) {
                FunnyGuilds.getPluginLogger().warning("No BarStyle of value " + barStyle + " found");
            }

            return this;
        }

        public Builder flags(Collection<? extends String> barFlags) {
            PandaStream.of(barFlags).forEach(barFlag -> {
                try {
                    this.barFlags.add(BarFlag.valueOf(barFlag.toUpperCase(Locale.ROOT)));
                }
                catch (IllegalArgumentException exception) {
                    FunnyGuilds.getPluginLogger().warning("No BarFlag of value " + barFlag + " found");
                }
            });

            return this;
        }

        public BossBarOptions build() {
            return new BossBarOptions(this.barColor, this.barStyle, this.barFlags);
        }
    }

}
