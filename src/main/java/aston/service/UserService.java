package aston.service;

import aston.model.User;

public interface UserService {
    User createUser(User user);

    User getUser(Long id);
}
