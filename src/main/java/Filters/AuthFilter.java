package Filters;

import DAO.TokenDao;
import Models.Token;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Properties;

import static Utility.ServletUtility.createToken;

public class AuthFilter implements Filter {
    private static TokenDao dao;

    public static boolean checkAuth(HttpServletRequest req) {
        String authorization = req.getHeader("Authorization");
        if (authorization == null)
            return false;

        Token token = dao.getToken(authorization);
        if (token == null)
            return false;

        if (token.expirationDate.isBefore(LocalDate.now()))
            createToken(token.userName, dao);
        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Properties props = new Properties();
        try {
            props.load(Resources.getResourceAsReader("properties.properties"));
            String mBatisResource = props.getProperty("mb_resource");
            try (Reader reader = Resources.getResourceAsReader(mBatisResource)) {
                SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader, props);
                dao = new TokenDao(sessionFactory);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        if (!checkAuth((HttpServletRequest) request)) {
//            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
