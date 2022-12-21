package com.kankaBot.kankaBot.dao.repository;

import com.kankaBot.kankaBot.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
