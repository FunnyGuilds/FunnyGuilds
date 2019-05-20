package net.dzikoysk.funnyguilds.util.telemetry;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;

public class FunnyTelemetry {
    private static final Gson   gson          = new Gson();
    public static final  String URL           = "http://funnytelemetry.mrgregorix.net";
    public static final  String FUNNYBIN_POST = URL + "/funnybin/api/post";

    public static FunnybinResponse postToFunnybin(String paste) throws IOException {
        return sendPost(FUNNYBIN_POST, paste, FunnybinResponse.class);
    }

    private static <T> T sendPost(String url, String body, Class<T> response) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        connection.addRequestProperty("Content-Type", "application/yaml");
        connection.addRequestProperty("User-Agent", "FunnyGuilds");
        connection.setRequestProperty("Content-Length", String.valueOf(bodyBytes.length));

        connection.getOutputStream().write(bodyBytes);
        return gson.fromJson(IOUtils.toString(connection.getInputStream(), "UTF-8"), response);
    }
}
