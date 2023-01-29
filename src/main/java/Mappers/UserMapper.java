package Mappers;

import Models.User;

public interface UserMapper {
    public User getUser(String username);

    public int createUser(User user);
}
