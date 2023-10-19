package es.gobcan.istac.edatos.external.users.web.security.filter;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

public class CaptchaFilter extends OncePerRequestFilter {

    @Context
    private HttpServletRequest  request;

    private MetadataProperties metadataProperties;

    private final ObjectMapper objectMapper;

    public CaptchaFilter(MetadataProperties metadataProperties, ObjectMapper objectMapper) {
        this.metadataProperties = metadataProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        UriBuilder urlBuilder = UriBuilder.fromUri(metadataProperties.getCaptchaUrl());
        urlBuilder.path("api/validate");
        for(String paramKey : Collections.list(req.getParameterNames())) {
            urlBuilder.queryParam(paramKey, req.getParameter(paramKey));
        }
        URI url = urlBuilder.build();
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForEntity(url, Object.class);
            chain.doFilter(req, res);
        } catch (HttpClientErrorException e) {
            res.setStatus(e.getRawStatusCode());
            res.getWriter().print(e.getResponseBodyAsString());
            res.getWriter().flush();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !StringUtils.equalsAny(path, "/api/login", "/api/account/signup");
    }
}

