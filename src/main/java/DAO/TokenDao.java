package DAO;

import Mappers.TokenMapper;
import Models.Token;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class TokenDao implements TokenMapper {
    private final SqlSessionFactory sqlFactory;

    public TokenDao(SqlSessionFactory sqlSession) {
        this.sqlFactory = sqlSession;
    }

    @Override
    public Token getToken(String userName) {
        validateNotNull(userName);
        try (SqlSession session = sqlFactory.openSession(true)) {
            TokenMapper mapper = session.getMapper(TokenMapper.class);
            return mapper.getToken(userName);
        }
    }

    @Override
    public int addToken(Token token) {
        validateNotNull(token);
        try (SqlSession session = sqlFactory.openSession(true)) {
            TokenMapper mapper = session.getMapper(TokenMapper.class);
            return mapper.addToken(token);
        }
    }

    @Override
    public int deleteToken(String userName) {
        validateNotNull(userName);
        try (SqlSession session = sqlFactory.openSession(true)) {
            TokenMapper mapper = session.getMapper(TokenMapper.class);
            return mapper.deleteToken(userName);
        }
    }

    private void validateNotNull(Object obj) {
        if (obj == null)
            throw new IllegalArgumentException("Object can't be a null value");
    }
}
