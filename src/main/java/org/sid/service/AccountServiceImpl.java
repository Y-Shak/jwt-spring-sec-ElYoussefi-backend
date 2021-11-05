package org.sid.service;

import org.sid.dao.RoleRepository;
import org.sid.dao.UserRepository;
import org.sid.entities.AppRole;
import org.sid.entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService{
    // cette classe est une maniere de centraliser l'essentiel sur la gestion des users
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public AppUser saveUser(AppUser user) {
        String passHash = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(passHash);
        return userRepository.save(user) ;
    }

    @Override
    public AppRole saveRole(AppRole role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String userName, String roleName) {
        AppRole role =roleRepository.findByRoleName(roleName);
        AppUser user = userRepository.findByUserName(userName);
        // puisque la méthode est transactionnal automatiquement il ajoute
        // le role dans la bdd

        user.getRoles().add(role);
    }

    @Override
    public AppUser findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
}
