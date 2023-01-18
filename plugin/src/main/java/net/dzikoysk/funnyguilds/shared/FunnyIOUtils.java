package net.dzikoysk.funnyguilds.shared;

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import panda.std.Result;
import panda.std.stream.PandaStream;
import panda.utilities.IOUtils;

public final class FunnyIOUtils {

    private FunnyIOUtils() {
    }

    public static Result<File, String> createFile(String fileName, boolean isDirectory) {
        return createFile(new File(fileName), isDirectory);
    }

    public static Result<File, String> createFile(File file, boolean isDirectory) {
        if (!file.exists()) {
            try {
                if (isDirectory) {
                    if (!file.mkdirs()) {
                        return Result.error("Could not create directory");
                    }

                    return Result.ok(file);
                }

                File parentFile = file.getParentFile();
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    return Result.error("Could not create parent directories");
                }

                if (!file.createNewFile()) {
                    return Result.error("Could not create file");
                }
            }
            catch (IOException exception) {
                FunnyGuilds.getPluginLogger().error("Could not initialize file: " + file.getAbsolutePath(), exception);
            }
        }

        return Result.ok(file);
    }

    public static void deleteFile(String fileName) {
        deleteFile(new File(fileName));
    }

    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] contents = file.listFiles();
            if (contents != null) {
                PandaStream.of(contents).forEach(FunnyIOUtils::deleteFile);
            }
        }

        if (!file.delete()) {
            FunnyGuilds.getPluginLogger().error("Failed to delete file: " + file.getAbsolutePath());
        }
    }

    public static String getContent(String urlString) {
        try {
            URLConnection connection = new URL(urlString).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;

            try (InputStream input = connection.getInputStream()) {
                return IOUtils.convertStreamToString(input, Charset.forName(encoding))
                        .orThrow(exception -> exception);
            }
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().update("Connection to the server (" + urlString + ") failed!");
            FunnyGuilds.getPluginLogger().update("Reason: " + Throwables.getStackTraceAsString(exception));
        }

        return "";
    }

    public static void copyFileFromResources(InputStream resource, File destination, boolean ignoreIfExists) throws IOException {
        if (ignoreIfExists && destination.exists()) {
            return;
        }

        Path target = destination.toPath();
        Files.copy(resource, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
