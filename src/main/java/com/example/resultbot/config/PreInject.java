package com.example.resultbot.config;

import com.example.resultbot.entity.Role;
import com.example.resultbot.entity.User;
import com.example.resultbot.entity.enumirated.Status;
import com.example.resultbot.repository.RoleRepository;
import com.example.resultbot.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class PreInject {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public PreInject(
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private Role createRole(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return role;
    }

    @PostConstruct
    @Transactional
    public void setDefaultUsers() {
        if (roleRepository.count() == 0) {
            List<Role> roles = new ArrayList<>();
            roles.add(createRole("ROLE_ADMIN", "Admin"));
            roles.add(createRole("ROLE_USER", "User"));
            roleRepository.saveAll(roles);
        }

        if (userRepository.count() == 0) {
            User user = new User();
            user.setChatId(1L);
            user.setFirstName("fistName");
            user.setLastName("lastName");
            user.setUserName("admin");
            user.setRole(roleRepository.findByName("ROLE_ADMIN"));
            user.setStatus(Status.ACTIVE);
            user.setEmail("admin@gmail.com");
            user.setPassword(encodePassword("123"));
            userRepository.save(user);
        }
    }
}

