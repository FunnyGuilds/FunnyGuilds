package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.io.File;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import panda.std.Pair;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class HeartConfiguration extends OkaeriConfig {

    @Comment("Blok lub entity, które jest sercem gildii")
    @Comment("Zmiana entity wymaga pełnego restartu serwera")
    @Comment("Bloki muszą być podawane w formacie - material:metadata")
    @Comment("Nazwy bloków muszą pasować do nazw podanych tutaj: https://spigotdocs.okaeri.cloud/select/org/bukkit/Material.html")
    @Comment("Typ entity musi byc zgodny z tą lista (i zdrowym rozsądkiem) - https://spigotdocs.okaeri.cloud/select/org/bukkit/entity/EntityType.html")
    @Comment("UWAGA: Zmiana bloku, gdy są juz zrobione jakieś gildie, spowoduje nieprawidłowe działanie ich regionów")
    @Comment(" ")
    @Comment("UWAGA: Jeśli jako serca gildii chcesz użyć bloku, który spada pod wpływem grawitacji - upewnij się, ze będzie on stał na jakimś innym bloku!")
    @Comment("Jeśli pojawi sie w powietrzu - spadnie i plugin nie będzie odczytywał go poprawnie!")
    public String createType = "ender_crystal";
    @Exclude
    public Pair<Material, Byte> createMaterial;
    @Exclude
    public EntityType createEntityType;

    @Comment("")
    @Comment("Czy poziom na jakim ma być wyznaczone centrum gildii ma być ustalany przez pozycję gracza")
    @CustomKey("use-player-position-for-center-y")
    public boolean usePlayerPositionForCenterY = false;

    @Comment("")
    @Comment("Na jakim poziomie ma być wyznaczone centrum gildii")
    @CustomKey("create-center-y")
    public int createCenterY = 60;

    @Comment("")
    @Comment("Konfiguracja hologramu nad sercem gildii")
    public HologramConfiguration hologram = new HologramConfiguration();

    @Comment("")
    @Comment("Czy ma się tworzyć kula z obsydianu dookoła centrum gildii")
    public boolean createCenterSphere = true;

    @Comment("")
    @Comment("Czy przy tworzeniu gildii powinien być wklejany schemat")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    public boolean pasteSchematicOnCreation = false;

    @Comment("")
    @Comment("Nazwa pliku ze schematem początkowym gildii")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    @Comment("Schemat musi znajdować się w folderze FunnyGuilds")
    public String guildSchematicFileName = "funnyguilds.schematic";

    @Comment("")
    @Comment("Czy schemat przy tworzeniu gildii powinien być wklejany razem z powietrzem")
    @Comment("Przy dużych schematach ma to wpływ na wydajność")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    public boolean pasteSchematicWithAir = true;

    @Exclude
    public File guildSchematicFile;

    public void loadProcessedProperties() {
        try {

            this.createEntityType = EntityType.valueOf(FunnyFormatter.format(this.createType.toUpperCase(Locale.ROOT), " ", "_"));
        }
        catch (IllegalArgumentException materialThen) {
            this.createMaterial = MaterialUtils.parseMaterialData(this.createType, true);
        }

        if (this.pasteSchematicOnCreation) {
            if (this.guildSchematicFileName == null || this.guildSchematicFileName.isEmpty()) {
                FunnyGuilds.getPluginLogger().error("The field named \"guild-schematic-file-name\" is empty, but field " +
                        "\"paste-schematic-on-creation\" is set to true!");
                this.pasteSchematicOnCreation = false;
            }
            else {
                this.guildSchematicFile = new File(FunnyGuilds.getInstance().getDataFolder(), this.guildSchematicFileName);

                if (!this.guildSchematicFile.exists()) {
                    FunnyGuilds.getPluginLogger().error("File with given name in field \"guild-schematic-file-name\" does not exist!");
                    this.pasteSchematicOnCreation = false;
                }
            }
        }
    }

}
