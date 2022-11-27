package io.proj3ct.kankaBot.dao.abstracts;

import io.proj3ct.kankaBot.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
