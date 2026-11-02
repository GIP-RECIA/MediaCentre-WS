/**
 * Copyright (C) 2017 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2017 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.mediacentre.ws.config;

import fr.recia.mediacentre.ws.config.bean.AppSecurityProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final AppSecurityProperties securityProperties;

    static String buildAccessExpression(final List<String> authorizedIps) {
        final StringBuilder hasIpAddress = new StringBuilder(
                "hasIpAddress('127.0.0.1') or hasIpAddress('::1')"
        );
        for (final String ip : authorizedIps) {
            hasIpAddress.append(" or hasIpAddress('").append(ip).append("')");
        }
        return hasIpAddress.toString();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.warn("Access denied for IP '{}' on '{}'", request.getRemoteAddr(), request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        final String accessExpression = buildAccessExpression(securityProperties.getAuthorizedIPAccess());

        log.debug("WebSecurity configuration: autorize access on '/**' for {}", accessExpression);

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/**").access(new WebExpressionAuthorizationManager(accessExpression)))
                .exceptionHandling(handling -> handling.accessDeniedHandler(accessDeniedHandler()))
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}