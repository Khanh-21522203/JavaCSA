package com.JavaCSA.user_service.service;

import com.JavaCSA.user_service.dto.UserDTO;
import com.JavaCSA.user_service.entity.User;
import com.JavaCSA.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserDTO registerUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return mapToDTO(user);
    }

    public UserDTO loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return mapToDTO(user);
        }
        return null;
    }

    public UserDTO getUserDetails(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return UserDTO.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
        }
        return null;
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
