package org.sid.web;

import org.sid.entities.AppUser;
import org.sid.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountRestController {
    @Autowired
    private AccountService accountService;
    @Transactional
    @PostMapping("/register")
    public AppUser register( @RequestBody RegisterForm userForm){
        if(!userForm.getPassword().equals(userForm.getRepassword()))
            throw new RuntimeException("You must confirm your password \n");
        AppUser userFromDatabase = accountService.findUserByUserName(userForm.getUsername());
        if (userFromDatabase !=null)
            throw new RuntimeException("This user already exist ! \n");
        AppUser user = new AppUser();
        user.setUserName(userForm.getUsername());
        user.setPassword(userForm.getPassword());
        accountService.saveUser(user);
        accountService.addRoleToUser(user.getUserName(),"USER");
        return user;
    }
}
