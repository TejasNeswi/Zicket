package com.zicket.zicket.controller;

import com.zicket.zicket.entity.User;
import com.zicket.zicket.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired

    private UserService userService;


    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody User userDetails)
    {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User userInDb=userService.findByUsername(username);
        userInDb.setUsername(userDetails.getUsername());
        userInDb.setPassword(userDetails.getPassword());
        userService.saveNewUser(userInDb);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser()
    {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user = userService.findByUsername(username);
        userService.deleteUser(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
