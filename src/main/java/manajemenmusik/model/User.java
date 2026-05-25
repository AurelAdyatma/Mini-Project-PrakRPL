package manajemenmusik.model;

/**
 * Abstract class User — menerapkan Abstraction & Encapsulation.
 * Subclass wajib mengimplementasi getRole() (Polymorphism).
 */
public abstract class User {

    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // ---- Encapsulation: private fields + getter/setter ----

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Autentikasi user berdasarkan username dan password.
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Abstract method — Polymorphism: setiap subclass mendefinisikan perannya sendiri.
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + ": " + username;
    }
}
