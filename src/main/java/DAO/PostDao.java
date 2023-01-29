package DAO;

import Mappers.PostMapper;
import Models.Post;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class PostDao implements PostMapper {
    private final SqlSessionFactory sqlFactory;

    public PostDao(SqlSessionFactory fac) {
        this.sqlFactory = fac;
    }

    @Override
    public Post getPost(int id) {
        validateNotNegative(id);
        try (SqlSession session = sqlFactory.openSession(true)) {
            PostMapper mapper = session.getMapper(PostMapper.class);
            return mapper.getPost(id);
        }
    }

    @Override
    public Post[] getAllPosts() {
        try (SqlSession session = sqlFactory.openSession(true)) {
            PostMapper mapper = session.getMapper(PostMapper.class);
            return mapper.getAllPosts();
        }
    }

    @Override
    public int addPost(Post post) {
        validateNotNull(post);
        try (SqlSession session = sqlFactory.openSession(true)) {
            PostMapper mapper = session.getMapper(PostMapper.class);
            return mapper.addPost(post);
        }
    }

    @Override
    public int updatePost(Post post) {
        validateNotNull(post);
        try (SqlSession session = sqlFactory.openSession(true)) {
            PostMapper mapper = session.getMapper(PostMapper.class);
            return mapper.updatePost(post);
        }
    }

    @Override
    public int deletePost(int id) {
        validateNotNegative(id);
        try (SqlSession session = sqlFactory.openSession(true)) {
            PostMapper mapper = session.getMapper(PostMapper.class);
            return mapper.deletePost(id);
        }
    }

    private void validateNotNegative(int id) {
        if (id < 0)
            throw new IllegalArgumentException("Parameter must be positive");
    }

    private void validateNotNull(Object obj) {
        if (obj == null)
            throw new IllegalArgumentException("Object can't be a null value");
    }
}
