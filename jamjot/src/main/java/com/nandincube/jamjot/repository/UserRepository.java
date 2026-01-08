package com.nandincube.jamjot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.model.User;

public interface UserRepository extends JpaRepository<User, String> {
}
