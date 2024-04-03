package com.dh.userService.userservice.service;


import com.dh.userService.userservice.entities.AccessKeycloak;
import com.dh.userService.userservice.entities.Login;
import com.dh.userService.userservice.entities.User;
import com.dh.userService.userservice.entities.dto.UserKeycloak;
import com.dh.userService.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    public AccessKeycloak login(Login loginData) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(loginData.getEmail());
        if(userOptional.isEmpty()) {
            throw new Exception("user not found!");
        }
        return keycloakService.login(loginData);
    }

    public void logout(String userId) {
        keycloakService.logout(userId);
    }

    public Optional<User> findById(Long id) {
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


    public Optional<User> findByEmail(String email) throws Exception {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new Exception("User not found!");
        }
        return user;
    }

    public void forgotPassword(String username)  {
        keycloakService.forgotPassword(username);
    }

}
