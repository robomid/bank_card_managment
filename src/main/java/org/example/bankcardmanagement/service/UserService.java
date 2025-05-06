package org.example.bankcardmanagement.service;

import jakarta.transaction.Transactional;
import org.example.bankcardmanagement.dto.UserDtoRequest;
import org.example.bankcardmanagement.dto.UserDtoResponse;
import org.example.bankcardmanagement.mapper.UserMapper;
import org.example.bankcardmanagement.model.User;
import org.example.bankcardmanagement.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        return userRepository.findByEmail(currentUsername).orElse(null);
    }

    public List<UserDtoResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper.INSTANCE::toUserDtoResponse).collect(Collectors.toList());
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public void updateUser(long userId, UserDtoRequest userDtoRequest) {
        User user = getUserById(userId);
        user.setEmail(userDtoRequest.email() != null ? userDtoRequest.email() : user.getEmail());
        user.setUsername(userDtoRequest.username() != null ? userDtoRequest.username() : user.getUsername());
        user.setEnabled(userDtoRequest.enabled() != null ? userDtoRequest.enabled() : user.getEnabled());

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(long userId) {
        User user = getUserById(userId);
        user.setEnabled(false);

        userRepository.save(user);
    }
}
