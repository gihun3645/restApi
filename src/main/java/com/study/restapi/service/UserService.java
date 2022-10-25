package com.study.restapi.service;

import com.study.restapi.dto.RegisterDto;
import com.study.restapi.entity.User;
import com.study.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User register(RegisterDto registerDto) {
        User user = new User();
        user.setName(registerDto.getName());
        user.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
        user.setUsername(registerDto.getUsername());
        user.setRoles("ROLE_USER");
        return userRepository.save(user);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findUser(int id) {
        return userRepository.findById(id).orElseThrow(()->{
            return new IllegalArgumentException("User ID를 찾을 수 없습니다.");
        });
    }
}
