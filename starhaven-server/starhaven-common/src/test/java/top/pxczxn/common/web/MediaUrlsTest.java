package top.pxczxn.common.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MediaUrlsTest {

    @Test
    void resolveJoinsPublicUrlAndPath() {
        assertEquals("http://127.0.0.1:5567/houses/a.png",
                MediaUrls.resolve("http://127.0.0.1:5567", "/houses/a.png"));
    }

    @Test
    void resolveKeepsAbsoluteUrl() {
        assertEquals("https://cdn.example/a.png",
                MediaUrls.resolve("http://127.0.0.1:5567", "https://cdn.example/a.png"));
    }

    @Test
    void resolveLeavesBlank() {
        assertNull(MediaUrls.resolve("http://127.0.0.1:5567", null));
        assertEquals("", MediaUrls.resolve("http://127.0.0.1:5567", ""));
    }
}
