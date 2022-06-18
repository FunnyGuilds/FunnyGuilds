package net.dzikoysk.funnyguilds.concurrency.requests;

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
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.telemetry.FunnyTelemetry;
import net.dzikoysk.funnyguilds.telemetry.FunnybinResponse;
import net.dzikoysk.funnyguilds.telemetry.PasteType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.stream.PandaStream;

public final class FunnybinRequest extends DefaultConcurrencyRequest {

    private final CommandSender sender;
    private final List<String> files;

    private FunnybinRequest(CommandSender sender, List<String> files) {
        this.sender = sender;
        this.files = files;
    }

    @Override
    public void execute() throws Exception {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
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

            ChatUtils.sendMessage(this.sender, formatter.format(messages.funnybinSendingFile));

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
                    ChatUtils.sendMessage(this.sender, formatter.format(messages.funnybinFileNotFound));
                    continue;
                }
                catch (IOException e) {
                    ChatUtils.sendMessage(this.sender, formatter.format(messages.funnybinFileNotOpened));
                    FunnyGuilds.getPluginLogger().error("Failed to open a file: " + fileName, e);
                    continue;
                }
            }

            try {
                sentPastes.add(FunnyTelemetry.postToFunnybin(content, type, fileName));
            }
            catch (IOException exception) {
                ChatUtils.sendMessage(this.sender, formatter.format(messages.funnybinFileNotSent));
                FunnyGuilds.getPluginLogger().error("Failed to submit a paste: " + fileName, exception);
            }
        }

        if (sentPastes.size() == 1) {
            String message = FunnyFormatter.format(messages.funnybinFileSent, "{LINK}", sentPastes.get(0).getShortUrl());
            ChatUtils.sendMessage(this.sender, message);
            return;
        }

        ChatUtils.sendMessage(this.sender, messages.funnybinBuildingBundle);

        try {
            Option<FunnybinResponse> response = FunnyTelemetry.createBundle(
                    PandaStream.of(sentPastes)
                            .map(FunnybinResponse::getUuid)
                            .toList()
            );

            if (response.isEmpty()) {
                throw new IOException("Response for FunnyTelemetry bundle is null");
            }

            String message = FunnyFormatter.format(messages.funnybinBundleSent, "{LINK}", response.get().getShortUrl());
            ChatUtils.sendMessage(this.sender, message);
        }
        catch (IOException exception) {
            ChatUtils.sendMessage(this.sender, messages.funnybinBundleNotBuilt);
            FunnyGuilds.getPluginLogger().error("Failed to submit a bundle. Files: " + this.files, exception);
        }
    }

    @Nullable
    private static FunnybinRequest ofData(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return new FunnybinRequest(sender, Arrays.asList("config", "log"));
        }

        if (args.length >= 2) {
            switch (args[1]) {
                case "config":
                    return new FunnybinRequest(sender, Collections.singletonList("config"));
                case "log":
                    return new FunnybinRequest(sender, Collections.singletonList("log"));
                case "custom":
                    if (args.length >= 3) {
                        new FunnybinRequest(sender, Collections.singletonList(args[2]));
                    }

                    break;
                case "bundle":
                    if (args.length >= 3) {
                        return new FunnybinRequest(sender, Arrays.asList(args).subList(2, args.length));
                    }

                    break;
                default:
                    return null;
            }
        }

        return null;
    }

    public static Option<FunnybinRequest> of(CommandSender sender, String[] args) {
        return Option.of(ofData(sender, args));
    }

}
