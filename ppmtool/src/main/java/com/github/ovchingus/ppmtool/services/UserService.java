package com.github.ovchingus.ppmtool.services;

import com.github.ovchingus.ppmtool.domain.User;
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

    public User saveUser (User newUser) {
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

        //TODO: Username have to be unique

        //TODO: make sure that password and confirmPassword match
        //We don`t persist or show confirmPassword
        return userRepository.save(newUser);
    }

}
