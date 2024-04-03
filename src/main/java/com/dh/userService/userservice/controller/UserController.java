package com.dh.userService.userservice.controller;


import com.dh.userService.userservice.entities.AccessKeycloak;
import com.dh.userService.userservice.entities.Login;
import com.dh.userService.userservice.entities.User;
import com.dh.userService.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) throws Exception {
        userService.createUser(user);
        return ResponseEntity.ok("User succesfully created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login loginData) throws Exception{
        AccessKeycloak credentials = userService.login(loginData);

        if (credentials != null) {
            return ResponseEntity.ok(credentials);
        } else if (userService.findByEmail(loginData.getEmail()).isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /*
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(userId);

        if (userId.isEmpty()) {
            ResponseEntity.notFound().build();
        }

        userService.logout(userId);

        return ResponseEntity.ok("succesfully logged out");
    }
*/

    @PostMapping("/logout")
    public void logout() {
        Authentication userId = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(userId);

        if(userId == null) {
            System.out.println("aca no hay nada");
        }

}

    @PutMapping("/{username}/forgot-password")
    public void forgotPassword(@PathVariable String username) {
        userService.forgotPassword(username);
    }


/*
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userFoundOptional = userService.findUserById(id);
        if(userFoundOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User userFound = userFoundOptional.get();
        return ResponseEntity.ok(userFound);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        Optional<User> userFoundOptional = userService.findUserById(id);
        if(userFoundOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUserById(id);
        return ResponseEntity.ok("User succesfully deleted");
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        Optional<User> userFoundOptional = userService.findUserById(user.getId());
        if (userFoundOptional.isEmpty()) {
            System.out.println("User not found");
            return ResponseEntity.notFound().build();
        }
        System.out.println("User succesfully updated");
        userService.updateUser(user);
        return ResponseEntity.ok(user);
    }

 */
}
