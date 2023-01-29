package Mappers;

import Models.Comment;

public interface CommentMapper {
    public Comment[] getComment(int id);

    public Comment getAllComments();

    public int addComment(Comment comment);

    public int updateComment(Comment comment);

    public int deleteComment(int id);
}
