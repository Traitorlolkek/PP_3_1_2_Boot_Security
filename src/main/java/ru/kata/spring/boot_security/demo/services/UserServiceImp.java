package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User add(User user) {
        String passEncoder = passwordEncoder.encode(user.getPassword());
        user.setPassword(passEncoder);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.getById(id).getRoles().clear();
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        Optional<User> updatableUserOptional = userRepository.findById(id);
        if (updatableUserOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found with ID " + id);
        }
        User updatableUser = updatableUserOptional.get();
        updatableUser.setUsername(user.getUsername());
        updatableUser.setName(user.getName());
        updatableUser.setSurname(user.getSurname());
        updatableUser.setAge(user.getAge());
        updatableUser.setPassword(passwordEncoder.encode(user.getPassword()));
        updatableUser.setRoles(user.getRoles()); // Обновление ролей
        return userRepository.save(updatableUser);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID " + id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username " + username));
    }
}
