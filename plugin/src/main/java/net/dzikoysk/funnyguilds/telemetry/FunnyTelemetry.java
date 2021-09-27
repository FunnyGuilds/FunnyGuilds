package net.dzikoysk.funnyguilds.telemetry;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import net.dzikoysk.funnyguilds.shared.IOUtils;

/**
 * TODO: Move this to a separate library.
 */
public class FunnyTelemetry {

    private FunnyTelemetry() {}

    private static final Gson gson = new Gson();
    public static final String URL = "https://funnytelemetry.dzikoysk.net";
    public static final String FUNNYBIN_POST = URL + "/funnybin/api/post";
    public static final String FUNNYBIN_POST_BUNDLE = URL + "/funnybin/api/bundle/post";

    public static FunnybinResponse postToFunnybin(String paste, PasteType pasteType, String tag) throws IOException {
        return sendPost(FUNNYBIN_POST + "?type=" + pasteType + "&tag=" + encodeUTF8(tag), paste, FunnybinResponse.class);
    }

    public static FunnybinResponse createBundle(List<String> pastes) throws IOException {
        if (pastes.isEmpty()) {
            return null;
        }

        Iterator<String> iterator = pastes.iterator();
        StringBuilder url = new StringBuilder(createQueryElement("paste", iterator.next()));

        while (iterator.hasNext()) {
            url.append("&");
            addQueryElement("paste", iterator.next(), url);
        }

        return sendPost(FUNNYBIN_POST_BUNDLE + "?" + url, "", FunnybinResponse.class);
    }

    private static <T> T sendPost(String url, String body, Class<T> response) throws IOException {
        System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

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

    private static String encodeUTF8(String str) throws UnsupportedEncodingException {
        if (str == null) {
            return "";
        }
        return URLEncoder.encode(str, "UTF-8");
    }

    private static String createQueryElement(String key, String value) throws UnsupportedEncodingException {
        String result = encodeUTF8(key);
        if (value != null) {
            result += "=" + encodeUTF8(value);
        }
        return result;
    }

    private static StringBuilder addQueryElement(String key, String value, StringBuilder builder) throws UnsupportedEncodingException {
        builder.append(encodeUTF8(key));
        if (value != null) {
            builder.append('=');
            builder.append(encodeUTF8(value));
        }
        return builder;
    }
}
