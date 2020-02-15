package lv.helloit.bootcamp.lottery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value( "${lottery.security.enabled}" )
    private boolean securityEnabled;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (securityEnabled) {
            configureForProduction(http);
        } else {
            configureForTests(http);
        }
    }

    private void configureForProduction(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers( "/public/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .httpBasic()
                    .and()
                .formLogin()
                .loginPage("/login")
                    .permitAll()
                    .defaultSuccessUrl("/public/")
                    .and()
                .logout()
                    .permitAll()
                    .logoutSuccessUrl("/public/");
    }

    private void configureForTests(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .httpBasic()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("lottery")
//                .password("{noop}q1w2e3r4")
//                .roles("USER");
//        // encoding Base64: lottery:q1w2e3r4 -> bG90dGVyeTpxMXcyZTNyNA==
//    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("lottery")
                        .password("q1w2e3r4")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**","/webjars/**", "/css/**");
    }
}