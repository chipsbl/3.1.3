package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
    void save(User user);

    void delete(Long id);

    void update(User user);

    List<User> getAll();

    User getById(Long id);

    public UserDetails loadUserByUsername(String username);

    public void setRoles(User user, List<Long> selectedRoleIds);
}
