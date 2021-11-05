package org.sid;

import org.sid.dao.TaskRepository;
import org.sid.entities.AppRole;
import org.sid.entities.AppUser;
import org.sid.entities.Task;
import org.sid.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.stream.Stream;

@SpringBootApplication
public class JwtSpringSecElYoussefiApplication implements CommandLineRunner {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private AccountService accountService;

    public static void main(String[] args) {
        SpringApplication.run(JwtSpringSecElYoussefiApplication.class, args);
    }

    // charger le bean du encorder prq spring ne le fait pas automatiquement
    // on peut l'utiliser n'importe où dans notre app
    // en utilisant l'annotation @Autowired
    // prq au moment du demarrage toutes les methodes qui utilise l'annotation Bean
    // vont etre exécuter , et il vont devenir un Bean Spring
    // alors ils vont etre utilisable partout dans l'app
    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Override
    public void run(String... args) throws Exception {
        accountService.saveUser(new AppUser(null,"admin","1234",null));
        accountService.saveUser(new AppUser(null,"user","1234",null));
        accountService.saveRole(new AppRole(null,"ADMIN"));
        accountService.saveRole(new AppRole(null,"USER"));
        accountService.addRoleToUser("admin","ADMIN");
        accountService.addRoleToUser("admin","USER");
        accountService.addRoleToUser("user","USER");

        Stream.of("T1","T2","T3").forEach(
                t-> {
                    taskRepository.save(new Task(null,t));
                }
        );
        taskRepository.findAll().forEach(t-> System.out.println(t.getTaskName()));
    }
}
