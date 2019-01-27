package org.trv.alex.unshortenurl.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class NetworkTest {

    private static final List<String> CORRECT_URLS = Arrays.asList(
            "http://example.com",
            "https://example.com",
            "http://example",
            "https://example",
            "http://com",
            "https://com",
            "http://xam",
            "https://xam"
    );

    private static final List<String> INCORRECT_URLS = Arrays.asList(
            "htt://example.com",
            "htts://example.com",
            "htp://example",
            "htps://example",
            "htp://com",
            "htps://com",
            "",
            null,
            "null",
            "htp://xam",
            "htps://xam"
    );

    @Test
    public void shouldNotAddHttpScheme() {
        assertThat(CORRECT_URLS.stream()
                        .map(Network::addHttpScheme)
                        .collect(Collectors.toList()),
                is(CORRECT_URLS));
    }

    @Test
    public void shouldAddHttpScheme() {
        assertThat(INCORRECT_URLS.stream()
                        .map(Network::addHttpScheme)
                        .collect(Collectors.toList()),
                everyItem(startsWith("http://")));
    }

    @Test
    public void shouldBeHttpUrl() {
        assertThat(CORRECT_URLS.stream()
                        .map(Network::isHttpUrl)
                        .collect(Collectors.toList()),
                everyItem(is(true)));
    }

    @Test
    public void shouldNotBeHttpUrl() {
        assertThat(INCORRECT_URLS.stream()
                        .map(Network::isHttpUrl)
                        .collect(Collectors.toList()),
                everyItem(is(false)));
    }
}