package DAO;

import Mappers.UserMapper;
import Models.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class UserDao implements UserMapper {
    SqlSessionFactory sqlFactory;

    public UserDao(SqlSessionFactory sqlFactory) {
        this.sqlFactory = sqlFactory;
    }

    @Override
    public User getUser(String username) {
        validateNotNull(username);
        try (SqlSession session = sqlFactory.openSession(true)) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.getUser(username);
        }
    }

    @Override
    public int createUser(User user) {
        validateNotNull(user);
        try (SqlSession session = sqlFactory.openSession(true)) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.createUser(user);
        }
    }

    private void validateNotNull(Object obj) {
        if (obj == null)
            throw new IllegalArgumentException("Object can't be a null value");
    }
}
