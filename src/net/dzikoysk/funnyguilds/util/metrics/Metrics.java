package net.dzikoysk.funnyguilds.util.metrics;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitTask;

public class Metrics {

    private final static int REVISION = 7;
    private static final String BASE_URL = "http://report.mcstats.org";
    private static final String REPORT_URL = "/plugin/%s";
    private static final int PING_INTERVAL = 10;
    private final Plugin plugin;
    private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet<Graph>());
    private final YamlConfiguration configuration;
    private final File configurationFile;
    private final String guid;
    private final boolean debug;
    private final Object optOutLock = new Object();
    private volatile BukkitTask task = null;

    public Metrics(Plugin plugin) throws IOException {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        this.plugin = plugin;

        // load the config
        configurationFile = getConfigFile();
        configuration = YamlConfiguration.loadConfiguration(configurationFile);

        // add some defaults
        configuration.addDefault("opt-out", false);
        configuration.addDefault("guid", UUID.randomUUID().toString());
        configuration.addDefault("debug", false);

        // Do we need to create the file?
        if (configuration.get("guid", null) == null) {
            configuration.options().header("http://mcstats.org").copyDefaults(true);
            configuration.save(configurationFile);
        }

        // Load the guid then
        guid = configuration.getString("guid");
        debug = configuration.getBoolean("debug", false);
    }

    public Graph createGraph(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Graph name cannot be null");
        }

        // Construct the graph object
        final Graph graph = new Graph(name);

        // Now we can add our graph
        graphs.add(graph);

        // and return back
        return graph;
    }

    public void addGraph(final Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }

        graphs.add(graph);
    }

    public boolean start() {
        synchronized (optOutLock) {
            // Did we opt out?
            if (isOptOut()) {
                return false;
            }

            // Is metrics already running?
            if (task != null) {
                return true;
            }

            // Begin hitting the server with glorious data
            task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

                private boolean firstPost = true;

                public void run() {
                    try {
                        // This has to be synchronized or it can collide with the disable method.
                        synchronized (optOutLock) {
                            // Disable Task, if it is running and the server owner decided to opt-out
                            if (isOptOut() && task != null) {
                                task.cancel();
                                task = null;
                                // Tell all plotters to stop gathering information.
                                for (Graph graph : graphs) {
                                    graph.onOptOut();
                                }
                            }
                        }

                        // We use the inverse of firstPost because if it is the first time we are posting,
                        // it is not a interval ping, so it evaluates to FALSE
                        // Each time thereafter it will evaluate to TRUE, i.e PING!
                        postPlugin(!firstPost);

                        // After the first post we set firstPost to false
                        // Each post thereafter will be a ping
                        firstPost = false;
                    } catch (IOException e) {
                        if (debug) {
                            Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
                        }
                    }
                }
            }, 0, PING_INTERVAL * 1200);

            return true;
        }
    }

    public boolean isOptOut() {
        synchronized (optOutLock) {
            try {
                // Reload the metrics file
                configuration.load(getConfigFile());
            } catch (IOException ex) {
                if (debug) {
                    Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
                }
                return true;
            } catch (InvalidConfigurationException ex) {
                if (debug) {
                    Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
                }
                return true;
            }
            return configuration.getBoolean("opt-out", false);
        }
    }

    public void enable() throws IOException {
        // This has to be synchronized or it can collide with the check in the task.
        synchronized (optOutLock) {
            // Check if the server owner has already set opt-out, if not, set it.
            if (isOptOut()) {
                configuration.set("opt-out", false);
                configuration.save(configurationFile);
            }

            // Enable Task, if it is not running
            if (task == null) {
                start();
            }
        }
    }

    public void disable() throws IOException {
        // This has to be synchronized or it can collide with the check in the task.
        synchronized (optOutLock) {
            // Check if the server owner has already set opt-out, if not, set it.
            if (!isOptOut()) {
                configuration.set("opt-out", true);
                configuration.save(configurationFile);
            }

            // Disable Task, if it is running
            if (task != null) {
                task.cancel();
                task = null;
            }
        }
    }

    public File getConfigFile() {
        // I believe the easiest way to get the base folder (e.g craftbukkit set via -P) for plugins to use
        // is to abuse the plugin object we already have
        // plugin.getDataFolder() => base/plugins/PluginA/
        // pluginsFolder => base/plugins/
        // The base is not necessarily relative to the startup directory.
        File pluginsFolder = plugin.getDataFolder().getParentFile();

        // return => base/plugins/PluginMetrics/config.yml
        return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
    }

    private void postPlugin(final boolean isPing) throws IOException {
        // Server software specific section
        PluginDescriptionFile description = plugin.getDescription();
        String pluginName = description.getName();
        boolean onlineMode = Bukkit.getServer().getOnlineMode(); // TRUE if online mode is enabled
        String pluginVersion = description.getVersion();
        String serverVersion = Bukkit.getVersion();
        int playersOnline = Bukkit.getServer().getOnlinePlayers().length;

        // END server software specific section -- all code below does not use any code outside of this class / Java

        // Construct the post data
        StringBuilder json = new StringBuilder(1024);
        json.append('{');

        // The plugin's description file containg all of the plugin data such as name, version, author, etc
        appendJSONPair(json, "guid", guid);
        appendJSONPair(json, "plugin_version", pluginVersion);
        appendJSONPair(json, "server_version", serverVersion);
        appendJSONPair(json, "players_online", Integer.toString(playersOnline));

        // New data as of R6
        String osname = System.getProperty("os.name");
        String osarch = System.getProperty("os.arch");
        String osversion = System.getProperty("os.version");
        String java_version = System.getProperty("java.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        // normalize os arch .. amd64 -> x86_64
        if (osarch.equals("amd64")) {
            osarch = "x86_64";
        }

        appendJSONPair(json, "osname", osname);
        appendJSONPair(json, "osarch", osarch);
        appendJSONPair(json, "osversion", osversion);
        appendJSONPair(json, "cores", Integer.toString(coreCount));
        appendJSONPair(json, "auth_mode", onlineMode ? "1" : "0");
        appendJSONPair(json, "java_version", java_version);

        // If we're pinging, append it
        if (isPing) {
            appendJSONPair(json, "ping", "1");
        }

        if (graphs.size() > 0) {
            synchronized (graphs) {
                json.append(',');
                json.append('"');
                json.append("graphs");
                json.append('"');
                json.append(':');
                json.append('{');

                boolean firstGraph = true;

                final Iterator<Graph> iter = graphs.iterator();

                while (iter.hasNext()) {
                    Graph graph = iter.next();

                    StringBuilder graphJson = new StringBuilder();
                    graphJson.append('{');

                    for (Plotter plotter : graph.getPlotters()) {
                        appendJSONPair(graphJson, plotter.getColumnName(), Integer.toString(plotter.getValue()));
                    }

                    graphJson.append('}');

                    if (!firstGraph) {
                        json.append(',');
                    }

                    json.append(escapeJSON(graph.getName()));
                    json.append(':');
                    json.append(graphJson);

                    firstGraph = false;
                }

                json.append('}');
            }
        }

        // close json
        json.append('}');

        // Create the url
        URL url = new URL(BASE_URL + String.format(REPORT_URL, urlEncode(pluginName)));

        // Connect to the website
        URLConnection connection;

        // Mineshafter creates a socks proxy, so we can safely bypass it
        // It does not reroute POST requests so we need to go around it
        if (isMineshafterPresent()) {
            connection = url.openConnection(Proxy.NO_PROXY);
        } else {
            connection = url.openConnection();
        }


        byte[] uncompressed = json.toString().getBytes();
        byte[] compressed = gzip(json.toString());

        // Headers
        connection.addRequestProperty("User-Agent", "MCStats/" + REVISION);
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("Content-Encoding", "gzip");
        connection.addRequestProperty("Content-Length", Integer.toString(compressed.length));
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");

        connection.setDoOutput(true);

        if (debug) {
            System.out.println("[Metrics] Prepared request for " + pluginName + " uncompressed=" + uncompressed.length + " compressed=" + compressed.length);
        }

        // Write the data
        OutputStream os = connection.getOutputStream();
        os.write(compressed);
        os.flush();

        // Now read the response
        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = reader.readLine();

        // close resources
        os.close();
        reader.close();

        if (response == null || response.startsWith("ERR") || response.startsWith("7")) {
            if (response == null) {
                response = "null";
            } else if (response.startsWith("7")) {
                response = response.substring(response.startsWith("7,") ? 2 : 1);
            }

            throw new IOException(response);
        } else {
            // Is this the first update this hour?
            if (response.equals("1") || response.contains("This is your first update this hour")) {
                synchronized (graphs) {
                    final Iterator<Graph> iter = graphs.iterator();

                    while (iter.hasNext()) {
                        final Graph graph = iter.next();

                        for (Plotter plotter : graph.getPlotters()) {
                            plotter.reset();
                        }
                    }
                }
            }
        }
    }

    public static byte[] gzip(String input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = null;

        try {
            gzos = new GZIPOutputStream(baos);
            gzos.write(input.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzos != null) try {
                gzos.close();
            } catch (IOException ignore) {
            }
        }

        return baos.toByteArray();
    }

    private boolean isMineshafterPresent() {
        try {
            Class.forName("mineshafter.MineServer");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void appendJSONPair(StringBuilder json, String key, String value) throws UnsupportedEncodingException {
        boolean isValueNumeric = false;

        try {
            if (value.equals("0") || !value.endsWith("0")) {
                Double.parseDouble(value);
                isValueNumeric = true;
            }
        } catch (NumberFormatException e) {
            isValueNumeric = false;
        }

        if (json.charAt(json.length() - 1) != '{') {
            json.append(',');
        }

        json.append(escapeJSON(key));
        json.append(':');

        if (isValueNumeric) {
            json.append(value);
        } else {
            json.append(escapeJSON(value));
        }
    }

    private static String escapeJSON(String text) {
        StringBuilder builder = new StringBuilder();

        builder.append('"');
        for (int index = 0; index < text.length(); index++) {
            char chr = text.charAt(index);

            switch (chr) {
                case '"':
                case '\\':
                    builder.append('\\');
                    builder.append(chr);
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                default:
                    if (chr < ' ') {
                        String t = "000" + Integer.toHexString(chr);
                        builder.append("\\u" + t.substring(t.length() - 4));
                    } else {
                        builder.append(chr);
                    }
                    break;
            }
        }
        builder.append('"');

        return builder.toString();
    }

    private static String urlEncode(final String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }

    public static class Graph {

        private final String name;
        private final Set<Plotter> plotters = new LinkedHashSet<Plotter>();

        private Graph(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void addPlotter(final Plotter plotter) {
            plotters.add(plotter);
        }

        public void removePlotter(final Plotter plotter) {
            plotters.remove(plotter);
        }

        public Set<Plotter> getPlotters() {
            return Collections.unmodifiableSet(plotters);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof Graph)) {
                return false;
            }

            final Graph graph = (Graph) object;
            return graph.name.equals(name);
        }

        protected void onOptOut() {
        }
    }

    public static abstract class Plotter {

        private final String name;

        public Plotter() {
            this("Default");
        }

        public Plotter(final String name) {
            this.name = name;
        }

        public abstract int getValue();

        public String getColumnName() {
            return name;
        }

        public void reset() {
        }

        @Override
        public int hashCode() {
            return getColumnName().hashCode();
        }

        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof Plotter)) {
                return false;
            }

            final Plotter plotter = (Plotter) object;
            return plotter.name.equals(name) && plotter.getValue() == getValue();
        }
    }
}