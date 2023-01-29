package DAO;

import Mappers.CommentMapper;
import Models.Comment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class CommentDao implements CommentMapper {
    private final SqlSessionFactory sqlFactory;

    public CommentDao(SqlSessionFactory fac) {
        this.sqlFactory = fac;
    }

    @Override
    public Comment[] getComment(int id) {
        validateNotNegative(id);
        try (SqlSession session = sqlFactory.openSession(true)) {
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            return mapper.getComment(id);
        }
    }

    @Override
    public Comment getAllComments() {
        try (SqlSession session = sqlFactory.openSession(true)) {
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            return mapper.getAllComments();
        }
    }

    @Override
    public int addComment(Comment comment) {
        validateNotNull(comment);
        try (SqlSession session = sqlFactory.openSession(true)) {
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            return mapper.addComment(comment);
        }
    }

    @Override
    public int updateComment(Comment comment) {
        validateNotNull(comment);
        try (SqlSession session = sqlFactory.openSession(true)) {
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            return mapper.updateComment(comment);
        }
    }

    @Override
    public int deleteComment(int id) {
        validateNotNegative(id);
        try (SqlSession session = sqlFactory.openSession(true)) {
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            return mapper.deleteComment(id);
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
