package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminStartAndSaveController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminStartAndSaveController(UserService userService, RoleService roleService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Страница со всеми пользователями
    @Transactional(readOnly = true)
    @GetMapping()
    public String home(Model model) {
        if (!model.containsAttribute("newUser")) {
            model.addAttribute("newUser", new User());
        }
        if (!model.containsAttribute("editUser")) {
            model.addAttribute("editUser", new User());
        }
        model.addAttribute("users", userService.getAll());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "/admin";
    }


    //Отправка формы
    @PostMapping(params = "action=create")
    public String saveUser(@ModelAttribute("newUser") @Valid User user, BindingResult bindingResult,
                           @RequestParam(name = "selectedRoles", required = false) List<Long> selectedRoleIds,
                           Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newUser", bindingResult);
            redirectAttributes.addFlashAttribute("newUser", user);
            model.addAttribute("allRoles", roleService.getAllRoles());
            model.addAttribute("selectedRoles", selectedRoleIds);
            return "redirect:/admin";
        }
        Collection<Role> roles;
        if (selectedRoleIds == null || selectedRoleIds.isEmpty()) {
            roles = Collections.singletonList(roleService.findByName("ROLE_USER"));
        } else {
            roles = selectedRoleIds.stream()
                    .map(roleService::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        user.setRoles(roles);
        userService.save(user);
        return "redirect:/admin";
    }

//    //Обновление пользователя
//    @Transactional(readOnly = true)
//    @GetMapping("/update")
//    public String editUser(@RequestParam Long id, Model model) {
//        User user = userService.getById(id);
//        model.addAttribute("allRoles", roleService.getAllRoles());
//        model.addAttribute("user", user);
//        return "update";
//    }

    //Отправка формы обновления
    @PostMapping(params = "action=update")
    public String updateUser(@ModelAttribute("editUser") @Valid  User user,
                             BindingResult bindingResult,
                             @RequestParam(name = "selectedRoles", required = false) List<Long> selectedRoleIds,
                             Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.getFieldErrors()
                .stream()
                .anyMatch(fieldError -> !fieldError.getField().equals("password")) ||
                (!user.getPassword().isEmpty() && bindingResult.hasFieldErrors("password"))) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editUser", bindingResult);
            redirectAttributes.addFlashAttribute("editUser", user);
            model.addAttribute("allRoles", roleService.getAllRoles());
            model.addAttribute("selectedRoleIds", selectedRoleIds);
            return "redirect:/admin";
        }
        Collection<Role> roles;
        if (selectedRoleIds == null || selectedRoleIds.isEmpty()) {
            roles = Collections.singletonList(roleService.findByName("ROLE_USER"));
        } else {
            roles = selectedRoleIds.stream()
                    .map(roleService::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        user.setRoles(roles);
        userService.update(user);
        return "redirect:/admin";
    }

    //Удаление пользователя
    @PostMapping(params = "action=delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}