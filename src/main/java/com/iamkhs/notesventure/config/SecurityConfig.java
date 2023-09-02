package com.iamkhs.notesventure.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
/**
 * This configuration class is responsible for configuring security settings in the application.
 * It defines security rules, authentication providers, and handles authentication success.
 *
 */

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {


    private CustomLoginSuccessHandler authenticationSuccessHandler;

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        var daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return daoAuthenticationProvider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth->
                        auth.requestMatchers("/user/**").hasAnyAuthority("ROLE_USER", "OIDC_USER")
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/**").permitAll())

                .rememberMe(remember->
                        remember.tokenValiditySeconds(604800) // for 7 days
                .userDetailsService(userDetailsService()))

                .formLogin(form-> form.loginPage("/login")
                        .loginProcessingUrl("/do-login")
                        .successHandler(authenticationSuccessHandler))

                .oauth2Login(auth-> {
                    auth.loginPage("/login");
                    auth.successHandler(authenticationSuccessHandler);
                })

                .authenticationProvider(daoAuthenticationProvider());

        return http.build();
    }

}
