package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

/**
 * The migration makes sure {@code use-player-position-for-centery} is changed
 * into the correct version of {@code use-player-position-for-center-y}.
 *
 * The source of the problem is the edge case in the okaeri-configs'
 * {@link eu.okaeri.configs.annotation.Names} annotation, which was
 * unable to correctly guess intended format, combined with the missing
 * CustomKey to force the intended key name from the begging.
 */
public class P0005_Fix_heart_configuration_centery_key extends NamedMigration {

    public P0005_Fix_heart_configuration_centery_key() {
        super(
                "Rename heart-configuration's use-player-position-for-centery to use-player-position-for-center-y",
                move("heart-configuration.use-player-position-for-centery", "heart-configuration.use-player-position-for-center-y")
        );
    }

}
