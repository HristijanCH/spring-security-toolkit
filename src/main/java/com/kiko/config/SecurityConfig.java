package com.kiko.config;

import com.kiko.events.CsrfCookieFilter;
import com.kiko.exceptions.CustomAccessDeniedHandler;
import com.kiko.exceptions.CustomBesicAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler=new CsrfTokenRequestAttributeHandler();
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());*/
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll());*/

        http.sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true))
//                .requiresChannel(rcc -> rcc.anyRequest().requiresSecure()) only HTTPS
                .cors(corsConfig-> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration=new CorsConfiguration();
                        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:5173/"));
                        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                        corsConfiguration.setMaxAge(3600L);
                        return corsConfiguration;
                    }
                }))
                .csrf(csrfCongif->csrfCongif.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers("/contact","/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(requests ->
                requests.requestMatchers("/myAccount*","/myBalance","/myCards","/myLoans","/user","/allAccounts").authenticated()
                        .requestMatchers("/notices","/contact","/error","/register","/login","/logout").permitAll());
        http.formLogin(withDefaults());
        http.httpBasic(hbc-> hbc.authenticationEntryPoint(new CustomBesicAuthenticationEntryPoint()));
        http.exceptionHandling(ehc-> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource){
//       UserDetails user = User.withUsername("kiko").password("{bcrypt}$2a$12$b4XwxzRucV/02kco/fQvM./Qbtdo9gTX7ABnZczoYL5H7mBBqv1B2").authorities("READ").build();
//        UserDetails admin = User.withUsername("admin").password("{bcrypt}$2a$12$.VwvOKvHIRrE0ydCA4IYa.8QvU40.oRaXExdJ966RAnq1d.DpGJCy").authorities("ADMIN").build();
//        return new InMemoryUserDetailsManager(user,admin);
//        return new JdbcUserDetailsManager(dataSource);
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
