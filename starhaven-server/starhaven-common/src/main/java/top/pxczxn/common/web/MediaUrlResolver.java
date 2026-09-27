package top.pxczxn.common.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MediaUrlResolver {

    @Value("${starhaven.public-url:http://127.0.0.1:5567}")
    private String publicUrl;

    public String resolve(String path) {
        return MediaUrls.resolve(publicUrl, path);
    }
}
