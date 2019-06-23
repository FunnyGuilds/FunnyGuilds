package net.dzikoysk.funnyguilds.util.telemetry;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;
import org.diorite.utils.network.DioriteURLUtils;

/**
 * TODO: Move this to a separate library.
 */
public class FunnyTelemetry {

    private static final Gson   gson                 = new Gson();
    public static final  String URL                  = "https://funnytelemetry.dzikoysk.net";
    public static final  String FUNNYBIN_POST        = URL + "/funnybin/api/post";
    public static final  String FUNNYBIN_POST_BUNDLE = URL + "/funnybin/api/bundle/post";

    public static FunnybinResponse postToFunnybin(String paste, PasteType pasteType, String tag) throws IOException {
        return sendPost(FUNNYBIN_POST + "?type=" + pasteType.toString() + "&tag=" + DioriteURLUtils.encodeUTF8(tag), paste, FunnybinResponse.class);
    }

    public static FunnybinResponse createBundle(List<String> pastes) throws IOException {
        if (pastes.isEmpty()) {
            return null;
        }

        Iterator<String> iterator = pastes.iterator();
        StringBuilder url = new StringBuilder(DioriteURLUtils.createQueryElement("paste", iterator.next()));

        while (iterator.hasNext()) {
            url.append("&");
            DioriteURLUtils.addQueryElement("paste", iterator.next(), url);
        }

        return sendPost(FUNNYBIN_POST_BUNDLE + "?" + url.toString(), "", FunnybinResponse.class);
    }

    private static <T> T sendPost(String url, String body, Class<T> response) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        connection.addRequestProperty("User-Agent", "FunnyGuilds");
        connection.addRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("Content-Length", String.valueOf(bodyBytes.length));

        connection.getOutputStream().write(bodyBytes);
        return gson.fromJson(IOUtils.toString(connection.getInputStream(), "UTF-8"), response);
    }
}
