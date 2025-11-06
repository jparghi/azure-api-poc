package com.example.api.service;

import com.example.api.exception.UserNotFoundException;
import com.example.api.messaging.UserEventPublisher;
import com.example.api.model.User;
import com.example.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;

    public UserService(UserRepository userRepository, UserEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User create(User user) {
        User created = userRepository.save(user);
        eventPublisher.publishUserCreated(created);
        return created;
    }

    public User update(Long id, User updated) {
        User existing = findById(id);
        existing.setEmail(updated.getEmail());
        existing.setUsername(updated.getUsername());
        existing.setRole(updated.getRole());
        User saved = userRepository.save(existing);
        eventPublisher.publishUserUpdated(saved);
        return saved;
    }

    public void delete(Long id) {
        User existing = findById(id);
        userRepository.delete(existing);
        eventPublisher.publishUserDeleted(existing);
    }
}
