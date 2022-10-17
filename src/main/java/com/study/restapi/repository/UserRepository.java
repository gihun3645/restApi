package com.study.restapi.repository;

import com.study.restapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
     User findByUsername(String username);
}
