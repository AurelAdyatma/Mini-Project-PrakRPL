package manajemenmusik.dao;

import manajemenmusik.model.User;

public interface UserDAO {
    boolean register(String username, String password);
    User login(String username, String password);
}
