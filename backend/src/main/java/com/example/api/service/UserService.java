package com.example.api.service;

import com.example.api.exception.UserNotFoundException;
import com.example.api.model.User;
import com.example.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User update(Long id, User updated) {
        User existing = findById(id);
        existing.setEmail(updated.getEmail());
        existing.setUsername(updated.getUsername());
        existing.setRole(updated.getRole());
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
