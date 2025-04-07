package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Страница просмотра пользователя с логикой
    @GetMapping("/{username}")
    public String home(Model model, Principal principal, @PathVariable String username) throws AccessDeniedException {
        User user = userRepository.findByUsername(principal.getName());
        User currentUser = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("currentUser", currentUser);
        if (userRepository.findByUsername(principal.getName()).getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            model.addAttribute("isAdmin", true);
            model.addAttribute("users", userRepository.findAll());
        } else {
            model.addAttribute("isAdmin", false);
            model.addAttribute("users", userRepository.findByUsername(username));
        }
        if (!principal.getName().equals(username) && user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Доступ для данного пользователя запрещен");
        }
        return "user";
    }
}
