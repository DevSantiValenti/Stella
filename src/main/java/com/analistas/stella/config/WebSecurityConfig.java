package com.analistas.stella.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JDBC UserDetailsManager para manejar usuarios y roles
    @Bean
    public UserDetailsManager users() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

        // Query para obtener usuario + password
        manager.setUsersByUsernameQuery(
            "SELECT nombrecompleto as username, contrasena as password, true as enabled " +
            "FROM usuarios WHERE nombrecompleto = ?"
        );

        // Query para obtener roles/authorities
        manager.setAuthoritiesByUsernameQuery(
            "SELECT u.nombrecompleto as username, r.nombre as authority " +
            "FROM roles r INNER JOIN usuarios u ON u.rol_id = r.id " +
            "WHERE u.nombrecompleto = ?"
        );

        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/home", "/productos/listado", "/img/**", "/css/**", "/js/**",
                                "/fonts/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll())
                .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/?logout=true")
                        .permitAll());
        return http.build();
    }

//     @Autowired
//     public void configGlobal(AuthenticationManagerBuilder builder) throws Exception {
//         builder
//                 .jdbcAuthentication()
//                 .dataSource(dataSource)
//                 .usersByUsernameQuery(
//                         "select nombrecompleto as username, contrasena as password, true as enabled " +
//                                 "from usuarios " +
//                                 "where nombrecompleto = ?")
//                 .authoritiesByUsernameQuery(
//                         "select u.nombrecompleto as username, r.nombre as authority " +
//                                 "from roles r " +
//                                 "inner join usuarios u on u.rol_id = r.id " +
//                                 "where u.nombrecompleto = ?");
//     }

}
