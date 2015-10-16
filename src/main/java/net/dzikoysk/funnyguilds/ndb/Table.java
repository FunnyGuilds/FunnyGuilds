package net.dzikoysk.funnyguilds.ndb;

public enum Table {

    /**
     * Zapis wszystkich graczy, którzy kiedykolwiek byli na serwerze. Zawiera
     * ilość punków, unikalne UUID oraz ewentualną gildię do której należą.
     */
    USERS("users",
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY",
            "`uuid` CHAR(36) UNIQUE NOT NULL",
            "`points` INT DEFAULT 0",
            "`guild_id` INT NULL"
    ),

    /**
     * Zapis wszystkich pseudonimów graczy, jakie kiedykolwiek istniały na
     * serwerze. Zawiera ID gracza właściciela (#USERS) oraz dany pseudonim.
     */
    USERNAMES("users_names",
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY",
            "`user_id` INT NOT NULL", // #USERS
            "`name` VARCHAR(16) NOT NULL"
    ),

    /**
     * Zapis wszystkich zabójstw graczy, jakie kiedykolwiek wydarzyły się na
     * serwerze. Zawiera ID gracza zabójcy (#USERS), ID gracza ofiary (#USERS),
     * czas, w którym miało to miejsce oraz ewentualne podbicie innej gildii
     * (#CONQUESTS).
     */
    KILLS("users_kills",
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY",
            "`killer_id` INT NOT NULL", // #USERS
            "`victim_id` INT NOT NULL", // #USERS
            "`time` DATETIME NOT NULL",
            "`conquest_id` INT NULL" // #CONQUESTS
    ),

    GUILDS("guilds",
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY",
            "`tag` VARCHAR(30) UNIQUE NOT NULL",
            "`desc` TEXT NULL",
            "`lives` SMALLINT NOT NULL",
            "`friendly_fire` BOOL DEFAULT false",
            "`born` DATETIME NOT NULL",
            "`validated` BOOL DEFAULT false",
            "`owner_id` INT UNIQUE NOT NULL" // #USERS
    ),

    LOCKS("guilds_locks",
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY",
            "`guild_id` INT NOT NULL", // #GUILDS
            "`reason` TEXT NULL",
            "`assigned` DATETIME NOT NULL",
            "`expires` DATETIME NULL"
    ),

    REGIONS("guilds_regions",
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY",
            "`region_name` VARCHAR(30) NULL",
            "`guild_id` INT NOT NULL",
            "`center_x` INT NOT NULL",
            "`center_z` INT NOT NULL",
            "`size` INT NOT NULL",
            "`world` CHAR(36) NULL"
    ),

    HOMES("guilds_homes",
            ""
    ),

    RELATIONS("guilds_relations",
            ""
    ),

    CONQUESTS("guilds_conquests",
            ""
    ),

    ;

    private final String name;
    private final String[] schema;

    private Table(String name, String... schema) {
        this.name = name;
        this.schema = schema;
    }

    public String getId() {
        return this.name;
    }

    public String getName() {
        return "funnyguilds_" + this.getId(); // TODO pobierać prefix z konfiguracji
    }

    public String[] getSchema() {
        return this.schema;
    }

    public static String getSchemaString(Table table) {
        String[] schema = table.getSchema();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < schema.length; i++) {
            builder.append(schema);

            if (i + 1 != schema.length) // jeżeli nie jest ostatnie w tablicy
                builder.append(", ");
        }
        return builder.toString();
    }
}
