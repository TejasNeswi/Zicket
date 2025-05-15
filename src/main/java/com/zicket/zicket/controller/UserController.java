package com.zicket.zicket.controller;

import com.zicket.zicket.entity.User;
import com.zicket.zicket.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @PostMapping("/signup")
    public boolean createNewUser(@RequestBody User user)
    {
        userService.saveNewUser(user);
        return true;
    }
    @PutMapping("{userId}")
    public void updateUser(@RequestBody User user, @PathVariable ObjectId userId)
    {
        Optional<User> userById = userService.findUserById(userId);
        if(userById.isPresent())
        {
            User old=userById.get();
            old.setUsername(user.getUsername()!=null && !user.getUsername().equals("")?user.getUsername(): old.getUsername());
            old.setPassword(user.getPassword()!=null && !user.getPassword().equals("")?user.getPassword(): old.getPassword());
            userService.saveNewUser(old);
        }
    }

    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username)
    {
        User user = userService.findByUsername(username);
        userService.deleteUser(user);
    }
}
