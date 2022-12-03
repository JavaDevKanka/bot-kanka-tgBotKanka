package com.kankaBot.kankaBot.dao.repository;

import com.kankaBot.kankaBot.models.UserData.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
