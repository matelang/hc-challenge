package dev.matelang.orchestrator.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@EnableWebSecurity
public class OAuth2ResourceServerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/message/**").hasAuthority("SCOPE_message:read")
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        // @formatter:on
    }
}

