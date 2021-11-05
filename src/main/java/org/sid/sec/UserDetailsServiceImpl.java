package org.sid.sec;

import org.sid.entities.AppUser;
import org.sid.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = accountService.findUserByUserName(username);

        if (user == null) throw new UsernameNotFoundException("utilisateur introuvable ");
        Collection<GrantedAuthority> autorities = new ArrayList<>();
        user.getRoles().forEach(r->{
                    autorities.add(new SimpleGrantedAuthority(r.getRoleName()));
                }
        );
        return new User(user.getUserName(),user.getPassword(),autorities);
    }
}
