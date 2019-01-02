package com.github.eriksen.seckilling.repository;

import com.github.eriksen.seckilling.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * UserRepo
 */
public interface UserRepo extends MongoRepository<User, String> {

}