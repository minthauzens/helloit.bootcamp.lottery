package lv.helloit.bootcamp.lottery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value( "${lv.helloit.bootcamp.lottery.security.enabled}" )
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
                    .antMatchers( "/", "/public/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .httpBasic()
                    .and()
                .formLogin()
                .loginPage("/login.html").permitAll()
                    .defaultSuccessUrl("/")
                    .failureUrl("/login?error=true")
                    .and()
                .logout()
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .logoutSuccessUrl("/")
                    .permitAll()
                    .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("lottery")
                .password("{noop}q1w2e3r4")
                .roles("USER");
        // encoding Base64: lottery:q1w2e3r4 -> bG90dGVyeTpxMXcyZTNyNA==
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**","/webjars/**");
    }
}