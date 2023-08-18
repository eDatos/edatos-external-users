package es.gobcan.istac.edatos.external.users.rest.external.resources;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.service.DisabledTokenService;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

@Controller
public class AuthenticationResource {

    @Autowired
    private MetadataConfigurationService metadataService;

    @Autowired
    private DisabledTokenService disabledTokenService;

    @Autowired
    private MetadataProperties metadataProperties;

    @GetMapping("/api/authenticate")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<Void> authenticate(@RequestParam String origin, @RequestParam(required = false, defaultValue = "false") boolean force,
        @CookieValue(value = "authenticationtoken", required = false) String token, HttpServletRequest request) throws UnsupportedEncodingException, MalformedURLException {

        URL endpointURL = new URL(request.getRequestURL().toString());
        String externalUsersDomain = endpointURL.getProtocol() + "://" + endpointURL.getAuthority();

        String location;
        if (!origin.startsWith(externalUsersDomain) && metadataProperties.getAllowedOrigins().stream().noneMatch(origin::startsWith)) {
            location = origin;
        } else if (StringUtils.isNotBlank(token) && !disabledTokenService.exists(token)) {
            location = UriBuilder.fromUri(origin).queryParam("token", token).build().toString();
        } else {
            location = force ? metadataService.retrieveExternalUsersExternalWebApplicationUrlBase() + "/#/login?origin=" + URLEncoder.encode(origin, "UTF-8") : origin;
        }

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", location).build();
    }
}
