package com.kiko.config;

import com.kiko.exceptions.CustomAccessDeniedHandler;
import com.kiko.exceptions.CustomBesicAuthenticationEntryPoint;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());*/
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll());*/

        http.sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true))
//                .requiresChannel(rcc -> rcc.anyRequest().requiresSecure()) only HTTPS
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .authorizeHttpRequests(requests ->
                requests.requestMatchers("/account","/balance","/cards","/loans","/user").authenticated()
                        .requestMatchers("/notices","contact","/error","/register").permitAll());
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
