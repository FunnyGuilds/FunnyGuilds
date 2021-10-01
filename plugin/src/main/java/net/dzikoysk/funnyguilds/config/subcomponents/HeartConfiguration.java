package net.dzikoysk.funnyguilds.config.subcomponents;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.io.File;

public class HeartConfiguration extends OkaeriConfig {

    @Comment("Blok lub entity, ktore jest sercem gildii")
    @Comment("Zmiana entity wymaga pelnego restartu serwera")
    @Comment("Bloki musza byc podawane w formacie - material:metadata")
    @Comment("Nazwy blokow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @Comment("Typ entity musi byc zgodny z ta lista (i zdrowym rozsadkiem) - https://spigotdocs.okaeri.eu/select/org/bukkit/entity/EntityType.html")
    @Comment("UWAGA: Zmiana bloku, gdy sa juz zrobione jakies gildie, spowoduje niedzialanie ich regionow")
    @Comment(" ")
    @Comment("UWAGA: Jesli jako serca gildii chcesz uzyc bloku, ktory spada pod wplywem grawitacji - upewnij sie, ze bedzie on stal na jakims bloku!")
    @Comment("Jesli pojawi sie w powietrzu - spadnie i plugin nie bedzie odczytywal go poprawnie!")
    public String createType = "ender_crystal";
    @Exclude
    public Pair<Material, Byte> createMaterial;
    @Exclude
    public EntityType createEntityType;

    @Comment("Na jakim poziomie ma byc wyznaczone centrum gildii")
    @Comment("Wpisz 0 jesli ma byc ustalone przez pozycje gracza")
    @CustomKey("create-center-y")
    public int createCenterY = 60;

    @Comment("Konfiguracja hologramu nad sercem gildii.")
    @CustomKey("hologram")
    public HologramConfiguration hologram = new HologramConfiguration();

    @Comment("Czy ma sie tworzyc kula z obsydianu dookola centrum gildii")
    public boolean createCenterSphere = true;

    @Comment("Czy przy tworzeniu gildii powinien byc wklejany schemat")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    public boolean pasteSchematicOnCreation = false;

    @Comment("Nazwa pliku ze schematem poczatkowym gildii")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    @Comment("Schemat musi znajdować się w folderze FunnyGuilds")
    public String guildSchematicFileName = "funnyguilds.schematic";

    @Comment("Czy schemat przy tworzeniu gildii powinien byc wklejany razem z powietrzem?")
    @Comment("Przy duzych schematach ma to wplyw na wydajnosc")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    public boolean pasteSchematicWithAir = true;

    @Exclude
    public File guildSchematicFile;

    public void loadProcessedProperties() {
        try {
            this.createEntityType = EntityType.valueOf(this.createType.toUpperCase().replace(" ", "_"));
        }
        catch (Exception materialThen) {
            this.createMaterial = MaterialUtils.parseMaterialData(this.createType, true);
        }

        if (this.pasteSchematicOnCreation) {
            if (this.guildSchematicFileName == null || this.guildSchematicFileName.isEmpty()) {
                FunnyGuilds.getPluginLogger().error("The field named \"guild-schematic-file-name\" is empty, but field \"paste-schematic-on-creation\" is set to true!");
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
