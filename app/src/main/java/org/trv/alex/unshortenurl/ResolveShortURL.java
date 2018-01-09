package org.trv.alex.unshortenurl;

import android.webkit.URLUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class provides getting long URL from short. Short URL is an URL
 * which has redirect to another URL with response code 301 or 302.
 * In other case URL considered long.
 */
public class ResolveShortURL {

    // request user-agent header
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0";

    // response Location header
    private static final String LOCATION = "Location";

    private static final String HTTP_SCHEME = "http://";

    private ResolveShortURL() {
    }

    /**
     * Get URL from Location header if response code is 301 or 302.
     * If any other response code received then return {@code null}.
     * @param   spec the URL.
     * @return  Long URL if Location header exists, in other case {@code null}.
     * @throws  IOException if URL is incorrect or network connection disconnected.
     */
    private static String getLongURL(String spec) throws IOException {
        String longUrl = null;

        spec = addHttpScheme(spec);

        URL url = new URL(spec);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(5000);

        int responseCode = urlConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM
                || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            longUrl = urlConnection.getHeaderField(LOCATION);
            if (!URLUtil.isHttpUrl(longUrl) && !URLUtil.isHttpsUrl(longUrl)) {
                longUrl = new URL(url, longUrl).toString();
            }
        }

        return longUrl;
    }

    /**
     * Get long URL from short URL.
     * @param   spec the URL.
     * @return  the {@code List} with one element or empty if {@code spec} URL is not short.
     * @throws  IOException if URL is incorrect or network connection disconnected.
     */
    public static List<String> getOneLevelLongURL(String spec) throws IOException {
        List<String> urls = new ArrayList<>();
        String shortURL;

        if ((shortURL = getLongURL(spec)) != null) {
            urls.add(shortURL);
        }

        return urls;
    }

    /**
     * Get long URL from short URL. If gotten URL is short then try to
     * get short URL from gotten and so on until next URL has become long.
     * @param   spec the URL.
     * @return  the {@code List} with one or more elements (all gotten URLs), or
     *          empty if {@code spec} URL is not short.
     * @throws  IOException if URL is incorrect or network connection disconnected.
     */
    public static List<String> getDeepLongURL(String spec) throws IOException {
        List<String> listOfUrls = new ArrayList<>();
        String urlString = spec;

        while ((urlString = getLongURL(urlString)) != null) {
            listOfUrls.add(urlString);
        }

        return listOfUrls;

    }

    /**
     * Adds at the begging of {@code spec} http protocol if it doesn't contain http(s),
     * otherwise no changes are made.
     * @param spec the URL.
     * @return @{code spec} with http protocol.
     */
    public static String addHttpScheme(String spec) {
        if (!URLUtil.isHttpUrl(spec) && !URLUtil.isHttpsUrl(spec)) {
            return HTTP_SCHEME + spec;
        }
        return spec;
    }

    /**
     * Returns true or false whether Internet is available or not.
     * @return true - available, false - unavailable.
     */
    public static boolean isOnline() {
        int timeoutMs = 2000;
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress("8.8.8.8", 53);
        try {
            socket.connect(address, timeoutMs);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
