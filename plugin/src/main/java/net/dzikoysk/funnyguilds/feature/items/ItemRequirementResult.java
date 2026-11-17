package net.dzikoysk.funnyguilds.feature.items;

import java.util.Collections;
import java.util.Map;

public final class ItemRequirementResult {

    private final boolean meetsMoney;
    private final boolean meetsLevel;
    private final boolean meetsRank;
    private final Map<String, ItemCountResult> itemCounts;

    public ItemRequirementResult(
            boolean meetsMoney,
            boolean meetsLevel,
            boolean meetsRank,
            Map<String, ItemCountResult> itemCounts
    ) {
        this.meetsMoney = meetsMoney;
        this.meetsLevel = meetsLevel;
        this.meetsRank = meetsRank;
        this.itemCounts = Collections.unmodifiableMap(itemCounts);
    }

    public boolean meetsAll() {
        if (!meetsMoney || !meetsLevel || !meetsRank) {
            return false;
        }
        for (ItemCountResult count : itemCounts.values()) {
            if (!count.isMet()) {
                return false;
            }
        }
        return true;
    }

    public boolean isMeetsMoney() {
        return meetsMoney;
    }

    public boolean isMeetsLevel() {
        return meetsLevel;
    }

    public boolean isMeetsRank() {
        return meetsRank;
    }

    public Map<String, ItemCountResult> getItemCounts() {
        return itemCounts;
    }

    public static final class ItemCountResult {

        private final int required;
        private final int inv;
        private final int ender;
        private final String displayName;

        public ItemCountResult(int required, int inv, int ender, String displayName) {
            this.required = required;
            this.inv = inv;
            this.ender = ender;
            this.displayName = displayName;
        }

        public ItemCountResult(int required, int inv, int ender) {
            this(required, inv, ender, "");
        }

        public int getRequired() {
            return required;
        }

        public int getInv() {
            return inv;
        }

        public int getEnder() {
            return ender;
        }

        public int getTotal() {
            return inv + ender;
        }

        public int getMissing() {
            return Math.max(0, required - inv);
        }

        public boolean isMet() {
            return inv >= required;
        }

        public String getDisplayName() {
            return displayName;
        }

    }

}

