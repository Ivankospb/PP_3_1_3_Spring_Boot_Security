package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.services.UserServiceImpl;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl, RoleServiceImpl roleServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/page")
    public String adminUserPage(@RequestParam("id") Long id, Model model, Principal principal) {
        User user = userServiceImpl.getUserById(id).orElse(null);
        model.addAttribute("user", user);
        return "user_view";
    }

    @GetMapping
    public String userPage(Model model, Principal principal) {
        String email = principal.getName(); // Получение электронной почты аутентифицированного пользователя
        User user = userServiceImpl.getUserByLogin(email); // метод для получения пользователя по электронной почте
        model.addAttribute("user", user);
        return "user";
    }
}