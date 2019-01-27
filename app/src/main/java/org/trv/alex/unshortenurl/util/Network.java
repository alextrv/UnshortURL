package org.trv.alex.unshortenurl.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * This class provides utility methods to work with network
 */
public final class Network {

    private static final String HTTP_SCHEME = "http://";
    private static final String HTTPS_SCHEME = "https://";

    private Network() {
    }

    /**
     * Adds at the beginning of {@code spec} http protocol if it doesn't contain http(s),
     * otherwise no changes are made.
     *
     * @param spec the URL.
     * @return @{code spec} with http protocol.
     */
    public static String addHttpScheme(String spec) {
        if (!isHttpUrl(spec)) {
            return HTTP_SCHEME + spec;
        }
        return spec;
    }

    /**
     * Checks whether the url contains http(s):// protocol or not.
     *
     * @param url the Url
     * @return true - contains http(s):// at the beginning, false - if doesn't
     */
    public static boolean isHttpUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return url.startsWith(HTTP_SCHEME) || url.startsWith(HTTPS_SCHEME);
    }

    /**
     * Returns true or false whether Internet is available or not.
     *
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
