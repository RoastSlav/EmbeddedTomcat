package Servlets;

import DAO.PostDao;
import Models.Post;
import com.google.gson.stream.JsonReader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Utility.ServletUtility.*;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class PostsServlet extends HttpServlet {
    private static final Pattern COMMENTS_PATTERN = Pattern.compile("/(\\d+)/comments");
    private static final Pattern POST_ID_PATTERN = Pattern.compile("/(d+)");
    private PostDao dao;

    @Override
    public void init() throws ServletException {
        Properties props = new Properties();
        try {
            props.load(Resources.getResourceAsReader("properties.properties"));
            String mBatisResource = props.getProperty("mb_resource");
            try (Reader reader = Resources.getResourceAsReader(mBatisResource)) {
                SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader, props);
                dao = new PostDao(sessionFactory);
            }
        } catch (IOException e) {
            throw new ServletException("There was an error while initializing the servlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // /posts
        if(pathInfo == null) {
            Post[] allPosts = dao.getAllPosts();
            sendJsonBody(resp, allPosts);
        }

        // /posts/1/comments
        Matcher matcher = COMMENTS_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            RequestDispatcher commentsServlet = req.getRequestDispatcher("/comments?postId=" + matcher.group(1));
            commentsServlet.forward(req, resp);
            return;
        }

        // /posts/1
        matcher = POST_ID_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            int i = Integer.parseInt(matcher.group(1));
            Post post = dao.getPost(i);
            sendJsonBody(resp, post);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String json = receiveJsonBody(req);
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        try {
            Post post = GSON.fromJson(reader, Post.class);
            dao.addPost(post);
            sendJsonBody(resp, post);
        } catch (Exception e) {
            resp.sendError(SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(SC_BAD_REQUEST);
            return;
        }

        try {
            String json = receiveJsonBody(req);
            Post post = GSON.fromJson(json, Post.class);
            dao.addPost(post);
            sendJsonBody(resp, post);
            post.id = Integer.parseInt(pathInfo.substring(1));
            dao.updatePost(post);
            sendJsonBody(resp, post);
        } catch (Exception e) {
            resp.sendError(SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            dao.deletePost(id);
        } catch (Exception e) {
            resp.sendError(SC_BAD_REQUEST);
        }
    }
}
