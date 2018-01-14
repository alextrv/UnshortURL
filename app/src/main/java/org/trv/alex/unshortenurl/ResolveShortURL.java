package org.trv.alex.unshortenurl;

import android.net.Uri;
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

    // exception sites constants
    private static final String VK_HOST = "vk.com";
    private static final String VK_QUERY_PARAM_NAME = "to";

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

        String exceptionURL = getURLException(url);

        if (exceptionURL != null) {
            return exceptionURL;
        }

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
    public static List<String> getOneLevelLongURL(String spec) {
        List<String> urls = new ArrayList<>();
        String shortURL;

        try {
            if ((shortURL = getLongURL(spec)) != null) {
                urls.add(shortURL);
            }
        } catch (IOException e) {
        }

        return urls;
    }

    /**
     * Get long URL from short URL. If gotten URL is short then try to
     * get short URL from gotten and so on until next URL has become long.
     * @param   spec the URL.
     * @return  the {@code List} with one or more elements (all gotten URLs), or
     *          empty if {@code spec} URL is not short.
     */
    public static List<String> getDeepLongURL(String spec) {
        List<String> listOfUrls = new ArrayList<>();
        String urlString = spec;

        try {
            while ((urlString = getLongURL(urlString)) != null) {
                listOfUrls.add(urlString);
            }
        } catch (IOException e) {
        }

        return listOfUrls;
    }

    /**
     * Adds at the beginning of {@code spec} http protocol if it doesn't contain http(s),
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

    /**
     * Handles sites which don't use redirect codes 301 and 302. For example, they may use
     * redirect in JavaScript.
     * @param url the URL.
     * @return long URL. If can't find, returns {@code null}.
     */
    private static String getURLException(URL url) {
        if (url.getHost().equals(VK_HOST)) {
            Uri uri = Uri.parse(url.toString());
            return uri.getQueryParameter(VK_QUERY_PARAM_NAME);
        }

        return null;
    }

}
