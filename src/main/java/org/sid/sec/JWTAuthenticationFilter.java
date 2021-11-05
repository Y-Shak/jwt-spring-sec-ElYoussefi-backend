package org.sid.sec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.sid.entities.AppUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        AppUser user = null;
        try {
            // todo creer une methode Bean comme avec BCrypt pour que à chaque appel l'app ne cree pas nouveau object
           // on demmande au objet jackson de serializer le contenu du request dans un objet AppUser
            user = new ObjectMapper().readValue(request.getInputStream(),AppUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // retourner a spring security l'object authentificationManager()
        //
        System.out.println("***************************************");
        System.out.println(authenticationManager);

        System.out.println("***************************************");
        System.out.println("username : " + user.getUserName());
        System.out.println("password : " + user.getPassword());
        return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  user.getUserName(),
                  user.getPassword()
          ));
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        User springUser = (User) authResult.getPrincipal();
        // à partir de ce springUser on va crée le token
        String jwtToken = Jwts.builder()
                .setSubject(springUser.getUsername())
                        .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                                .signWith(SignatureAlgorithm.HS512,SecurityConstants.SECRET)
                                        .claim("roles",springUser.getAuthorities())
                                                .compact();
        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwtToken);

    }
}
