package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.security.Principal;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Страница просмотра для USER
    @GetMapping("/user")
    @Transactional(readOnly = true)
    public String home(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        System.out.println("Principal: " + principal);
        System.out.println("User from DB: " + user);
        System.out.println("User ID: " + (user != null ? user.getId() : "null"));
        model.addAttribute("user", user);
        model.addAttribute("users", userRepository.findAll());
        return "user";
    }

    //Страница просмотра всех юзеров для админа
    @GetMapping("/{username}")
    public String admin(Model model, Principal principal, @PathVariable String username) {
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "user";
    }
}
