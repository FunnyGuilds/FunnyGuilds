package net.dzikoysk.funnyguilds.shared;

import com.google.common.base.Throwables;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import net.dzikoysk.funnyguilds.FunnyGuilds;

public final class IOUtils {

    private IOUtils() {}

    public static File initialize(File file, boolean b) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (b) {
                    file.createNewFile();
                }
                else {
                    file.mkdir();
                }
            }
            catch (IOException ex) {
                FunnyGuilds.getPluginLogger().error("Could not initialize file: " + file.getAbsolutePath(), ex);
            }
        }

        return file;
    }

    public static String getContent(String s) {
        String body = null;
        InputStream in = null;

        try {
            URLConnection con = new URL(s).openConnection();

            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            in = con.getInputStream();

            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;

            body = IOUtils.toString(in, encoding);
            in.close();
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().update("Connection to the update server (" + s + ") failed!");
            FunnyGuilds.getPluginLogger().update("Reason: " + Throwables.getStackTraceAsString(ex));
        }
        finally {
            close(in);
        }

        return body;
    }

    public static File getFile(String s, boolean folder) {
        File file = new File(s);

        try {
            if (!file.exists()) {
                if (folder) {
                    file.mkdirs();
                }
                else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            }
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("The file could not be created!", exception);
        }

        return file;
    }

    public static void delete(File f) {
        if (!f.exists()) {
            return;
        }

        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }

        if (!f.delete()) {
            try {
                throw new FileNotFoundException("Failed to delete file: " + f);
            }
            catch (FileNotFoundException exception) {
                FunnyGuilds.getPluginLogger().error("The file could not be deleted!", exception);
            }
        }
    }

    public static String toString(InputStream in, String encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int len;

        while ((len = in.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }

        return baos.toString(encoding);
    }

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        }
        catch (IOException exception) {
            FunnyGuilds.getPluginLogger().error("Could not close IO", exception);
        }
    }

}
