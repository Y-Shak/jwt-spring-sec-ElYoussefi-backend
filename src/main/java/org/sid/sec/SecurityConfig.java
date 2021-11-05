package org.sid.sec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // je vais transmettre tous ce qui concerne :
        // -- le chargement de user
        // -- le chargement des roles
        // -- definir les authorites
        // au service UserDetailService pour qu'il le prend en charge
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        // ce code et pour desactiver la session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().antMatchers("/login/**", "/register/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/tasks/**").hasAuthority("ADMIN");
        http.authorizeRequests().anyRequest().authenticated();

        // declarer le filter 1 qui a besoin de prendre en parametre l'objet authentificationManager()
        // une methode héritée  par la classe WebSecurityConfigurerAdapter
        // qui permet de retourner l'objet principale de spring security
        // c'est lui qui gere l'authentification
        http.addFilter(new JWTAuthenticationFilter(authenticationManager()));
        // filter 2
        http.addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

    }



}
