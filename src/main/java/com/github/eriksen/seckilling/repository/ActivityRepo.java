package com.github.eriksen.seckilling.repository;

import com.github.eriksen.seckilling.model.Activity;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * ActivityRepo
 */
public interface ActivityRepo extends MongoRepository<Activity, String> {

}