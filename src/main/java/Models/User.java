package Models;

public class User {
    public int id;
    public String username;
    public String email;
    public String password;
    public int salt;

    public User() {

    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
