package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeoutException;

public class IOUtils {

    public static File initizalize(File file, boolean b) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (b) {
                    file.createNewFile();
                }
                else {
                    file.mkdir();
                }
            } catch (IOException e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
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
        } catch (TimeoutException e) {
            FunnyGuilds.info(e.getMessage());
        } catch (Exception e) {
            FunnyGuilds.warning(e.getMessage());
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
        } catch (Exception e) {
            e.printStackTrace();
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static String toString(InputStream in, String encoding) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return new String(baos.toByteArray(), encoding);
    }

}
