package top.pxczxn.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BearerTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ") && request.getHeader("satoken") == null) {
            String token = auth.substring(7);
            filterChain.doFilter(new HeaderRequestWrapper(request, "satoken", token), response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    static class HeaderRequestWrapper extends HttpServletRequestWrapper {
        private final String name;
        private final String value;

        HeaderRequestWrapper(HttpServletRequest request, String name, String value) {
            super(request);
            this.name = name;
            this.value = value;
        }

        @Override
        public String getHeader(String name) {
            if (this.name.equalsIgnoreCase(name)) {
                return value;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (this.name.equalsIgnoreCase(name)) {
                return Collections.enumeration(List.of(value));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            if (names.stream().noneMatch(item -> item.equalsIgnoreCase(this.name))) {
                names.add(this.name);
            }
            return Collections.enumeration(names);
        }
    }
}
