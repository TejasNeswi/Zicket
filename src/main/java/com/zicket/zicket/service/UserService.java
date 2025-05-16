package com.zicket.zicket.service;

import com.zicket.zicket.entity.User;
import com.zicket.zicket.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }
    public void saveNewUser(User user)
    {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER"));
        user.setPayments(new ArrayList<>());
        user.setTickets(new ArrayList<>());
        userRepository.save(user);
    }

    public void save(User user)
    {
        userRepository.save(user);
    }

    public void loginUser(User user)
    {
        userRepository.save(user);
    }

    public User findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(User user)
    {
        userRepository.delete(user);
    }
}
