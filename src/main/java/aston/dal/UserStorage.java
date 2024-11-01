package aston.dal;

import aston.model.User;

import java.util.Optional;

public interface UserStorage {
    User create(User user);

    Optional<User> findOne(Long id);
}
