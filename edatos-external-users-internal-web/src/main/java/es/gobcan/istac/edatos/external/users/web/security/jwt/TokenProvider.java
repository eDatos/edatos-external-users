package es.gobcan.istac.edatos.external.users.web.security.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import es.gobcan.istac.edatos.external.users.core.domain.InternalEnabledTokenEntity;
import es.gobcan.istac.edatos.external.users.core.service.InternalEnabledTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import es.gobcan.istac.edatos.external.users.web.config.JHipsterExtraProperties;
import io.github.jhipster.config.JHipsterProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import static es.gobcan.istac.edatos.external.users.web.util.SecurityCookiesUtil.SERVICE_TICKET_COOKIE;
import static es.gobcan.istac.edatos.external.users.web.util.SecurityCookiesUtil.getServiceTicketCookie;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private String secretKey;

    private long tokenValidityInMilliseconds;

    private long tokenValidityInMillisecondsForRememberMe;

    private long tokenRenewPeriodInMilliseconds;

    private final JHipsterProperties jHipsterProperties;

    private final JHipsterExtraProperties jHipsterExtraProperties;

    private final InternalEnabledTokenService enabledTokenService;

    private final HttpServletRequest httpServletRequest;

    public TokenProvider(JHipsterProperties jHipsterProperties, JHipsterExtraProperties jHipsterExtraProperties, InternalEnabledTokenService enabledTokenService, HttpServletRequest httpServletRequest) {
        this.jHipsterProperties = jHipsterProperties;
        this.jHipsterExtraProperties = jHipsterExtraProperties;
        this.enabledTokenService = enabledTokenService;
        this.httpServletRequest = httpServletRequest;
    }

    @PostConstruct
    public void init() {
        this.secretKey = jHipsterProperties.getSecurity().getAuthentication().getJwt().getSecret();

        this.tokenValidityInMilliseconds = 1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe = 1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();
        this.tokenRenewPeriodInMilliseconds = 1000 * jHipsterExtraProperties.getSecurity().getAuthentication().getJwt().getTokenRenewPeriodInSeconds();
    }

    public String createToken(Authentication authentication, Boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }
        String token = Jwts.builder().setSubject(authentication.getName()).claim(AUTHORITIES_KEY, authorities).signWith(SignatureAlgorithm.HS512, secretKey).setExpiration(validity).compact();
        String serviceTicket = httpServletRequest.getAttribute(SERVICE_TICKET_COOKIE) == null ? getServiceTicketCookie(httpServletRequest.getCookies()) : httpServletRequest.getAttribute(SERVICE_TICKET_COOKIE).toString();
        enabledTokenService.updateOrCreate(new InternalEnabledTokenEntity(token, serviceTicket, validity.toInstant()));
        return token;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(",")).filter(authority -> !StringUtils.isEmpty(authority))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Jws<Claims> validateToken(String authToken) {
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            if(!enabledTokenService.existsByToken(authToken)) {
                claimsJws = null;
                throw new CredentialsExpiredException("The authentication token is disabled. Login again.");
            }
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException | CredentialsExpiredException e) {
            log.debug("JWT: token no válido trace: {}", e);
        }

        return claimsJws;
    }

    public boolean checkRenewTicketIsRequired(Jws<Claims> claimsJws) {
        LocalDateTime convertToLocalDateTime = convertToLocalDateTimeViaInstant(claimsJws.getBody().getExpiration());
        convertToLocalDateTime = convertToLocalDateTime.minus(tokenRenewPeriodInMilliseconds, ChronoUnit.MILLIS);
        LocalDateTime now = LocalDateTime.now();
        return (now.isAfter(convertToLocalDateTime));
    }

    private LocalDateTime convertToLocalDateTimeViaInstant(Date source) {
        if (source == null) {
            return null;
        }

        return source.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
