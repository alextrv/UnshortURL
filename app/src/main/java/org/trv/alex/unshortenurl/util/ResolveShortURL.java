package org.trv.alex.unshortenurl.util;

import android.net.Uri;

import org.trv.alex.unshortenurl.model.HistoryURL;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class provides getting long URL from short. Short URL is an URL
 * which has redirect to another URL with response code 301 or 302.
 * In other case URL considered long.
 */
public final class ResolveShortURL {

    // exception sites constants
    private static final String VK_HOST = "vk.com";
    private static final String VK_QUERY_PARAM_NAME = "to";

    // request user-agent header
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0";

    // response Location header
    private static final String LOCATION = "Location";

    private ResolveShortURL() {
    }

    /**
     * Gets URL from Location header if response code is 301 or 302.
     * If any other response code received then return {@code null}.
     *
     * @param spec the URL.
     * @return Long URL if Location header exists, in other case {@code null}.
     * @throws IOException if URL is incorrect or network connection disconnected.
     */
    private static String getLongURL(String spec) throws IOException {
        String longUrl = null;

        spec = Network.addHttpScheme(spec);

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
            if (!Network.isHttpUrl(longUrl)) {
                longUrl = new URL(url, longUrl).toString();
            }
        }

        return longUrl;
    }

    /**
     * Gets long URL from short URL. If gotten URL is short then try to
     * get short URL from gotten and so on until next URL has become long.
     *
     * @param spec the URL.
     * @return the unmodifiable {@code List} with one or more elements (all gotten URLs) or
     * empty if {@code spec} URL is not short. If any error occurred returns {@code null}.
     */
    public static List<HistoryURL> resolveURL(String spec, boolean deep) {
        List<HistoryURL> urls = new ArrayList<>();
        String urlStr = spec;

        try {
            if (deep) {
                while ((urlStr = getLongURL(urlStr)) != null) {
                    urls.add(new HistoryURL(0, urlStr, 0));
                }
                urls = Utils.unmodifiableList(urls);
            } else {
                if ((urlStr = getLongURL(spec)) != null) {
                    urls.add(new HistoryURL(0, urlStr, 0));
                }
                urls = Utils.unmodifiableList(urls);
            }
        } catch (IOException e) {
            urls = null;
        }

        return urls;
    }

    /**
     * Handles sites which don't use redirect codes 301 and 302. For example, they may use
     * redirect in JavaScript.
     *
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
