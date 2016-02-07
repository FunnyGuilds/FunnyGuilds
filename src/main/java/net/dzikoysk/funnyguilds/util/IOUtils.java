package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeoutException;

public class IOUtils {

    public static File initialize(File file, boolean folder) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (folder) file.mkdir();
                else file.createNewFile();
            } catch (IOException e) {
                if (FunnyGuilds.exception(e.getCause())) e.printStackTrace();
            }
        }
        return file;
    }

    public static String getContent(String s) {
        String body = null;
        try {
            URL url = new URL(s);
            URLConnection con = url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            body = IOUtils.toString(in, encoding);
            in.close();
        } catch (TimeoutException e) {
            FunnyGuilds.info(e.getMessage());
        } catch (Exception e) {
            FunnyGuilds.warning(e.getMessage());
        }
        return body;
    }

    public static File getFile(String s, boolean folder) {
        return initialize(new File(s), folder);
    }

    public static int countFiles(String s) {
        File folder = new File(s);
        File[] files = folder.listFiles();
        return files == null ? 0 : files.length;
    }

    public static void delete(File f) {
        if (!f.exists()) return;
        if (f.isDirectory())
            for (File c : f.listFiles()) delete(c);
        if (!f.delete())
            try {
                throw new FileNotFoundException("Failed to delete file: " + f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    }

    public static String toString(InputStream in, String encoding) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1)
            baos.write(buf, 0, len);
        in.close();
        return new String(baos.toByteArray(), encoding);
    }

}
