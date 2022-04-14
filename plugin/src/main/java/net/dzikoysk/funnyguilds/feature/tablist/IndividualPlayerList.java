package net.dzikoysk.funnyguilds.feature.tablist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.tablist.TablistPage;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.dzikoysk.funnyguilds.shared.MapUtil;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import panda.std.Option;

public class IndividualPlayerList {

    private final User user;
    private final PlayerList playerList;

    private final Map<Integer, String> unformattedCells;
    private final int cellCount;
    private final String header;
    private final String footer;
    private final int cellPing;

    private final boolean animated;
    private final List<TablistPage> pages;
    private final int pagesCount;

    private final boolean enableLegacyPlaceholders;

    private int cycle = 0;
    private int currentPage = 0;

    public IndividualPlayerList(User user,
                                PlayerListAccessor playerListAccessor,
                                Map<Integer, String> unformattedCells,
                                String header, String footer,
                                boolean animated,
                                List<TablistPage> pages,
                                int cellPing,
                                boolean fillCells,
                                boolean enableLegacyPlaceholders) {
        this.user = user;

        this.unformattedCells = new HashMap<>(unformattedCells);
        this.header = header;
        this.footer = footer;
        this.animated = animated;
        this.pages = pages;
        this.pagesCount = pages.size();
        this.cellPing = cellPing;

        this.enableLegacyPlaceholders = enableLegacyPlaceholders;

        if (!fillCells) {
            Entry<Integer, String> entry = MapUtil.findTheMaximumEntryByKey(unformattedCells);

            if (entry != null) {
                this.cellCount = entry.getKey();
            }
            else {
                this.cellCount = PlayerListConstants.DEFAULT_CELL_COUNT;
            }
        }
        else {
            this.cellCount = PlayerListConstants.DEFAULT_CELL_COUNT;
        }

        this.playerList = playerListAccessor.createPlayerList(this.cellCount);
    }

