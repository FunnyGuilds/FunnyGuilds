package net.dzikoysk.funnyguilds.concurrency.requests;

import com.google.common.io.Files;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.commons.ConfigHelper;
import net.dzikoysk.funnyguilds.util.commons.LoggingUtils;
import net.dzikoysk.funnyguilds.util.telemetry.FunnyTelemetry;
import net.dzikoysk.funnyguilds.util.telemetry.FunnybinResponse;
import net.dzikoysk.funnyguilds.util.telemetry.PasteType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FunnybinRequest extends DefaultConcurrencyRequest {

    private final CommandSender sender;
    private final List<String> files;

    private FunnybinRequest(CommandSender sender, List<String> files) {
        this.sender = sender;
        this.files = files;
    }

    @Override
    public void execute() {
        List<FunnybinResponse> sentPastes = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            String fileName = files.get(i);
            File file;
            String content = null;
            PasteType type = PasteType.OTHER;

            sender.sendMessage(ChatColor.GREEN + "Wysylam plik: " + ChatColor.AQUA + (i + 1) + ChatColor.GREEN + "/" + ChatColor.AQUA + files.size() + ChatColor.GREEN + "...");

            if ("log".equals(fileName)) {
                file = new File("logs/latest.log");
                type = PasteType.LOGS;
            }
            else if ("config".equals(fileName)) {
                file = null;
                type = PasteType.CONFIG;

                PluginConfiguration config = ConfigHelper.loadConfig(FunnyGuilds.getInstance().getPluginConfigurationFile(), PluginConfiguration.class);
                config.mysql.hostname = "<CUT>";
                config.mysql.database = "<CUT>";
                config.mysql.user = "<CUT>";
                config.mysql.password = "<CUT>";

                content = ConfigHelper.configToString(config);
            }
            else {
                file = new File(fileName);
            }

            if (content == null) {
                try {
                    content = Files.asCharSource(file, StandardCharsets.UTF_8).read();
                }
                catch (FileNotFoundException e) {
                    sender.sendMessage(ChatColor.RED + "Podany plik: " + fileName + " nie istnieje");
                    continue;
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + "Podany plik: " + fileName + " nie mogl być otworzony (szczegoly w konsoli)");
                    FunnyGuilds.getInstance().getPluginLogger().error("Failed to open a file: " + fileName, e);
                    continue;
                }
            }

            try {
                sentPastes.add(FunnyTelemetry.postToFunnybin(content, type, fileName));
            }
            catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Podany plik: " + fileName + " nie mogl byc wyslany (szczegoly w konsoli)");
                FunnyGuilds.getInstance().getPluginLogger().error("Failed to submit a paste: " + fileName, e);
            }
        }

        if (sentPastes.size() == 1) {
            sender.sendMessage(ChatColor.GREEN + "Plik wyslany. Link: " + ChatColor.AQUA + sentPastes.get(0).getShortUrl());
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Tworze paczke z wyslanych plikow...");

        try {
            FunnybinResponse response = FunnyTelemetry.createBundle(sentPastes.stream().map(FunnybinResponse::getUuid).collect(Collectors.toList()));
            sender.sendMessage(ChatColor.GREEN + "Paczka wyslana. Link: " + ChatColor.AQUA + response.getShortUrl());
        }
        catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Wystapil blad podczas tworzenia paczki. ");
            FunnyGuilds.getInstance().getPluginLogger().error("Failed to submit a bundle. Files: " + files, e);
        }
    }

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
            }
        }

        return null;
    }

    public static Optional<FunnybinRequest> of(CommandSender sender, String[] args) {
        return Optional.ofNullable(ofData(sender, args));
    }

}
