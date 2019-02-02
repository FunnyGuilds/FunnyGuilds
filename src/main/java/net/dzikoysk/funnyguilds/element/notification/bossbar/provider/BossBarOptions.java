package net.dzikoysk.funnyguilds.element.notification.bossbar.provider;

import com.google.common.collect.Sets;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;

import java.util.Collection;
import java.util.Set;

public final class BossBarOptions {

    private final BarColor     barColor;
    private final BarStyle     barStyle;
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

        private BarColor     barColor = BarColor.RED;
        private BarStyle     barStyle = BarStyle.SOLID;
        private Set<BarFlag> barFlags = Sets.newHashSet();

        public Builder color(String barColor) {
            for (BarColor loopColor : BarColor.values()) {
                if (loopColor.name().equalsIgnoreCase(barColor)) {
                    this.barColor = loopColor;
                    break;
                }
            }

            return this;
        }

        public Builder style(String barStyle) {
            for (BarStyle loopStyle : BarStyle.values()) {
                if (loopStyle.name().equalsIgnoreCase(barStyle)) {
                    this.barStyle = loopStyle;
                    break;
                }
            }

            return this;
        }

        public Builder flags(Collection<? extends String> barFlags) {
            for (String flag : barFlags) {
                for (BarFlag barFlag : BarFlag.values()) {
                    if (barFlag.name().equalsIgnoreCase(flag)) {
                        this.barFlags.add(barFlag);
                    }
                }
            }

            return this;
        }

        public BossBarOptions build() {
            return new BossBarOptions(this.barColor, this.barStyle, this.barFlags);
        }

    }
}
