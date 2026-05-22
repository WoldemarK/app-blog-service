package ru.yandex.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
public class CorsFilter implements Filter {

    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, PATCH";
    private static final String ALLOWED_HEADERS = "Origin, Content-Type, Accept, Authorization, X-Requested-With, Access-Control-Request-Method, Access-Control-Request-Headers";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        httpResponse.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}

