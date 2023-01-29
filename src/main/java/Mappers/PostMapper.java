package Mappers;

import Models.Post;

public interface PostMapper {
    public Post getPost(int id);

    public Post[] getAllPosts();

    public int addPost(Post post);

    public int updatePost(Post post);

    public int deletePost(int id);
}
