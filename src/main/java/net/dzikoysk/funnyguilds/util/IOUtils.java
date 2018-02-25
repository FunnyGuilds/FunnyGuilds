package net.dzikoysk.funnyguilds.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public final class IOUtils {

    public static File initialize(File file, boolean b) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (b) {
                    file.createNewFile();
                } else {
                    file.mkdir();
                }
            } catch (IOException e) {
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
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
        } catch (Exception e) {
            FunnyLogger.update("Connection to the update server (" + s + ") failed!");
        } finally {
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
                } else {
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

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private IOUtils() {}
}
