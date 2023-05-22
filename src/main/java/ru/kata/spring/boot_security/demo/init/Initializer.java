package ru.kata.spring.boot_security.demo.init;

import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class Initializer {
    private final UserService userService;

    public Initializer(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {

        Set<Role> adminRoles = new HashSet<>();
        Role userRole = new Role("ROLE_USER");
        Role adminRole = new Role("ROLE_ADMIN");

        User admin = new User("Admin", "Adminov", "admin@mail.ru", "admin", adminRoles);

        adminRoles.add(userRole);
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);

        userService.saveUser(admin);
    }
}
