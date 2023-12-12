package com.cesi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cesi.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {}
