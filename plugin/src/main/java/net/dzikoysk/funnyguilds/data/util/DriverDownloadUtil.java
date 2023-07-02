package net.dzikoysk.funnyguilds.data.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;

public class DriverDownloadUtil {

    public static File JAR_FILE = new File(FunnyGuilds.getInstance().getDataFolder(), "/lib/mysql-driver.jar");


    public static void loadDependency(String version) throws Exception {

        try {
            loadDriver();
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ignored) {


            try {
                downloadDriver(version);
            } catch (Exception e) {
                throw new SQLException("Cant download the driver dependencie of $name");
            }
            try {
                loadDriver();
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (Exception e) {
                JAR_FILE.delete();
                throw new SQLException("Cant load the driver dependencies of $name");
            }
        }
    }

    private static void loadDriver() {
        try {
            URL url = JAR_FILE.toURI().toURL();
            URLClassLoader classLoader = (URLClassLoader) DriverDownloadUtil.class.getClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void downloadDriver(String version) {
        try {
            InputStream input = new URL("https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/" + version + "/mysql-connector-j-" + version + ".jar").openStream();

            if (JAR_FILE.exists()) {
                if (JAR_FILE.isDirectory()) {
                    throw new IOException("File '" + JAR_FILE.getName() + "' is a directory");
                }

                if (!JAR_FILE.canWrite()) {
                    throw new IOException("File '" + JAR_FILE.getName() + "' cannot be written");
                }
            } else {
                File parent = JAR_FILE.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new IOException("File '" + JAR_FILE.getName() + "' could not be created");
                }
            }

            FileOutputStream output = new FileOutputStream(JAR_FILE);

            byte[] buffer = new byte[4096];
            int n = input.read(buffer);
            while (-1 != n) {
                output.write(buffer, 0, n);

                n = input.read(buffer);
            }

            input.close();
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
