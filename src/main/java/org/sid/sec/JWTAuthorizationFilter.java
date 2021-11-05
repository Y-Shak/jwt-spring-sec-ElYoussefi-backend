package org.sid.sec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Headers","Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
        response.addHeader("Access-Control-Expose-Headers","Access-Control-Allow-Origin,Access-Control-Allow-Credentials,Authorization");

        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
        }else{



        // je cherche si dans le request il y a une variable nommée Autorization
        String jwt = request.getHeader(SecurityConstants.HEADER_STRING);
        // si le header ne contient pas le token le cas de null
        // ou si le token ne commence pas par le prefix
        // on verifie pas , on fait return pour sortir de la methode
        if(jwt == null || !jwt.startsWith(SecurityConstants.TOKEN_PREFIX)){
            // on appel doFilter pour qu'il passe à autre filtre s'ils existent
            filterChain.doFilter(request,response);
            return;
        }
        // Sinon on va signer le token
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .parseClaimsJws(jwt.replace(SecurityConstants.TOKEN_PREFIX,""))
                .getBody();
        String username = claims.getSubject();
        // dans le token on stock la liste des roles sous forme d'un tableau avec
        // la colonne 1 contient la Key nommé "authority" , on va l'utiliser dans la recupération dans le foreach
        // la colonne 2 contient la valeur soit ADMIN soit USER

        ArrayList<Map<String,String>> roles = (ArrayList<Map<String, String>>) claims.get("roles");
        // ici caste l'objet roles qu'on vient de recuperer pour prendre la forme du tableau autorithies

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(r -> {
            authorities.add(new SimpleGrantedAuthority(r.get("authority")));
        });
        // le mpd ne dois pas etre dans le token , prq le user est deja authentifié
        // jwt que on traite maintenant suppose que le user est deja authentifié ( si il est null on sort au debut de la methode)
        // ici on verifie que le token reçu est ce qu'il est valide ou pas
        // ici il a besoin des roles pour que au moment ou on demande une ressource qui est protegé ,
        // on verifie si le user il a le droit d'acceder à cette ressource ou pas
        UsernamePasswordAuthenticationToken userByToken =
                new UsernamePasswordAuthenticationToken(username,null,authorities);
        // ici on accede au context de security de spring avec SecurityContextHolder
        // et apres on va recharger le user authentifié " userByToken "
        // c'est comme on dit a spring le user qui a fait la requette le voici tu le charge dans ton contexte
        // de cette maniere il va connaître son username et ses roles

        SecurityContextHolder.getContext().setAuthentication(userByToken);
        filterChain.doFilter(request,response);

        }
    }
}
