package net.dzikoysk.funnyguilds.telemetry;

import com.google.common.io.Files;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.stream.PandaStream;

public final class FunnybinAsyncTask extends AsyncFunnyTask {

    private final CommandSender sender;
    private final List<String> files;

    private FunnybinAsyncTask(CommandSender sender, List<String> files) {
        this.sender = sender;
        this.files = files;
    }

    @Override
    public void execute() throws Exception {
        MessageService messageService = FunnyGuilds.getInstance().getMessageService();
        List<FunnybinResponse> sentPastes = new ArrayList<>();

        for (int i = 0; i < this.files.size(); i++) {
            String fileName = this.files.get(i);
            File file;
            String content = null;
            PasteType type = PasteType.OTHER;

            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{NUM}", i + 1)
                    .register("{TOTAL}", this.files.size())
                    .register("{FILE}", fileName);

            messageService.getMessage(config -> config.funnybinSendingFile)
                    .with(formatter)
                    .receiver(this.sender)
                    .send();

            if ("log".equals(fileName)) {
                file = new File("logs/latest.log");
                type = PasteType.LOGS;
            }
            else if ("config".equals(fileName)) {
                file = null;
                type = PasteType.CONFIG;

                PluginConfiguration config = ConfigManager.create(PluginConfiguration.class, (it) -> {
                    it.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons());
                    it.withBindFile(FunnyGuilds.getInstance().getPluginConfigurationFile());
                    it.load();
                });

                config.mysql.hostname = "<CUT>";
                config.mysql.database = "<CUT>";
                config.mysql.user = "<CUT>";
                config.mysql.password = "<CUT>";

                content = config.saveToString();
            }
            else {
                file = new File(fileName);
            }

            if (content == null && file != null) {
                try {
                    content = Files.asCharSource(file, StandardCharsets.UTF_8).read();
                }
                catch (FileNotFoundException e) {
                    messageService.getMessage(config -> config.funnybinFileNotFound)
                            .with(formatter)
                            .receiver(this.sender)
                            .send();
                    continue;
                }
                catch (IOException e) {
                    messageService.getMessage(config -> config.funnybinFileNotOpened)
                            .with(formatter)
                            .receiver(this.sender)
                            .send();
                    FunnyGuilds.getPluginLogger().error("Failed to open a file: " + fileName, e);
                    continue;
                }
            }

            try {
                sentPastes.add(FunnyTelemetry.postToFunnybin(content, type, fileName));
            }
            catch (IOException exception) {
                messageService.getMessage(config -> config.funnybinFileNotSent)
                        .with(formatter)
                        .receiver(this.sender)
                        .send();
                FunnyGuilds.getPluginLogger().error("Failed to submit a paste: " + fileName, exception);
            }
        }

        if (sentPastes.size() == 1) {
            messageService.getMessage(config -> config.funnybinFileSent)
                    .with("{LINK}", sentPastes.get(0).getShortUrl())
                    .receiver(this.sender)
                    .send();
            return;
        }

        messageService.getMessage(config -> config.funnybinBuildingBundle)
                .receiver(this.sender)
                .send();

        try {
            Option<FunnybinResponse> response = FunnyTelemetry.createBundle(
                    PandaStream.of(sentPastes)
                            .map(FunnybinResponse::getUuid)
                            .toList()
            );

            if (response.isEmpty()) {
                throw new IOException("Response for FunnyTelemetry bundle is null");
            }

            messageService.getMessage(config -> config.funnybinBundleSent)
                    .with("{LINK}", response.get().getShortUrl())
                    .receiver(this.sender)
                    .send();
        }
        catch (IOException exception) {
            messageService.getMessage(config -> config.funnybinBundleNotBuilt)
                    .receiver(this.sender)
                    .send();
            FunnyGuilds.getPluginLogger().error("Failed to submit a bundle. Files: " + this.files, exception);
        }
    }

    @Nullable
    private static FunnybinAsyncTask ofData(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return new FunnybinAsyncTask(sender, Arrays.asList("config", "log"));
        }

        if (args.length >= 2) {
            switch (args[1]) {
                case "config":
                    return new FunnybinAsyncTask(sender, Collections.singletonList("config"));
                case "log":
                    return new FunnybinAsyncTask(sender, Collections.singletonList("log"));
                case "custom":
                    if (args.length >= 3) {
                        new FunnybinAsyncTask(sender, Collections.singletonList(args[2]));
                    }

                    break;
                case "bundle":
                    if (args.length >= 3) {
                        return new FunnybinAsyncTask(sender, Arrays.asList(args).subList(2, args.length));
                    }

                    break;
                default:
                    return null;
            }
        }

        return null;
    }

    public static Option<FunnybinAsyncTask> of(CommandSender sender, String[] args) {
        return Option.of(ofData(sender, args));
    }

}
