package es.gobcan.istac.edatos.external.users.web.config;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.errors.AccessDeniedCustomHandler;
import es.gobcan.istac.edatos.external.users.web.security.filter.CaptchaFilter;
import es.gobcan.istac.edatos.external.users.web.security.filter.JWTAuthenticationFilter;
import es.gobcan.istac.edatos.external.users.web.security.filter.JWTAuthorizationFilter;
import es.gobcan.istac.edatos.external.users.web.security.provider.LoginPasswordAuthenticationProvider;
import es.gobcan.istac.edatos.external.users.web.security.provider.TokenProvider;
import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.security.Http401UnauthorizedEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;

    private JHipsterProperties  jHipsterProperties;

    @Autowired
    private MetadataProperties  metadataProperties;

    private final Environment   env;

    public SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider, JHipsterProperties jHipsterProperties, Environment env) {

        this.tokenProvider = tokenProvider;
        this.jHipsterProperties = jHipsterProperties;
        this.env = env;
    }

    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.setClearAuthentication(true);
        logoutHandler.setInvalidateHttpSession(true);
        return logoutHandler;
    }

    @Bean
    public Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint() {
        return new Http401UnauthorizedEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new LoginPasswordAuthenticationProvider();
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

    private CsrfTokenRepository getCsrfTokenRepository() {
        CookieAndHeaderCsrfTokenRepository tokenRepository = CookieAndHeaderCsrfTokenRepository.withHttpOnlyFalse();
        tokenRepository.setCookiePath("/");
        return tokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //@formatter:off
        web
            .ignoring()
            .antMatchers("/*")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/assets/**")
            .antMatchers("/test/**");
        //@formatter:on
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        // One way to configure CORS on this spring version when we are using spring-mvc
        return new WebMvcConfigurerAdapter() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //@formatter:off
                registry.addMapping("/api/**") // As the doc says, just adding the mapping is super permissive, so is needed to securize it
                        .allowedOrigins(metadataProperties.getAllowedOrigins().toArray(new String[0])) // https://stackoverflow.com/a/4042464
                        .allowedMethods("GET", "POST", "PUT")
                        .allowedHeaders("authorization", "content-type", "x-xsrf-token", "api-key")
                        .exposedHeaders("x-xsrf-token") // We need to be able to read it from the response
                        .allowCredentials(true) // Needed for sending cookies on the requests
                ;
                //@formatter:on
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] UNAUTHORIZED_REQUESTS = {
            "/api/activate",
            "/api/authenticate",
            "/api/profile-info",
            "/api/account/signup/**",
            "/api/account/migrated-user-info/**",
            "/api/login",
            "/api/recover-password/**",
            "/v2/api-docs/**",
            "/apis/operations-internal/**",
            "/api/data-protection-policy",
            "/api/languages/internationalization",
            "/captcha/**",
            "/api/filters/last-access/**",
            "/api/account/email/confirm"
        };

        //@formatter:off
        http
             .cors() // This is needed here so spring-security can take into account the spring-mvc cors configuration, mainly to allow the OPTIONS requests done on preflight
         .and()
            .addFilterBefore(new CaptchaFilter(metadataProperties, mapper()), JWTAuthenticationFilter.class)
            .addFilter(new JWTAuthenticationFilter(authenticationProvider(), tokenProvider, mapper(),jHipsterProperties, env, metadataProperties))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), tokenProvider))
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())
            .authenticationEntryPoint(http401UnauthorizedEntryPoint())
        .and()
            .csrf().csrfTokenRepository(getCsrfTokenRepository()).ignoringAntMatchers(UNAUTHORIZED_REQUESTS)
        .and()
            .headers().frameOptions().sameOrigin()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
                .antMatchers(UNAUTHORIZED_REQUESTS).permitAll()
                .antMatchers(HttpMethod.HEAD, "/api/categories").permitAll()
                .antMatchers("/**").authenticated();
        //@formatter:on
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedCustomHandler();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
