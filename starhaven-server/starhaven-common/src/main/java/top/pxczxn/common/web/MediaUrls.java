package top.pxczxn.common.web;

import org.springframework.util.StringUtils;

public final class MediaUrls {

    private MediaUrls() {
    }

    public static String resolve(String publicUrl, String path) {
        if (!StringUtils.hasText(path)) {
            return path;
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        String base = StringUtils.hasText(publicUrl) ? publicUrl : "http://127.0.0.1:5567";
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return path.startsWith("/") ? base + path : base + "/" + path;
    }
}
