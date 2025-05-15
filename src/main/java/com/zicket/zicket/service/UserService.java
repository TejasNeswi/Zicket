package com.zicket.zicket.service;

import com.zicket.zicket.entity.User;
import com.zicket.zicket.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }
    public void saveNewUser(User user)
    {
        user.setRoles(List.of("USER"));
        userRepository.save(user);
    }

    public Optional<User> findUserById(ObjectId id)
    {
        return userRepository.findById(id);
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
