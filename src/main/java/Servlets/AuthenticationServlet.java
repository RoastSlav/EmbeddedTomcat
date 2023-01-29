package Servlets;

import DAO.TokenDao;
import DAO.UserDao;
import Models.Token;
import Models.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

import static Utility.ServletUtility.*;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class AuthenticationServlet extends HttpServlet {
    private static final Pattern REGISTER_PATTERN = Pattern.compile("/register");
    private static final Pattern LOGIN_PATTERN = Pattern.compile("/login");
    UserDao userDao;
    TokenDao tokenDao;

    @Override
    public void init() throws ServletException {
        Properties props = new Properties();
        try {
            props.load(Resources.getResourceAsReader("properties.properties"));
            String mBatisResource = props.getProperty("mb_resource");
            try (Reader reader = Resources.getResourceAsReader(mBatisResource)) {
                SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader, props);
                userDao = new UserDao(sessionFactory);
                tokenDao = new TokenDao(sessionFactory);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (REGISTER_PATTERN.matcher(pathInfo).matches()) {
            registerUser(req);
            return;
        }

        if (LOGIN_PATTERN.matcher(pathInfo).matches()) {
            if (!loginUser(req, resp))
                resp.sendError(SC_FORBIDDEN, "Invalid credentials");
            return;
        }

        resp.sendError(SC_NOT_FOUND);
    }

    private boolean loginUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = getValueFromPart("username", req);
        String password = getValueFromPart("password", req);

        User user = userDao.getUser(username);
        if (user == null)
            return false;

        String encryptedPass = hashPassword(password + user.salt);
        if (user.password.equals(encryptedPass)) {
            Token token = tokenDao.getToken(username);
            if (token == null) {
                token = createToken(username, tokenDao);
            } else if (token.expirationDate.isBefore(LocalDate.now())) {
                tokenDao.deleteToken(token.token);
                token = createToken(username, tokenDao);
            }
            resp.addHeader("Authorization", "Bearer " + token);
            return true;
        }
        return false;
    }

    private void registerUser(HttpServletRequest req) throws IOException {
        String jsonBody = receiveJsonBody(req);
        User user = GSON.fromJson(jsonBody, User.class);

        Random random = new Random();
        int randomNum = random.nextInt(1000000000);

        user.password = hashPassword(user.password + randomNum);
        user.salt = randomNum;
        userDao.createUser(user);
    }
}
