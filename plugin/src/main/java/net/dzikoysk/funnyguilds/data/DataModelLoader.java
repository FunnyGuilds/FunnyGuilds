package net.dzikoysk.funnyguilds.data;

import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import panda.std.Result;

/** Loads startup data and reports failures without depending on the plugin lifecycle. */
public final class DataModelLoader {

    private final FunnyGuildsLogger logger;

    public DataModelLoader(FunnyGuildsLogger logger) {
        this.logger = logger;
    }

    public Result<DataModel, String> load(Supplier<DataModel> factory) {
        try {
            DataModel dataModel = factory.get();
            dataModel.load();
            return Result.ok(dataModel);
        } catch (IllegalStateException exception) {
            if (exception.getCause() == null) {
                return Result.error(exception.getMessage());
            }
            this.logger.error("Could not load data", exception);
        } catch (Exception exception) {
            this.logger.error("Could not load data from database", exception);
        }
        return Result.error("Critical error has been encountered!");
    }

}
