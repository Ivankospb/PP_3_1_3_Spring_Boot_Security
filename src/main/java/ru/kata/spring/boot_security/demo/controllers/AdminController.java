package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserServiceImpl;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceImpl userService;
    private final RoleService roleService;

    public AdminController(UserServiceImpl userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/page")
    public String adminPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("principal", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("newUser", new User());
        return "administrator";
    }

    @PatchMapping("/redactor/{id}")
    public String patchAdminRedactor(@ModelAttribute("user") User user, @PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.adminRedactor(user, id);
        return "redirect:/admin/page";
    }

    @DeleteMapping("/delete/{id}")
    public String adminDelete(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.delete(id);
        return "redirect:/admin/page";
    }

    @PostMapping("/registration")
    public String registrationPost(@ModelAttribute("newuser") User user, @AuthenticationPrincipal UserDetails userDetails) {
        userService.saveUser(user);
        return "redirect:/admin/page";
    }
}
