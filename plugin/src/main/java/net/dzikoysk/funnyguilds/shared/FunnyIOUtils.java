package net.dzikoysk.funnyguilds.shared;

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import panda.std.Result;
import panda.std.stream.PandaStream;
import panda.utilities.IOUtils;

public final class FunnyIOUtils {

    private FunnyIOUtils() {
    }

    public static File createFile(String fileName, boolean isDirectory) {
        return createFile(new File(fileName), isDirectory);
    }

    public static File createFile(File file, boolean isDirectory) {
        if (!file.exists()) {
            try {
                if (isDirectory) {
                    if (!file.mkdirs()) {
                        throw new IOException("Could not create directory");
                    }

                    return file;
                }

                if (!file.getParentFile().mkdirs()) {
                    throw new IOException("Could not create parent directories");
                }

                if (!file.createNewFile()) {
                    throw new IOException("Could not create file");
                }
            }
            catch (IOException exception) {
                FunnyGuilds.getPluginLogger().error("Could not initialize file: " + file.getAbsolutePath(), exception);
            }
        }

        return file;
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
            if (contents != null && contents.length != 0) {
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
                Result<String, IOException> conversion = IOUtils.convertStreamToString(input, Charset.forName(encoding));
                if (conversion.isOk()) {
                    return conversion.get();
                }

                throw conversion.getError();
            }
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().update("Connection to the server (" + urlString + ") failed!");
            FunnyGuilds.getPluginLogger().update("Reason: " + Throwables.getStackTraceAsString(ex));
        }

        return "";
    }

}
