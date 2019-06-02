package net.dzikoysk.funnyguilds.command;

import com.google.common.io.Files;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsVersion;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.util.commons.ConfigHelper;
import net.dzikoysk.funnyguilds.util.commons.LoggingUtils;
import net.dzikoysk.funnyguilds.util.telemetry.FunnyTelemetry;
import net.dzikoysk.funnyguilds.util.telemetry.FunnybinResponse;
import net.dzikoysk.funnyguilds.util.telemetry.PasteType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExcFunnyGuilds implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        String parameter = args.length > 0 ? args[0].toLowerCase() : "";

        switch (parameter) {
            case "reload":
            case "rl":
                reload(sender);
                break;
            case "check":
            case "update":
                FunnyGuildsVersion.isNewAvailable(sender, true);
                break;
            case "save-all":
                saveAll(sender);
                break;
            case "funnybin":
                post(sender, args);
                break;
            case "help":
                sender.sendMessage(ChatColor.AQUA + "FunnyGuilds Help:");
                sender.sendMessage(ChatColor.GRAY + "/funnyguilds (reload|rl) - przeladuj plugin");
                sender.sendMessage(ChatColor.GRAY + "/funnyguilds (update|check) - sprawdz dostepnosc aktualizacji");
                sender.sendMessage(ChatColor.GRAY + "/funnyguilds save-all - zapisz wszystko");
                sender.sendMessage(ChatColor.GRAY + "/funnyguilds funnybin - zapisz konfigurację online (~ usprawnia pomoc na https://github.com/FunnyGuilds/FunnyGuilds/issues)");
                break;
            default:
                sender.sendMessage(ChatColor.GRAY + "FunnyGuilds " + ChatColor.AQUA + FunnyGuilds.getInstance().getFullVersion() + ChatColor.GRAY + " by " + ChatColor.AQUA + "FunnyGuilds Team");
                break;
        }

    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission("funnyguilds.reload")) {
            sender.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().permission);
            return;
        }

        long startTime = System.currentTimeMillis();

        Thread thread = new Thread(() -> {
            FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
            funnyGuilds.reloadPluginConfiguration();
            funnyGuilds.reloadMessageConfiguration();
            funnyGuilds.getDataPersistenceHandler().reloadHandler();
            funnyGuilds.getDynamicListenerManager().reloadAll();

            if (FunnyGuilds.getInstance().getPluginConfiguration().playerListEnable) {
                PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
                AbstractTablist.wipeCache();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    AbstractTablist.createTablist(config.playerList, config.playerListHeader, config.playerListFooter, config.playerListPing, player);
                }
            }

            long endTime = System.currentTimeMillis();
            String diff = String.format("%.2f", ((endTime - startTime) / 1000.0));

            sender.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano! (" + ChatColor.AQUA + diff + "s" + ChatColor.GRAY + ")");
        });

        sender.sendMessage(ChatColor.GRAY + "Przeladowywanie...");
        thread.start();
    }

    private void saveAll(CommandSender sender) {
        if (!sender.hasPermission("funnyguilds.admin")) {
            sender.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().permission);
            return;
        }

        sender.sendMessage(ChatColor.GRAY + "Zapisywanie...");
        long l = System.currentTimeMillis();

        DataModel dataModel = FunnyGuilds.getInstance().getDataModel();

        try {
            dataModel.save(false);
            FunnyGuilds.getInstance().getInvitationPersistenceHandler().saveInvitations();
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("An error occurred while saving plugin data!", ex);
            return;
        }

        sender.sendMessage(ChatColor.GRAY + "Zapisano (" + ChatColor.AQUA + (System.currentTimeMillis() - l) / 1000.0F + "s" + ChatColor.GRAY + ")!");
    }

    private void post(CommandSender sender, String[] args) {
        if (args.length == 1) {
            this.doPost(sender, Arrays.asList("config", "log"));
            return;
        }
        else if (args.length >= 2) {
            switch (args[1]) {
                case "config": {
                    this.doPost(sender, Collections.singletonList("config"));
                    return;
                }
                case "log": {
                    this.doPost(sender, Collections.singletonList("log"));
                    return;
                }
                case "custom": {
                    if (args.length >= 3) {
                        this.doPost(sender, Collections.singletonList(args[2]));
                        return;
                    }
                    break;
                }
                case "bundle": {
                    if (args.length >= 3) {
                        this.doPost(sender, Arrays.asList(args).subList(2, args.length));
                        return;
                    }
                    break;
                }
            }
        }

        sender.sendMessage(ChatColor.RED + "Uzycie: ");
        sender.sendMessage(ChatColor.RED + "/fg funnybin - domyslnie wysyla FunnyGuilds/config.yml i logs/latest.log");
        sender.sendMessage(ChatColor.RED + "/fg funnybin config - wysyla FunnyGuilds/config.yml");
        sender.sendMessage(ChatColor.RED + "/fg funnybin log - wysyla logs/latest.log");
        sender.sendMessage(ChatColor.RED + "/fg funnybin custom <path> - wysyla dowolny plik z folderu serwera na funnybina");
        sender.sendMessage(ChatColor.RED + "/fg funnybin bundle <file1> <fileN...> - wysyla dowolne pliki z folderu serwera na funnybina");
    }

    private void doPost(CommandSender sender, List<String> files) {
        sender.getServer().getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(), () -> {
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
                    LoggingUtils.flushRootLogger();
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
                    sender.sendMessage(ChatColor.RED + "Podany plik: " + fileName + " nie mogl byc wyslany (szczegoly w konsolii)");
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
        });
    }
}
