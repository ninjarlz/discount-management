package pl.tul.discountmanagement.shared.infrastructure.security.config;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.tul.discountmanagement.shared.infrastructure.devmode.controller.DevModeAuthController;
import pl.tul.discountmanagement.shared.infrastructure.devmode.util.DevModeUtils;
import pl.tul.discountmanagement.shared.infrastructure.security.converter.AuthenticationTokenConverter;

import static pl.tul.discountmanagement.shared.infrastructure.security.constant.TokenStructure.TOKEN_TYPE;

/**
 * Configuration for Spring Security
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityFilterConfig {

    private final DevModeUtils devModeUtils;
    private final AuthenticationTokenConverter authenticationTokenConverter;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    private static final String SUBDIRECTORIES_PATTERN = "/**";
    private static final String[] ALLOWED_URLS = {};

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(oAuth2ResourceServerProperties.getJwt().getJwkSetUri())
                .jwtProcessorCustomizer(customizer ->
                        customizer.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType(TOKEN_TYPE))))
                .build();
    }

    /**
     * Produces security filter chain configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] devModeUrls = {DevModeAuthController.AUTH_URL + SUBDIRECTORIES_PATTERN};
        http.sessionManagement(management -> management.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(devModeUtils.isDevModeEnabled() ? ArrayUtils.addAll(ALLOWED_URLS, devModeUrls) : ALLOWED_URLS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(server -> server.jwt(customizer -> customizer.jwtAuthenticationConverter(authenticationTokenConverter)));
        return http.build();
    }
}
