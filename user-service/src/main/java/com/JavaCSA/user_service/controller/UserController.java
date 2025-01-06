package com.JavaCSA.user_service.controller;

import com.JavaCSA.user_service.dto.UserDTO;
import com.JavaCSA.user_service.entity.User;
import com.JavaCSA.user_service.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user, HttpServletRequest request) {
        UserDTO userDTO = userService.registerUser(user);
        if (userDTO != null){
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username, @RequestParam String password, HttpServletRequest request){
        UserDTO userDTO = userService.loginUser(username, password);
        if (userDTO != null){
            // Create a JWT token with configurations:
            String token = JWT.create()
                    .withSubject(userDTO.getUsername())  // Set the 'subject' claim as the username
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // Set expiration time to 10 minutes from now
                    .withIssuer(request.getRequestURL().toString())  // Set the 'issuer' claim to the URL where the request was made
                    .sign(Algorithm.HMAC256("secret")); // Sign the token with HMAC256 algorithm using the 'secret' key

            // Return the ResponseEntity with status OK, and add the token in the Authorization header
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).body(userDTO);
        } else {
            // If the user is not found or the authentication failed, return an unauthorized status
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getUserById(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserDetails(userId);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO.getEmail()); // Credit Scoring Service is getting this emailId
        }
        return ResponseEntity.notFound().build();
    }
}