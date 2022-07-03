package com.synology.hajubal.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.slf4j.MDC.clear;
import static org.slf4j.MDC.put;

@Slf4j
@Component
@WebFilter(urlPatterns = "*")
@Order(0)
public class LogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        put("traceId", UUID.randomUUID().toString());

//        if (isAsyncDispatch(request)) {
//            filterChain.doFilter(request, response);
//        } else {
            doFilterWrapped(new RequestWrapper(request), new ResponseWrapper(response), filterChain);
//        }

        clear();
    }

    protected void doFilterWrapped(RequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        try {
            logRequest(request);

            filterChain.doFilter(request, response);
        } finally {
            logResponse(response);

            response.copyBodyToResponse();
        }
    }

    private static void logRequest(RequestWrapper request) throws IOException {
        String queryString = request.getQueryString();

        log.info("======================================================================");
        log.info("Request : {} uri=[{}] content-type=[{}]", request.getMethod()
                , queryString == null ? request.getRequestURI() : request.getRequestURI() + queryString
                , request.getContentType());

        logPayload("Request", request.getContentType(), request.getInputStream());

        log.info("======================================================================");
    }

    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        log.info("======================================================================");
        log.info("Response");

        logPayload("", response.getContentType(), response.getContentInputStream());

        log.info("======================================================================");
    }

    private static void logPayload(String prefix, String contentType, InputStream inputStream) throws IOException {
        boolean visible = isVisible(MediaType.valueOf(contentType == null ? "application/json" : contentType));

        if (visible) {
            byte[] content = StreamUtils.copyToByteArray(inputStream);

            if (content.length > 0) {
                log.info("{} Payload: {}", prefix, new String(content));
            }
        } else {
            log.info("{} Payload: Binary Content", prefix);
        }
    }

    private static boolean isVisible(MediaType mediaType) {
        final List<MediaType> VISIBLE_TYPES = Arrays.asList(
                MediaType.valueOf("text/*")
                , MediaType.APPLICATION_FORM_URLENCODED
                , MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
                , MediaType.valueOf("application/*+json")
                , MediaType.valueOf("application/*+xml")
                , MediaType.MULTIPART_FORM_DATA);

        return VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
    }
}
