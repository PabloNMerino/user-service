package com.dh.userService.userservice.service;


import com.dh.userService.userservice.entities.User;
import com.dh.userService.userservice.entities.dto.UserKeycloak;
import com.dh.userService.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeycloakService keycloakService;

    public User createUser(User user) throws Exception {
        System.out.println("- - - Creating user -> " + user.getName());
        userRepository.save(user);
        keycloakService.createUser(new UserKeycloak(user.getUserName(), user.getEmail(), user.getName(), user.getLastName(), user.getPassword()));

        return user;
    }

    public Optional<User> findUserById(Long id) {
        System.out.println("- - - Searching user by ID -> " + id);
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        System.out.println("- - - Retrieving all user");
        return userRepository.findAll();
    }

    public void deleteUserById(Long id) {
        System.out.println("- - - Deleting user by ID -> " + id);
        userRepository.deleteById(id);
    }

    public void updateUser(User user) {
        System.out.println("- - - Updating user info ID -> " + user.getId());
        userRepository.save(user);
    }
}
