package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.services.UserServiceImpl;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceImpl userServiceImpl;
    private final RoleServiceImpl roleServiceImpl;

    public AdminController(UserServiceImpl userService, RoleServiceImpl roleServiceImpl) {
        this.userServiceImpl = userService;
        this.roleServiceImpl = roleServiceImpl;
    }

    @GetMapping("/page")
    // страница админа со списком всех пользователей
    public String adminPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userServiceImpl.getUserByEmail(userDetails.getUsername());
        model.addAttribute("principal", user);
        model.addAttribute("users", userServiceImpl.getAllUsers());
        model.addAttribute("roles", roleServiceImpl.findAll());
        model.addAttribute("newUser", new User());
        return "administrator";
    }

    @GetMapping("/redactor/{id}")
    //Редактирование пользователя
    public String patchAdminRedactor(Model model, @PathVariable("id") Long id) {
        model.addAttribute("user", userServiceImpl.findOne(id));
        model.addAttribute("rolesAdd", roleServiceImpl.getRoles());
        return "update";
    }
    @PatchMapping("/redactor/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") User updatedUser) {
        User existingUser = userServiceImpl.getUserById(id).orElse(null);

        if (existingUser != null) {
            // Обновление данных пользователя на основе переданных значений
            existingUser.setName(updatedUser.getName());
            existingUser.setSurname(updatedUser.getSurname());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setRoles(updatedUser.getRoles());

            userServiceImpl.saveUser(existingUser); // Сохранение обновленных данных в базе данных
        }

        return "redirect:/admin/page"; // Перенаправление на страницу списка пользователей
    }

    @DeleteMapping("/delete/{id}")
    //Удаление пользователя админом
    public String adminDelete(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userServiceImpl.delete(id);
        return "redirect:/admin/page";
    }

    @GetMapping("/addUser")
    //Создание нового пользователя
    public String addNewUser(Model model, @ModelAttribute("user") User user) {
        List<Role> roles = roleServiceImpl.getRoles();
        model.addAttribute("rolesAdd", roles);
        return "creation";
    }

    @PostMapping
//Запись пользователя
    public String addCreateNewUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Если есть ошибки валидации, добавляем их в модель
            model.addAttribute("rolesAdd", roleServiceImpl.getRoles());
            return "creation"; // Возвращаем представление с формой создания пользователя
        }

        try {
            userServiceImpl.saveUser(user);
        } catch (Exception e) {
            // Если возникает исключение при сохранении пользователя
            bindingResult.rejectValue("email", "error.user", "Пользователь с таким логином уже существует");
            model.addAttribute("rolesAdd", roleServiceImpl.getRoles());
            return "creation"; // Возвращаем представление с формой создания пользователя
        }

        return "redirect:/admin/page";
    }
}