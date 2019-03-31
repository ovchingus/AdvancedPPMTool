package com.github.ovchingus.ppmtool.services;

import com.github.ovchingus.ppmtool.domain.User;
import com.github.ovchingus.ppmtool.exceptions.UsernameAlreadyExistsException;
import com.github.ovchingus.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User saveUser(User newUser) {
        String newUsername = newUser.getUsername();
        try {

            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

            // Username have to be unique
            newUser.setUsername(newUsername);

            //TODO: make sure that password and confirmPassword match
            //We don`t persist or show confirmPassword
            newUser.setConfirmPassword("");
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new UsernameAlreadyExistsException("Username '" + newUsername + "' already exists");
        }
    }

}