    public void send() {
        Map<Integer, String> unformattedCells = this.unformattedCells;
        String header = this.header;
        String footer = this.footer;

        if (this.animated) {
            this.cycle++;

            int pageCycles = this.pages.get(this.currentPage).cycles;
            if (this.cycle + 1 >= pageCycles) {
                this.cycle = 0;
                this.currentPage++;

                if (this.currentPage >= this.pagesCount) {
                    this.currentPage = 0;
                }
            }

            TablistPage page = this.pages.get(this.currentPage);
            if (page != null) {
                if (page.playerList != null) {
                    unformattedCells.putAll(page.playerList);
                }

                if (page.playerListHeader != null) {
                    header = page.playerListHeader;
                }

                if (page.playerListFooter != null) {
                    footer = page.playerListFooter;
                }
            }
        }

        String[] preparedCells = this.putVarsPrepareCells(unformattedCells, header, footer);
        String preparedHeader = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT];
        String preparedFooter = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1];

        SkinTexture[] preparedCellsTextures = this.putTexturePrepareCells();

        this.user.getPlayer()
                .peek(player -> this.playerList.send(player, preparedCells, preparedHeader, preparedFooter, preparedCellsTextures, this.cellPing));
    }

    private String[] putVarsPrepareCells(Map<Integer, String> tablistPattern, String header, String footer) {
        String[] allCells = new String[PlayerListConstants.DEFAULT_CELL_COUNT + 2]; // Additional two for header/footer
        for (int i = 0; i < this.cellCount; i++) {
            allCells[i] = this.putTop(tablistPattern.getOrDefault(i + 1, ""));
        }
        allCells[PlayerListConstants.DEFAULT_CELL_COUNT] = header;
        allCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1] = footer;
        String mergedCells = StringUtils.join(allCells, '\0');
        return StringUtils.splitPreserveAllTokens(this.putVars(mergedCells), '\0');
    }

    private String putTop(String cell) {
        return FunnyGuilds.getInstance().getRankPlaceholdersService().format(cell, this.user);
    }

    private String putVars(String cell) {
        String formatted = cell;

        Option<Player> playerOption = this.user.getPlayer();
        if (playerOption.isEmpty()) {
            return formatted;
        }
        Player player = playerOption.get();

        formatted = FunnyGuilds.getInstance().getTablistPlaceholdersService().format(formatted, this.user);
        formatted = ChatUtils.colored(formatted);
        formatted = HookUtils.replacePlaceholders(player, formatted);

        return formatted;
    }

    public SkinTexture[] putTexturePrepareCells() {
        SkinTexture[] textures = new SkinTexture[PlayerListConstants.DEFAULT_CELL_COUNT];
        for (int i = 0; i < this.cellCount; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            SkinTexture texture;
            switch (random.nextInt(0, 2)) {
                case 0:
                    texture = new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTU5NDI5NjI1MTAzNiwKICAicHJvZmlsZUlkIiA6ICJhNzFjNTQ5MmQwNTE0ZDg3OGFiOTEwZmRmZmRmYzgyZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcHBsZTU0NDciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBlNGIyNmJkMDcwNjFmMDQwZGUxNmJiOTVlMTY3NDU2MWU0NjQzZGY3Nzg0MDcxYzUwOGFjZTMxNDkwZjJkOCIKICAgIH0KICB9Cn0=", "rHxHLDlEDkDvGGo8foVbmtsqUeQH++nA3cLDGYdkZ8mn9vCgE0dmbGKEnVydLB95pJoy9vCFE2Kzy7iG78H1iNWDi8VUGcY7TPRLfGihYuO9MavZ35nbKvY0to66dTMpW3GSFpOQ/sGGmBjXfEAipa7tiASK2hiDv/F5JMGqkMB0oczlXyYdcKsrRNWRL29qE3wZX2mrMX3g5liJduviAWyeNjlvX012mqDOQ2xeH1OPAotwE6mEWLwZUWfSjqbHrBRLX13DhwCylSNqP7FLGMeXghN6ESLD9vz26xpFzGVLKXww1RXH+2jJ/07G0QNz2oYURF84AerliODWoZJc0HTjchL0CVRv5Vkhy+OwagLsvcvcJSm//O4imxk44sa3n5j3yzHP4aHxq7SUTO53IsMD0y23tCYHsYahhAcwVLrKpoFF6sApvn4CNHsRzeuKRszg33h8OM9esCYqH8ZUmaf9XzXyM3xSQ65coc00bzl1UMQ8zCO7kNanb5klkICSG0xkbU4L9U2dc5zp0oK/2ORdP5oWsLfbnATSqFYSPcqABb0jTYf7XSU2rBUTeNF+YWXcUf2e2scc8zrwJP9L0qReCIfKAP3gnI28ujwk/AZ53RRVjDlxD39rqcIz2I1P6CSJzcJN6aOYo3BBrg2qZrSU77pDEmghrPVj6wBjMpM=");
                    break;
                case 1:
                    texture = new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTY0OTkyNTYyOTQ1NSwKICAicHJvZmlsZUlkIiA6ICI0OWIzODUyNDdhMWY0NTM3YjBmN2MwZTFmMTVjMTc2NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJiY2QyMDMzYzYzZWM0YmY4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkwMmQ1NWJjOWI3NDg0OWU2NDg4OGExMzRlMTA1ZjA4YjRiMjdlNmJjZDg5MDBhNzgyMjJiNzdlMWY4N2YwNTAiCiAgICB9CiAgfQp9", "SpbWrqCDXZOdoIsJOe5aJO3qdoN9v1alCzVtTdaBmpLPDRm0Kx7q1fLXgySu49KYyWMdKwgfkeCS1JkdCUACVns1xXbqZ/lsxIeD+mqktReXsBVQ8oGRXP3ho8+hcgSfHJdoV3vR2Qx+lV94nJAbOBPFqLFJFcfmNKbUbCW1QimJBGjgjBC+41GVeOZQhT52X8tLhkL88/0v6PvLtQ6Her7EVEjB1yvbDySuQPFHPTqtPCDqDu7TyEZ6Lbw3q9FW2HVwyfvQj91nIYEFMMyEId5KNtVWbBSiMrtNTLLscUf3pp7CtG8R0eTTkWZdz1ALKy656VcOGkJBCf6bN0nG75IyJeKSl7mGlaKCASco9LjIyhMRdAaqchY9G3HnkaTL1j2vi6JRDPuP3R/XjYg8bip1yWjVWmLNc6TvCRLBdbTo508pKeXPWBOnGQkucL5yT6mtzveFxCSVy8GBrjmwRK0XnaPl1TQfo4frDUBMkyrur0fN1rnBcGKXEwSlIWh7uTvXKmho4/o3zcMyiPb5zw73fASb6+l0lj/OMig/Jx3h+IpZwgfv1nQjL9+A8/ZJaGmsorVArD5J1s8gqaf8u26yCNKOXHa66Ojbgih2fI8TEKGM4AthIA9OkRB9Gl25B7uQwQsX/cATuo+y8rSGL8+vWgA8a1waXv+/kMlp5/4=");
                    break;
                default:
                    texture = new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTY0OTkyNDYwMTAwOSwKICAicHJvZmlsZUlkIiA6ICI1MjhlYzVmMmEzZmM0MDA0YjYwY2IwOTA5Y2JiMjdjYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJQdWxpenppIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JmZTQ4ZWNlODdhYWVhOTBkZjEwNDdiYmE5ODY0ZjQwOThhMWVmN2Q4YmYzM2E0MjI5ODE0ZDY0ZWI3YzBlNDYiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "lQecbM8q2//0+k45oOMLwVyHXn2Ts6p9HLuZtiTAEHxY9x5HTf8tVrk+UgUxXRcAIRv/9c/47daQlSKFwoXQgXpIglR/8vqLkDodsDc1edPiOdp7GFOVCJ8Jnj4UaV+kQ+J93zzpW6pAbLlupZuwriJzfPMPhqFYuJp58ZXVwiE9uRuLykY3aXiKxPjAcqGqU8e8FcxzLZXj4b0Z0xvG+Imqoak6PYIpMW9oOhP4ZZ/u5Ag+dvVKKNq6aVfovrWHH/HYsIp6imfFebQuY51vkvOMNw7eNexqcAmnBFonv9z28VF1hXZI9bbyOsArYboPrxToDbumubVMGH6qHnd6QIqLRFg1/08IieH56/fhDU+UTOyGoED0j0EUXMxsJd2behUVCr67ykASdlPg634yYtVJigvQU0joe1UruCUMjDxJV7ZtGTNPQvnJh386vhnn2fDZXZJlZjisA0e65j2r6MHLkO/3IttTDSmXDn6Iiv9JtBSqcBWSnpKvCjFs7JQ3N9nNkZqlqgSSzL/1CxEIQI4htUC2qN1IZd8f+EM0iHkkngse9u0svPTYJ3hjyPFpi/B2nD9L/+9yOCn9IYXofUyZQW+saS8Ir/iOummoUmwhrKjDBHYZwI3HaEDkh+N/6s2BnKr3E4KbH+8W+dljbsfx0bywzs98QePflx06U8Q=");
            }
            textures[i] = texture;
        }
        return textures;
    }

}
