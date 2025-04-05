package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.service.UserDetailServiceImpl;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@ControllerAdvice
public class GlobalController {

    private final UserDetailServiceImpl userDetailServiceImpl;

    public GlobalController(UserDetailServiceImpl userDetailServiceImpl) {
        this.userDetailServiceImpl = userDetailServiceImpl;
    }

    @ModelAttribute("username")
    public String getUsername(Principal principal) {
        return principal.getName();
    }

    @ModelAttribute("role")
    public String getRole(Principal principal) {
        boolean isAdmin = userDetailServiceImpl
                .loadUserByUsername(principal.getName())
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.contains("ROLE_ADMIN"));
        if (isAdmin) {
            return "ADMIN USER";
        }
        return "USER";
    }
}
